package fuzs.netherchested.common.data;

import fuzs.netherchested.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.data.v3.loot.AbstractBlockLootSubProvider;
import net.minecraft.data.loot.LootTableSubProvider;

public class ModBlockLootProvider extends AbstractBlockLootSubProvider {

    public ModBlockLootProvider(LootTableSubProvider.Context context) {
        super(context);
    }

    @Override
    public void generate() {
        this.add(ModRegistry.NETHER_CHEST_BLOCK.value(), this::createNameableBlockEntityTable);
    }
}
