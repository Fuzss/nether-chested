package fuzs.netherchested.world.inventory;

import net.minecraft.world.Container;

public interface MultipliedContainer extends Container {

    @Override
    default int getMaxStackSize() {
        return Container.super.getMaxStackSize() * this.getStackSizeMultiplier();
    }

    int getStackSizeMultiplier();
}
