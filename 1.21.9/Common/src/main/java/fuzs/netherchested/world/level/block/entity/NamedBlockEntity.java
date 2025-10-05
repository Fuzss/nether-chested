package fuzs.netherchested.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * A copy of vanilla's {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity}, which does not implement
 * {@link net.minecraft.world.Container} to be able to handle item transfers ourselves on Fabric (Fabric Api always
 * gives vanilla logic precedence when the block entity also is a container).
 */
public abstract class NamedBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    private Component name;

    protected NamedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.lockKey = LockCode.fromTag(valueInput);
        this.name = parseCustomNameSafe(valueInput, "CustomName");
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        this.lockKey.addToTag(valueOutput);
        valueOutput.storeNullable("CustomName", ComponentSerialization.CODEC, this.name);
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    protected abstract Component getDefaultName();

    public boolean canOpen(Player player) {
        return canUnlock(player, this.lockKey, this.getDisplayName());
    }

    public static boolean canUnlock(Player player, LockCode code, Component displayName) {
        if (!player.isSpectator() && !code.unlocksWith(player.getMainHandItem())) {
            player.displayClientMessage(Component.translatable("container.isLocked", displayName), true);
            player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    protected abstract NonNullList<ItemStack> getItems();

    protected abstract void setItems(NonNullList<ItemStack> items);

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return this.canOpen(player) ? this.createMenu(i, inventory) : null;
    }

    protected abstract AbstractContainerMenu createMenu(int containerId, Inventory inventory);

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentGetter) {
        super.applyImplicitComponents(componentGetter);
        this.name = componentGetter.get(DataComponents.CUSTOM_NAME);
        this.lockKey = componentGetter.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        componentGetter.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getItems());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
        if (!this.lockKey.equals(LockCode.NO_LOCK)) {
            components.set(DataComponents.LOCK, this.lockKey);
        }

        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
    }

    @Override
    public void removeComponentsFromTag(ValueOutput valueOutput) {
        valueOutput.discard("CustomName");
        valueOutput.discard("lock");
        valueOutput.discard("Items");
    }
}
