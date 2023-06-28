package fuzs.netherchested.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A copy of vanilla's {@link net.minecraft.world.level.block.entity.BaseContainerBlockEntity}, which does not implement
 * {@link net.minecraft.world.Container} to be able to handle item transfers ourselves on Fabric
 * (Fabric Api always gives vanilla logic precedence when the block entity also is a container).
 */
public abstract class NamedBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private LockCode lockKey;
    private Component name;

    protected NamedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.lockKey = LockCode.NO_LOCK;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.lockKey = LockCode.fromTag(tag);
        if (tag.contains("CustomName", 8)) {
            this.name = Serializer.fromJson(tag.getString("CustomName"));
        }

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.lockKey.addToTag(tag);
        if (this.name != null) {
            tag.putString("CustomName", Serializer.toJson(this.name));
        }

    }

    public void setCustomName(Component name) {
        this.name = name;
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
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

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return this.canOpen(player) ? this.createMenu(i, inventory) : null;
    }

    protected abstract AbstractContainerMenu createMenu(int containerId, Inventory inventory);
}
