package ipsis.woot.crafting.fluidconvertor;

import com.google.gson.JsonObject;
import ipsis.woot.Woot;
import ipsis.woot.util.FluidStackHelper;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FluidConvertorRecipeBuilder {

    private final Ingredient catalyst;
    private final int catalystCount;
    private final FluidStack inFluid;
    private final FluidStack result;
    private final int energy;

    public FluidConvertorRecipeBuilder(FluidStack result, int energy, Ingredient catalyst, int catalystCount, FluidStack inFluid) {
        this.result = result;
        this.catalyst = catalyst;
        this.catalystCount = catalystCount;
        this.energy = energy;
        this.inFluid = inFluid;
    }

    public static FluidConvertorRecipeBuilder fluidConvertorRecipe(
            FluidStack result, int energy,
            Ingredient catalyst, int catalystCount,
            FluidStack inFluid) {

        return new FluidConvertorRecipeBuilder(result, energy, catalyst, catalystCount, inFluid);
    }


    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        recipeOutput.accept(resourceLocation, new FluidConvertorRecipe(catalyst, catalystCount,
                inFluid, result, energy), null);
    }
}
