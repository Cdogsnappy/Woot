package ipsis.woot.modules.anvil;

import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.anvil.AnvilRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;

public class AnvilRecipes {

    public static void load(@Nonnull RecipeManager manager) {
        // Setup the valid items for slot 0
        AnvilRecipe.clearValidInputs();
        for (RecipeHolder<?> recipe : manager.getRecipes()){
            if(recipe.value() instanceof AnvilRecipe) {
                AnvilRecipe dRecipe = (AnvilRecipe)recipe.value();
                Ingredient ingredient = dRecipe.baseItem();
                for (ItemStack itemStack : ingredient.getItems())
                    AnvilRecipe.addValidInput(itemStack);
            }
        }
    }
}
