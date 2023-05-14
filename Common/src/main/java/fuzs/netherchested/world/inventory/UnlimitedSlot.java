package fuzs.netherchested.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class UnlimitedSlot extends Slot {

    public UnlimitedSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return UnlimitedContainerUtils.getMaxStackSize(stack).orElse(super.getMaxStackSize(stack));
    }
}
