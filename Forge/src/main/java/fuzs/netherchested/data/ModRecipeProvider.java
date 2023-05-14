package fuzs.netherchested.data;

import fuzs.netherchested.init.ModRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder.shaped(ModRegistry.NETHER_CHEST_BLOCK.get())
                .define('#', Blocks.NETHER_BRICKS)
                .define('@', Items.BLAZE_ROD)
                .define('+', Blocks.CHEST)
                .pattern("@#@")
                .pattern("#+#")
                .pattern("@#@")
                .unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
                .save(recipeConsumer);
    }
}
