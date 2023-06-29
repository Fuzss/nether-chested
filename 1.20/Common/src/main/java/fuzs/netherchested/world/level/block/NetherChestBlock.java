package fuzs.netherchested.world.level.block;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.config.ServerConfig;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.network.LimitlessContainerSynchronizer;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import fuzs.netherchested.world.inventory.UnlimitedContainerUtils;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;

@SuppressWarnings("deprecation")
public class NetherChestBlock extends EnderChestBlock {
    public static final String NETHER_CHEST_DESCRIPTION_KEY = "block.netherchested.nether_chest.description";
    private static final Component DESCRIPTION_COMPONENT = Component.translatable(NETHER_CHEST_DESCRIPTION_KEY).withStyle(ChatFormatting.GOLD);

    public NetherChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            if (level.getBlockEntity(pos) instanceof NetherChestBlockEntity blockEntity) {
                blockEntity.setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof NetherChestBlockEntity blockEntity) {
                UnlimitedContainerUtils.dropContents(level, pos, blockEntity.container);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockPos above = pos.above();
            if (level.dimension() == Level.NETHER && NetherChested.CONFIG.get(ServerConfig.class).explodeInNether) {
                level.removeBlock(pos, false);
                Vec3 center = pos.getCenter();
                level.explode(null, level.damageSources().badRespawnPointExplosion(center), null, center, NetherChested.CONFIG.get(ServerConfig.class).netherExplosionStrength, true, Level.ExplosionInteraction.BLOCK);
            } else if (!NetherChested.CONFIG.get(ServerConfig.class).noBlockAbove || !level.getBlockState(above).isRedstoneConductor(level, above)) {
                MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
                if (menuProvider != null) {
                    OptionalInt containerId = player.openMenu(menuProvider);
                    if (containerId.isPresent() && player.containerMenu.containerId == containerId.getAsInt()) {
                        ((NetherChestMenu) player.containerMenu).setActualSynchronizer(new LimitlessContainerSynchronizer((ServerPlayer) player));
                    }
                    PiglinAi.angerNearbyPiglins(player, true);
                }

            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.get().create(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.get(), NetherChestBlockEntity::lidAnimateTick) : null;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof NetherChestBlockEntity blockEntity) {
            return UnlimitedContainerUtils.getRedstoneSignalFromContainer(blockEntity.container);
        }
        return 0;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof NetherChestBlockEntity blockEntity) {
            blockEntity.recheckOpen();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(DESCRIPTION_COMPONENT);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // particle code from old Nether Chest mod as they look quite nice
        if (random.nextInt(2) == 0) {
            double posX = pos.getX() + 0.5D + (0.4375D * (random.nextInt(2) * 2 - 1));
            double posY = pos.getY() + random.nextFloat();
            double posZ = pos.getZ() + 0.5D + (0.4375D * (random.nextInt(2) * 2 - 1));
            double ySpeed = random.nextFloat() * 0.015625D;
            if (random.nextInt(7) == 0) {
                level.addParticle(ParticleTypes.FLAME, posX, posY, posZ, 0, ySpeed * 4, 0);
            } else {
                level.addParticle(ParticleTypes.SMOKE, posX, posY, posZ, 0, ySpeed, 0);
            }
        }
    }
}
