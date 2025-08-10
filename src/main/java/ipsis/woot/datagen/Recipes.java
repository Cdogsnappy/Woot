package ipsis.woot.datagen;

import ipsis.woot.datagen.modules.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

// NB addCriterion is when the recipe should be unlocked - so cobblestone just means after you get cobblestone
public class Recipes extends RecipeProvider {


    public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        Anvil.registerRecipes(recipeOutput);
        ipsis.woot.datagen.modules.Factory.registerRecipes(recipeOutput);
        Generic.registerRecipes(recipeOutput);
        Infuser.registerRecipes(recipeOutput);
        Layout.registerRecipes(recipeOutput);
        Oracle.registerRecipes(recipeOutput);
        Squeezer.registerRecipes(recipeOutput);
        FluidConvertor.registerRecipes(recipeOutput);
    }

}
