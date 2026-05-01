package fuzs.netherchested.common.data;

import fuzs.netherchested.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.data.v2.AbstractLootProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;

public class ModBlockLootProvider extends AbstractLootProvider.Blocks {

    public ModBlockLootProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addLootTables() {
        this.add(ModRegistry.NETHER_CHEST_BLOCK.value(), this::createNameableBlockEntityTable);
    }
}
