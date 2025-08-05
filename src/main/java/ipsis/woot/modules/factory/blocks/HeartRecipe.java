package ipsis.woot.modules.factory.blocks;

import ipsis.woot.mod.ModNBT;
import ipsis.woot.util.FakeMob;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Currently running recipe
 */
public record HeartRecipe (int numTicks, int numUnits, List<ItemStack> recipeItems, List<FluidStack> recipeFluids){


    public static final StreamCodec<RegistryFriendlyByteBuf, HeartRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, HeartRecipe::numTicks,
            ByteBufCodecs.VAR_INT, HeartRecipe::numUnits,
            ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), HeartRecipe::recipeItems,
            ByteBufCodecs.collection(ArrayList::new, FluidStack.STREAM_CODEC), HeartRecipe::recipeFluids,
            HeartRecipe::new
    );

    public int getNumTicks() {
        return numTicks;
    }

    public int getNumUnits() {
        return numUnits;
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
