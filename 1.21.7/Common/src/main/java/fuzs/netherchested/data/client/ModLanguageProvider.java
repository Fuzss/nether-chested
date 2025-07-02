package fuzs.netherchested.data.client;

import fuzs.netherchested.init.ModRegistry;
import fuzs.netherchested.world.level.block.NetherChestBlock;
import fuzs.netherchested.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.add(ModRegistry.NETHER_CHEST_BLOCK.value(), "Nether Chest");
        builder.add(((NetherChestBlock) ModRegistry.NETHER_CHEST_BLOCK.value()).getDescriptionComponent(),
                "Allows for storing way more items in a single slot than any other inventory."
        );
        builder.add(NetherChestBlockEntity.CONTAINER_TITLE, "Nether Chest");
    }
}
