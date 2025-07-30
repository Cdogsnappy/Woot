package ipsis.woot.modules.factory.blocks;

import ipsis.woot.util.FakeMob;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Currently running recipe
 */
public class HeartRecipe {

    int numTicks;
    int numUnits;
    public List<ItemStack> recipeItems = new ArrayList<>();
    public List<FluidStack> recipeFluids = new ArrayList<>();

    public int getNumTicks() {
        return numTicks;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public HeartRecipe() {
        numTicks = 1;
        numUnits = 1;
    }

    public HeartRecipe(int numTicks, int numUnits) {
        this.numTicks = Math.clamp(numTicks, 1, Integer.MAX_VALUE);
        this.numUnits = Math.clamp(numUnits, 1, Integer.MAX_VALUE);
    }

    public void addItem(ItemStack itemStack) {
        boolean added = false;
        for (ItemStack currStack : recipeItems) {
            if (currStack.is(itemStack.getItem())) {
                currStack.setCount(currStack.getCount() + itemStack.getCount());
                added = true;
            }
        }

        if (!added) {
            ItemStack addStack = itemStack.copy();
            recipeItems.add(addStack);
        }
    }

    public void addFluid(FluidStack fluidStack) {
        boolean added = false;
        for (FluidStack currStack : recipeFluids) {
            if (currStack.is(fluidStack.getFluid())) {
                currStack.setAmount(currStack.getAmount() + fluidStack.getAmount());
                added = true;
            }

        }

        if (!added) {
            FluidStack addStack = fluidStack.copy();
            recipeFluids.add(addStack);
        }
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "numTicks=" + numTicks +
                ", numUnits=" + numUnits +
                ", items=" + recipeItems.size() +
                ", fluids=" + recipeFluids.size() +
                '}';
    }
}
