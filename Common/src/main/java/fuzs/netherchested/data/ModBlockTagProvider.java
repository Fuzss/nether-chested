package fuzs.netherchested.data;

import fuzs.netherchested.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

public class ModBlockTagProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.GUARDED_BY_PIGLINS).add(ModRegistry.NETHER_CHEST_BLOCK.value());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModRegistry.NETHER_CHEST_BLOCK.value());
    }
}
