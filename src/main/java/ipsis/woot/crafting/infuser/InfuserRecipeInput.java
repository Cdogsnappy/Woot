package ipsis.woot.crafting.infuser;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record InfuserRecipeInput(ItemStack ing, ItemStack aug, FluidStack fluid) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return ing;
    }

    public ItemStack getAugment(){
        return aug;
    }

    public FluidStack getFluid(){
        return fluid;
    }

    @Override
    public int size() {
        return 0;
    }
}
