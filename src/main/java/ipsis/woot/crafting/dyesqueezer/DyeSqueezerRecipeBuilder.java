package ipsis.woot.crafting.dyesqueezer;

import com.google.gson.JsonObject;
import ipsis.woot.Woot;
import ipsis.woot.crafting.WootRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ICondition;


import javax.annotation.Nullable;
import java.util.function.Consumer;

public class DyeSqueezerRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final int energy;
    private final int[] dyes;

    public DyeSqueezerRecipeBuilder(Ingredient ingredient, int energy, int red, int yellow, int blue, int white) {
        this.ingredient = ingredient;
        this.energy = energy;
        this.dyes = new int[]{red,blue,yellow,white};
    }


    public static DyeSqueezerRecipeBuilder dyeSqueezerRecipe(
            Ingredient ingredient, int energy,
            int red, int yellow, int blue, int white) {

        return new DyeSqueezerRecipeBuilder(ingredient, energy, red, yellow, blue, white);
    }

    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@org.jetbrains.annotations.Nullable String s) {
        return null;
    }

    @Override
    public Item getResult() {
        return ItemStack.EMPTY.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        recipeOutput.accept(resourceLocation, new DyeSqueezerRecipe(ingredient, dyes, energy), null);

    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        ResourceLocation res = ResourceLocation.parse(dyes[0] + "_" + dyes[1] + "_" + dyes[2] + "_" + dyes[3]+ "_" + id.toLowerCase());
        save(recipeOutput, res);

    }

    public static class Result implements RecipeOutput {
        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final int energy;
        private final int red;
        private final int yellow;
        private final int blue;
        private final int white;

        private Result(ResourceLocation id, Ingredient ingredient, int energy, int[] dyes) {
            this.id = id;
            this.ingredient = ingredient;
            this.energy = energy;
            this.red = dyes[0];
            this.yellow = dyes[1];
            this.blue = dyes[2];
            this.white = dyes[3];
        }



        @Override
        public Advancement.Builder advancement() {
            return null;
        }

        @Override
        public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @org.jetbrains.annotations.Nullable AdvancementHolder advancementHolder, ICondition... iConditions) {

        }
    }

}
