package ipsis.woot.modules.squeezer;

import ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;


import javax.annotation.Nonnull;

public class SqueezerRecipes {

    public static void load(@Nonnull RecipeManager manager) {

        // Setup the valid items for slot 0
        DyeSqueezerRecipe.clearValidInputs();
        for (RecipeHolder<?> recipe : manager.getRecipes()) {
            if (recipe.value() instanceof DyeSqueezerRecipe) {
                DyeSqueezerRecipe dRecipe = (DyeSqueezerRecipe)recipe.value();
                Ingredient ingredient = dRecipe.input();
                for (ItemStack itemStack :  ingredient.getItems())
                    DyeSqueezerRecipe.addValidInput(itemStack);
            }
        }
    }
}
