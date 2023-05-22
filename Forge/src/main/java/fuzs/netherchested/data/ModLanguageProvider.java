package fuzs.netherchested.data;

import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.NetherChestBlock;
import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(PackOutput packOutput, String modId) {
        super(packOutput, modId);
    }

    @Override
    protected void addTranslations() {
        this.add(ModRegistry.NETHER_CHEST_BLOCK.get(), "Nether Chest");
        this.add(NetherChestBlock.NETHER_CHEST_DESCRIPTION_KEY, "Allows for storing way more items in a single slot than any other inventory.");
        this.add("container.nether_chest", "Nether Chest");
    }
}
