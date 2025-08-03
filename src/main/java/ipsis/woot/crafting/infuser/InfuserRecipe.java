package ipsis.woot.crafting.infuser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ipsis.woot.Woot;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.factory.FactoryRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public record InfuserRecipe(Ingredient ingredient, Ingredient augment, int augmentCount, FluidStack fluid, ItemStack result, int energy) implements Recipe<RecipeInput> {


    public Ingredient getIngredient() { return this.ingredient; }
    public boolean hasAugment() { return this.augment != Ingredient.EMPTY; }
    public Ingredient getAugment() { return this.augment; }
    public int getAugmentCount() { return this.augmentCount; }
    public ItemStack getOutput() {return this.result; }
    public FluidStack getFluidInput() { return this.fluid; }
    public int getEnergy() { return this.energy; }


    /**
     * Jei
     */
    private static List<List<ItemStack>> inputs = new ArrayList<>();
    public List<List<ItemStack>> getInputs() { return inputs; }

    /**
     * Valid inputs
     */

    private static List<ItemStack> validInputs = new ArrayList<>();
    public static void clearValidInputs() { validInputs.clear(); }
    public static void addValidInput(ItemStack itemStack) { validInputs.add(itemStack); }
    public static boolean isValidInput(ItemStack itemStack) {
        for (ItemStack i : validInputs) {
            if (i.is(itemStack.getItem()))
                return true;
        }
        return false;
    }

    private static List<ItemStack> validAugments = new ArrayList<>();
    public static void clearValidAugments() { validAugments.clear(); }
    public static void addValidAugment(ItemStack itemStack) { validAugments.add(itemStack); }
    public static boolean isValidAugment(ItemStack itemStack) {
        for (ItemStack i : validAugments) {
            if (i.is(itemStack.getItem()))
                return true;
        }
        return false;
    }

    private static List<FluidStack> validFluids = new ArrayList<>();
    public static void clearValidFluids() { validFluids.clear(); }
    public static void addValidFluid(FluidStack fluidStack) { validFluids.add(fluidStack); }
    public static boolean isValidFluid(FluidStack fluidStack) {
        for (FluidStack f : validFluids) {
            if (f.is(fluidStack.getFluid()))
                return true;
        }

        return false;
    }

    /**
     * IRecipe
     * Matches ingredient and optional augment
     * Any fluid lookup will have to be done externally from all matching recipes
     */

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        if (!ingredient.test(recipeInput.getItem(0)))
            return false;

        if (augment != Ingredient.EMPTY) {
            ItemStack invStack = recipeInput.getItem(1);
            // augment count must be exact
            if (!augment.test(invStack) || augmentCount > invStack.getCount())
                return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WootRecipes.INFUSER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return WootRecipes.INFUSER_TYPE.get();
    }


    public static class InfuserRecipeType implements RecipeType<InfuserRecipe>, RecipeSerializer<InfuserRecipe>{

        public static final MapCodec<InfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(InfuserRecipe::ingredient),
                Ingredient.CODEC.fieldOf("augment").forGetter(InfuserRecipe::augment),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("augmentcount").forGetter(InfuserRecipe::augmentCount),
                FluidStack.CODEC.fieldOf("fluidstack").forGetter(InfuserRecipe::fluid),
                ItemStack.CODEC.fieldOf("result").forGetter(InfuserRecipe::result),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy").forGetter(InfuserRecipe::energy)
        ).apply(inst, InfuserRecipe::new));


        public static final StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, InfuserRecipe::ingredient,
                Ingredient.CONTENTS_STREAM_CODEC, InfuserRecipe::augment,
                ByteBufCodecs.VAR_INT, InfuserRecipe::augmentCount,
                FluidStack.STREAM_CODEC, InfuserRecipe::fluid,
                ItemStack.STREAM_CODEC, InfuserRecipe::result,
                ByteBufCodecs.VAR_INT, InfuserRecipe::energy,
                InfuserRecipe::new
        );


        @Override
        public MapCodec<InfuserRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
