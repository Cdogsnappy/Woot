package ipsis.woot.crafting.fluidconvertor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record ConvertorRecipeInput(ItemStack catalyst, FluidStack inputFluid) implements RecipeInput {


    @Override
    public ItemStack getItem(int i) {
        return catalyst;
    }

    @Override
    public int size() {
        return 1;
    }

    public FluidStack getFluidStack(){
        return inputFluid;
    }
}
