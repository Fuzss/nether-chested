package fuzs.netherchested.world.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class LimitlessContainer extends SimpleContainer {
    private final int stackSizeMultiplier;

    public LimitlessContainer(int stackSizeMultiplier, int size) {
        super(size);
        this.stackSizeMultiplier = stackSizeMultiplier;
    }

    public LimitlessContainer(int stackSizeMultiplier, ItemStack... items) {
        super(items);
        this.stackSizeMultiplier = stackSizeMultiplier;
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize() * this.stackSizeMultiplier;
    }
}
