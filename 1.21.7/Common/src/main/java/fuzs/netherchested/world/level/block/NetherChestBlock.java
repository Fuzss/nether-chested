package fuzs.netherchested.world.level.block;

import com.mojang.serialization.MapCodec;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.config.ServerConfig;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.block.v1.entity.TickingEntityBlock;
import fuzs.puzzleslib.api.util.v1.InteractionResultHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NetherChestBlock extends EnderChestBlock implements TickingEntityBlock<NetherChestBlockEntity> {
    public static final MapCodec<EnderChestBlock> CODEC = simpleCodec(NetherChestBlock::new);

    public NetherChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<EnderChestBlock> codec() {
        return CODEC;
    }

    public Component getDescriptionComponent() {
        return Component.translatable(this.getDescriptionId() + ".description").withStyle(ChatFormatting.GOLD);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, boolean movedByPiston) {
        Containers.updateNeighboursAfterDestroy(blockState, serverLevel, blockPos);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof NetherChestBlockEntity blockEntity) {
            return LimitlessContainerUtils.getRedstoneSignalFromContainer(blockEntity.getContainer());
        } else {
            return 0;
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockState(pos.above()).isRedstoneConductor(level, pos.above())) {
            return InteractionResultHelper.sidedSuccess(level.isClientSide);
        } else if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (level.dimension() == Level.NETHER &&
                    NetherChested.CONFIG.get(ServerConfig.class).netherExplosionStrength > 0) {
                level.removeBlock(pos, false);
                level.explode(null,
                        level.damageSources().badRespawnPointExplosion(pos.getCenter()),
                        null,
                        pos.getCenter(),
                        NetherChested.CONFIG.get(ServerConfig.class).netherExplosionStrength,
                        true,
                        Level.ExplosionInteraction.BLOCK);
            } else {
                MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
                if (menuProvider != null) {
                    player.openMenu(menuProvider);
                    PiglinAi.angerNearbyPiglins((ServerLevel) level, player, true);
                }
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TickingEntityBlock.super.newBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return TickingEntityBlock.super.getTicker(level, state, blockEntityType);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.hasProperty(WATERLOGGED) || !state.getValue(WATERLOGGED)) {
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

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof NetherChestBlockEntity blockEntity) {
            blockEntity.recheckOpen();
        }
    }

    @Override
    public BlockEntityType<? extends NetherChestBlockEntity> getBlockEntityType() {
        return ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.value();
    }
}
