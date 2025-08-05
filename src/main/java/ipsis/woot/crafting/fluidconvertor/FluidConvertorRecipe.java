package ipsis.woot.crafting.fluidconvertor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ipsis.woot.Woot;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipeBuilder.SERIALIZER;

public record FluidConvertorRecipe (Ingredient catalyst, int catalystCount, FluidStack inputFluid, FluidStack outputFluid, int energy) implements Recipe<RecipeInput> {

    public Ingredient getCatalyst() { return this.catalyst; }
    public int getCatalystCount() { return this.catalystCount; }
    public FluidStack getOutput() { return outputFluid.copy(); }
    public FluidStack getInputFluid() { return this.inputFluid; }
    public int getEnergy() { return this.energy; }

    @Override
    public String toString() {
        return "FluidConvertorRecipe{" +
                "catalyst=" + catalyst +
                ", catalystCount=" + catalystCount +
                ", inputFluid=" + inputFluid +
                ", outputFluid=" + outputFluid +
                ", energy=" + energy +
                '}';
    }



    /**
     * Valid inputs
     */
    private static List<ItemStack> validCatalysts = new ArrayList<>();
    public static void clearValidCatalysts() { validCatalysts.clear(); }
    public static void addValidCatalyst(ItemStack itemStack) { validCatalysts.add(itemStack); }
    public static boolean isValidCatalyst(ItemStack itemStack) {
        if (itemStack.isEmpty())
            return false;

        for (ItemStack i : validCatalysts) {
            if (i.is(itemStack.getItem()))
                return true;
        }
        return false;
    }

    private static List<FluidStack> validInputs = new ArrayList<>();
    public static void clearValidInputs() { validInputs.clear(); }
    public static void addValidInput(FluidStack fluidStack) { validInputs.add(fluidStack); }
    public static boolean isValidInput(FluidStack fluidStack) {
        if (fluidStack.isEmpty())
            return false;

        for (FluidStack f : validInputs) {
            if (f.is(fluidStack.getFluid()))
                return true;
        }
        return false;
    }

    /**
     * Jei
     */
    private static List<List<ItemStack>> inputs = new ArrayList<>();
    public List<List<ItemStack>> getInputs() { return inputs; }

    /**
     * IRecipe
     * Matches catalyst
     * Any fluid lookup will have to be done externally from all matching recipes
     */



    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return catalyst.test(recipeInput.getItem(0));
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
        return ItemStack.EMPTY;
    }

    

    public static class FluidConvertorRecipeType implements RecipeSerializer<FluidConvertorRecipe>, RecipeType<FluidConvertorRecipe>{

        public static final MapCodec<FluidConvertorRecipe> CODEC = RecordCodecBuilder.mapCodec((inst) -> inst.group(
                Ingredient.CODEC.fieldOf("catalyst").forGetter(FluidConvertorRecipe::catalyst),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("catalystcount").forGetter(FluidConvertorRecipe::catalystCount),
                FluidStack.CODEC.fieldOf("inputFluid").forGetter(FluidConvertorRecipe::inputFluid),
                FluidStack.CODEC.fieldOf("outputFluid").forGetter(FluidConvertorRecipe::outputFluid),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy").forGetter(FluidConvertorRecipe::energy)
                ).apply(inst, FluidConvertorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FluidConvertorRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, FluidConvertorRecipe::catalyst,
                ByteBufCodecs.VAR_INT, FluidConvertorRecipe::catalystCount,
                FluidStack.STREAM_CODEC, FluidConvertorRecipe::inputFluid,
                FluidStack.STREAM_CODEC, FluidConvertorRecipe::outputFluid,
                ByteBufCodecs.VAR_INT, FluidConvertorRecipe::energy,
                FluidConvertorRecipe::new
        );

        @Override
        public MapCodec<FluidConvertorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FluidConvertorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

}
