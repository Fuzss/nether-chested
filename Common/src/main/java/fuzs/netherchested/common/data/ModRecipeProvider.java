package fuzs.netherchested.common.data;

import fuzs.netherchested.common.init.ModRegistry;
import fuzs.puzzleslib.common.api.data.v3.recipes.AbstractRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

public class ModRecipeProvider extends AbstractRecipeProvider {

    public ModRecipeProvider(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    public void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, ModRegistry.NETHER_CHEST_BLOCK.value())
                .define('#', Items.NETHER_BRICKS)
                .define('@', Items.NETHERITE_SCRAP)
                .define('+', Items.CHEST)
                .pattern("@#@")
                .pattern("#+#")
                .pattern("@#@")
                .unlockedBy(getHasName(Items.NETHERITE_SCRAP), this.has(Items.NETHERITE_SCRAP))
                .save(this.output);
    }
}
