package fuzs.netherchested.common.world;

import fuzs.netherchested.common.world.inventory.LimitlessContainerUtils;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/**
 * A variant of {@link SimpleContainer} implementing {@link MultipliedContainer}.
 */
public class MultipliedSimpleContainer extends SimpleContainer implements MultipliedContainer {
    private final int stackSizeMultiplier;

    public MultipliedSimpleContainer(int stackSizeMultiplier, int size) {
        super(size);
        this.stackSizeMultiplier = stackSizeMultiplier;
    }

    public MultipliedSimpleContainer(int stackSizeMultiplier, ItemStack... items) {
        super(items);
        this.stackSizeMultiplier = stackSizeMultiplier;
    }

    @Override
    public int getStackSizeMultiplier() {
        return this.stackSizeMultiplier;
    }

    @Override
    public boolean canAddItem(ItemStack itemStack) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack itemAtIndex = this.getItem(i);
            if (itemAtIndex.isEmpty() || ItemStack.isSameItemSameComponents(itemAtIndex, itemStack)
                    && itemAtIndex.getCount() < LimitlessContainerUtils.getMaxStackSizeOrDefault(itemAtIndex,
                    this.getStackSizeMultiplier())) {
                return true;
            }
        }

        return false;
    }
}
