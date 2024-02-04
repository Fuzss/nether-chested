package fuzs.netherchested.data;

import fuzs.netherchested.init.ModRegistry;
import fuzs.puzzleslib.api.data.v1.AbstractRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModRegistry.NETHER_CHEST_BLOCK.get())
                .define('#', Items.NETHER_BRICKS)
                .define('@', Items.BLAZE_ROD)
                .define('+', Items.NETHER_STAR)
                .pattern("@#@")
                .pattern("#+#")
                .pattern("@#@")
                .unlockedBy(getHasName(Items.NETHER_STAR), has(Items.NETHER_STAR))
                .save(recipeConsumer);
    }
}
