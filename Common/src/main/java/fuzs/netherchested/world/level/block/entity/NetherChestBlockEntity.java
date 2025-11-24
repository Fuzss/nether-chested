package fuzs.netherchested.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedContainer;
import fuzs.netherchested.NetherChested;
import fuzs.netherchested.config.ServerConfig;
import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.inventory.NetherChestMenu;
import fuzs.puzzleslib.api.block.v1.entity.TickingBlockEntity;
import fuzs.puzzleslib.api.container.v1.ContainerMenuHelper;
import fuzs.puzzleslib.api.container.v1.ListBackedContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mostly a reimplementation of vanilla's {@link net.minecraft.world.level.block.entity.ChestBlockEntity}, which does
 * not implement {@link net.minecraft.world.Container} to be able to handle item transfers ourselves on Fabric.
 */
public class NetherChestBlockEntity extends NamedBlockEntity implements LidBlockEntity, TickingBlockEntity {
    /**
     * @see ItemStack#CODEC
     */
    public static final MapCodec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
            ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                    .forGetter(ItemStack::getComponentsPatch)).apply(instance, ItemStack::new));
    /**
     * @see ItemContainerContents.Slot#CODEC
     */
    public static final Codec<ItemContainerContents.Slot> ITEM_CONTAINER_CONTENTS_SLOT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.intRange(0, 255)
                                    .fieldOf("slot")
                                    .forGetter(ItemContainerContents.Slot::index),
                            ITEM_STACK_CODEC.fieldOf("item").forGetter(ItemContainerContents.Slot::item))
                    .apply(instance, ItemContainerContents.Slot::new));
    /**
     * @see ItemContainerContents#CODEC
     */
    public static final Codec<ItemContainerContents> ITEM_CONTAINER_CONTENTS_CODEC = ITEM_CONTAINER_CONTENTS_SLOT_CODEC.sizeLimitedListOf(
            256).xmap(ItemContainerContents::fromSlots, ItemContainerContents::asSlots);
    public static final MutableComponent CONTAINER_TITLE = Component.translatable("container.nether_chest");
    public static final int CONTAINER_SIZE = 54;

    private final ContainerOpenersCounter openersCounter = new NetherChestOpenersCounter();
    private final ChestLidController chestLidController = new ChestLidController();
    private final MultipliedContainer container = new NetherChestContainer();
    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    public NetherChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModRegistry.NETHER_CHEST_BLOCK_ENTITY_TYPE.value(), blockPos, blockState);
    }

    @Override
    public void clientTick() {
        this.chestLidController.tickLid();
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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        LimitlessContainerUtils.loadAllItems(tag, this.items, registries);

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        LimitlessContainerUtils.saveAllItems(tag, this.items, true, registries);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.getItems().clear();
        componentInput.getOrDefault(ModRegistry.UNLIMITED_CONTAINER_DATA_COMPONENT_TYPE.value(),
                ItemContainerContents.EMPTY).copyInto(this.getItems());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, null);
        components.set(ModRegistry.UNLIMITED_CONTAINER_DATA_COMPONENT_TYPE.value(),
                ItemContainerContents.fromItems(this.getItems()));
    }

    @Override
    protected Component getDefaultName() {
        return CONTAINER_TITLE;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        ContainerMenuHelper.copyItemsIntoList(items, this.items);
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

    public MultipliedContainer getContainer() {
        return this.container;
    }

    private class NetherChestContainer implements ListBackedContainer, MultipliedContainer {

        @Override
        public NonNullList<ItemStack> getContainerItems() {
            return NetherChestBlockEntity.this.items;
        }

        @Override
        public void setChanged() {
            NetherChestBlockEntity.this.setChanged();
        }

        @Override
        public void startOpen(Player player) {
            if (!NetherChestBlockEntity.this.remove && !player.isSpectator()) {
                NetherChestBlockEntity.this.openersCounter.incrementOpeners(player,
                        NetherChestBlockEntity.this.getLevel(),
                        NetherChestBlockEntity.this.getBlockPos(),
                        NetherChestBlockEntity.this.getBlockState());
            }
        }

        @Override
        public void stopOpen(Player player) {
            if (!NetherChestBlockEntity.this.remove && !player.isSpectator()) {
                NetherChestBlockEntity.this.openersCounter.decrementOpeners(player,
                        NetherChestBlockEntity.this.getLevel(),
                        NetherChestBlockEntity.this.getBlockPos(),
                        NetherChestBlockEntity.this.getBlockState());
            }
        }

        @Override
        public int getStackSizeMultiplier() {
            return NetherChested.CONFIG.get(ServerConfig.class).stackSizeMultiplier;
        }
    }

    private class NetherChestOpenersCounter extends ContainerOpenersCounter {

        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            level.playSound(null,
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    SoundEvents.ENDER_CHEST_OPEN,
                    SoundSource.BLOCKS,
                    0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            level.playSound(null,
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    SoundEvents.ENDER_CHEST_CLOSE,
                    SoundSource.BLOCKS,
                    0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
            level.blockEvent(pos, state.getBlock(), 1, openCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof NetherChestMenu netherChestMenu && netherChestMenu.is(
                    NetherChestBlockEntity.this.container);
        }
    }
}
