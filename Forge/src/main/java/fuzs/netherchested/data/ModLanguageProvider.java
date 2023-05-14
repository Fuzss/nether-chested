package fuzs.netherchested.data;

import fuzs.netherchested.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(DataGenerator dataGenerator, String modId) {
        super(dataGenerator, modId, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(ModRegistry.NETHER_CHEST_BLOCK.get(), "Nether Chest");
        this.add("container.nether_chest", "Nether Chest");
        this.add("block.netherchest.nether_chest.description", "Allows for storing way more items in a single slot than any other inventory.");
    }
}
