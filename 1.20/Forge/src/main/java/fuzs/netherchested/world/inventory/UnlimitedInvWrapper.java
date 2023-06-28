package fuzs.netherchested.world.inventory;

import fuzs.netherchested.world.inventory.UnlimitedContainerUtils;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class UnlimitedInvWrapper extends InvWrapper {

    public UnlimitedInvWrapper(Container inv) {
        super(inv);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack stackInSlot = this.getInv().getItem(slot);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= Math.min(UnlimitedContainerUtils.getMaxStackSize(stackInSlot), this.getSlotLimit(slot)))
                return stack;

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) return stack;

            if (!this.getInv().canPlaceItem(slot, stack)) return stack;

            m = Math.min(UnlimitedContainerUtils.getMaxStackSize(stack), this.getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    this.getInv().setItem(slot, copy);
                    this.getInv().setChanged();
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    this.getInv().setItem(slot, copy);
                    this.getInv().setChanged();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            if (!this.getInv().canPlaceItem(slot, stack)) return stack;

            m = Math.min(UnlimitedContainerUtils.getMaxStackSize(stack), this.getSlotLimit(slot));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    this.getInv().setItem(slot, stack.split(m));
                    this.getInv().setChanged();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate) {
                    this.getInv().setItem(slot, stack);
                    this.getInv().setChanged();
                }
                return ItemStack.EMPTY;
            }
        }
    }
}
