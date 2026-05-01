package fuzs.netherchested.common.world.inventory;

import fuzs.netherchested.common.world.MultipliedContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MultipliedSlot extends Slot {
    private final int stackSizeMultiplier;

    public MultipliedSlot(MultipliedContainer container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.stackSizeMultiplier = container.getStackSizeMultiplier();
    }

    @Override
    public int getMaxStackSize(ItemStack itemStack) {
        return LimitlessContainerUtils.getMaxStackSize(itemStack, this.stackSizeMultiplier)
                .orElseGet(() -> super.getMaxStackSize(itemStack));
    }
}
