package ipsis.woot.modules.anvil;

import ipsis.woot.crafting.anvil.AnvilRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;

public class AnvilRecipes {

    public static void load(@Nonnull RecipeManager manager) {
        // Setup the valid items for slot 0
        AnvilRecipe.clearValidInputs();
        for (Recipe recipe : manager.getRecipes()) {
            if (recipe instanceof AnvilRecipe) {
                AnvilRecipe dRecipe = (AnvilRecipe) recipe;
                Ingredient ingredient = dRecipe.getBaseIngredient();
                for (ItemStack itemStack :  ingredient.getMatchingStacks())
                    AnvilRecipe.addValidInput(itemStack);
            }
        }
    }
}
