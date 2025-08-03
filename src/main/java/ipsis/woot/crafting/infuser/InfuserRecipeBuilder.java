package ipsis.woot.crafting.infuser;

import com.google.gson.JsonObject;
import ipsis.woot.Woot;
import ipsis.woot.util.FluidStackHelper;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;


import javax.annotation.Nullable;
import java.util.function.Consumer;

public class InfuserRecipeBuilder {

    private final Ingredient ingredient;
    private final Ingredient augment;
    private final int augmentCount;
    private final FluidStack fluid;
    private final ItemStack result;
    private final int energy;

    public InfuserRecipeBuilder(ItemStack result, Ingredient ingredient, Ingredient augment, int augmentCount, FluidStack fluidStack, int energy) {
        this.ingredient = ingredient;
        this.augment = augment;
        this.augmentCount = augmentCount;
        this.fluid = fluidStack;
        this.result = result;
        this.energy = energy;
    }

    public static InfuserRecipeBuilder infuserRecipe(ItemStack result, Ingredient ingredient, Ingredient augment, int augmentCount, FluidStack fluidStack, int energy) {
        return new InfuserRecipeBuilder(result, ingredient, augment, augmentCount, fluidStack, energy);
    }


    public void save(RecipeOutput recipeOutput, String name) {
        recipeOutput.accept(ResourceLocation.fromNamespaceAndPath(Woot.MODID, "infuser/" + name),
                 new InfuserRecipe(
                 this.ingredient,
                this.augment,
                this.augmentCount,
                this.fluid,
                this.result,
                this.energy
                 ), null
        );
    }

}
