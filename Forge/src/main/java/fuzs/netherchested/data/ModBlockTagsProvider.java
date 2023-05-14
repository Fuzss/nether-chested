package fuzs.netherchested.data;

import fuzs.netherchested.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(DataGenerator dataGenerator, String modId, @Nullable ExistingFileHelper fileHelper) {
        super(dataGenerator, modId, fileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(BlockTags.GUARDED_BY_PIGLINS).add(ModRegistry.NETHER_CHEST_BLOCK.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModRegistry.NETHER_CHEST_BLOCK.get());
    }
}
