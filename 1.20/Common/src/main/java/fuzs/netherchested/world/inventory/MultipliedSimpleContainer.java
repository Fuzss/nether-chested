package fuzs.netherchested.world.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

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
}
