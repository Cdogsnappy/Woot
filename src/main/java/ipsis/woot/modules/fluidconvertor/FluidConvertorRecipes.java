package ipsis.woot.modules.fluidconvertor;

import ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;


import javax.annotation.Nonnull;
import java.util.ArrayList;

public class FluidConvertorRecipes {

    private static ArrayList<FluidConvertorRecipe> recipes = new ArrayList<>();

    public static void load(@Nonnull RecipeManager manager) {

        FluidConvertorRecipe.clearValidInputs();
        FluidConvertorRecipe.clearValidCatalysts();
        for (RecipeHolder<?> recipe : manager.getRecipes()) {
            if (recipe.value() instanceof FluidConvertorRecipe) {
                FluidConvertorRecipe dRecipe = (FluidConvertorRecipe)recipe.value();
                for (ItemStack itemStack : dRecipe.getCatalyst().getItems())
                    FluidConvertorRecipe.addValidCatalyst(itemStack);

                FluidConvertorRecipe.addValidInput(dRecipe.getInputFluid());
            }
        }
    }
}
