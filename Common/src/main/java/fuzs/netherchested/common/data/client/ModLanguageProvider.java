package fuzs.netherchested.common.data.client;

import fuzs.netherchested.common.init.ModRegistry;
import fuzs.netherchested.common.world.level.block.NetherChestBlock;
import fuzs.netherchested.common.world.level.block.entity.NetherChestBlockEntity;
import fuzs.puzzleslib.common.api.client.data.v3.language.AbstractLanguageProvider;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations() {
        this.add(ModRegistry.NETHER_CHEST_BLOCK.value(), "Nether Chest");
        this.add(((NetherChestBlock) ModRegistry.NETHER_CHEST_BLOCK.value()).getDescriptionComponent(),
                "Allows for storing way more items in a single slot than any other inventory."
        );
        this.add(NetherChestBlockEntity.CONTAINER_TITLE, "Nether Chest");
    }
}
