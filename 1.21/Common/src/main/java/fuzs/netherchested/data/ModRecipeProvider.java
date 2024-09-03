package fuzs.netherchested.data;

import fuzs.netherchested.init.ModRegistry;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModRegistry.NETHER_CHEST_BLOCK.value())
                .define('#', Items.NETHER_BRICKS)
                .define('@', Items.NETHERITE_SCRAP)
                .define('+', Items.CHEST)
                .pattern("@#@")
                .pattern("#+#")
                .pattern("@#@")
                .unlockedBy(getHasName(Items.NETHERITE_SCRAP), has(Items.NETHERITE_SCRAP))
                .save(recipeOutput);
    }
}
