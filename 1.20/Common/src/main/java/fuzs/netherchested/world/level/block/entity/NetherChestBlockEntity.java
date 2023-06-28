package fuzs.netherchested.world.level.block.entity;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.config.ServerConfig;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import fuzs.netherchested.world.inventory.UnlimitedContainerUtils;
import fuzs.puzzleslib.api.container.v1.ContainerImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mostly a reimplementation of vanilla's {@link net.minecraft.world.level.block.entity.ChestBlockEntity}, which does not implement
 * {@link net.minecraft.world.Container} to be able to handle item transfers ourselves on Fabric.
 */
public class NetherChestBlockEntity extends NamedBlockEntity implements LidBlockEntity {
    private static final int CONTAINER_SIZE = 54;
    private static final MutableComponent CONTAINER_TITLE = Component.translatable("container.nether_chest");

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {

        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
            level.blockEvent(pos, state.getBlock(), 1, openCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof NetherChestMenu netherChestMenu) {
                return netherChestMenu.getContainer() == NetherChestBlockEntity.this.container;
            } else {
                return false;
            }
        }
    };
    private final ChestLidController chestLidController = new ChestLidController();
    public final Container container = new ContainerImpl() {

        @Override
        public NonNullList<ItemStack> getItems() {
            return NetherChestBlockEntity.this.items;
        }

        @Override
        public void startOpen(Player player) {
            if (!NetherChestBlockEntity.this.remove && !player.isSpectator()) {
                NetherChestBlockEntity.this.openersCounter.incrementOpeners(player, NetherChestBlockEntity.this.getLevel(), NetherChestBlockEntity.this.getBlockPos(), NetherChestBlockEntity.this.getBlockState());
            }
        }

        @Override
        public void stopOpen(Player player) {
            if (!NetherChestBlockEntity.this.remove && !player.isSpectator()) {
                NetherChestBlockEntity.this.openersCounter.decrementOpeners(player, NetherChestBlockEntity.this.getLevel(), NetherChestBlockEntity.this.getBlockPos(), NetherChestBlockEntity.this.getBlockState());
            }
        }

        @Override
        public int getMaxStackSize() {
            return ContainerImpl.super.getMaxStackSize() * NetherChested.CONFIG.get(ServerConfig.class).stackSizeMultiplier;
        }

        @Override
        public void setChanged() {
            NetherChestBlockEntity.this.setChanged();
        }
    };
    private NonNullList<ItemStack> items;

    public NetherChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.get(), blockPos, blockState);
        this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    }

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, NetherChestBlockEntity blockEntity) {
        blockEntity.chestLidController.tickLid();
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.chestLidController.shouldBeOpen(type > 0);
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    @Override
    protected Component getDefaultName() {
        return CONTAINER_TITLE;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        UnlimitedContainerUtils.loadAllItems(tag, this.items);

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.remove("Items");
        UnlimitedContainerUtils.saveAllItems(tag, this.items, true);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new NetherChestMenu(containerId, inventory, this.container);
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
}
