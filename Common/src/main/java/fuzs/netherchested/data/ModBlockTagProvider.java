package fuzs.netherchested.data;

import fuzs.netherchested.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class ModBlockTagProvider extends AbstractTagProvider<Block> {

    public ModBlockTagProvider(DataProviderContext context) {
        super(Registries.BLOCK, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.GUARDED_BY_PIGLINS).add(ModRegistry.NETHER_CHEST_BLOCK.value());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModRegistry.NETHER_CHEST_BLOCK.value());
    }
}
