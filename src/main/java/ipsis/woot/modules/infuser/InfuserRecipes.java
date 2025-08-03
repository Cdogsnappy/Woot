package ipsis.woot.modules.infuser;

import ipsis.woot.crafting.infuser.InfuserRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;

public class InfuserRecipes {

    public static void load(@Nonnull RecipeManager manager) {

        // Setup the valid items for slot 0,1
        InfuserRecipe.clearValidInputs();
        InfuserRecipe.clearValidAugments();
        InfuserRecipe.clearValidFluids();
        for (RecipeHolder<?> recipe : manager.getRecipes()) {
            if (recipe.value() instanceof InfuserRecipe) {
                InfuserRecipe dRecipe = (InfuserRecipe) recipe.value();
                for (ItemStack itemStack : dRecipe.getIngredient().getItems())
                    InfuserRecipe.addValidInput(itemStack);

                if (dRecipe.hasAugment()) {
                    for (ItemStack itemStack : dRecipe.getAugment().getItems())
                        InfuserRecipe.addValidAugment(itemStack);
                }

                InfuserRecipe.addValidFluid(dRecipe.getFluidInput());
            }
        }
    }
}
