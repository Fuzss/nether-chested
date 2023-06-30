package fuzs.netherchested.world.inventory;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MultipliedSlot extends Slot {
    private final int stackSizeMultiplier;

    public MultipliedSlot(MultipliedContainer container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.stackSizeMultiplier = container.getStackSizeMultiplier();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return LimitlessContainerUtils.getMaxStackSize(stack, this.stackSizeMultiplier).orElseGet(() -> super.getMaxStackSize(stack));
    }
}
