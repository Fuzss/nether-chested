package fuzs.netherchested.world.inventory;

import fuzs.netherchested.NetherChested;
import fuzs.netherchested.config.ServerConfig;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class UnlimitedContainer extends SimpleContainer {

    public UnlimitedContainer(int size) {
        super(size);
    }

    public UnlimitedContainer(ItemStack... itemStacks) {
        super(itemStacks);
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize() * NetherChested.CONFIG.get(ServerConfig.class).stackSizeMultiplier;
    }
}
