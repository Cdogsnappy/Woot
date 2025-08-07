package ipsis.woot.crafting.dyesqueezer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ipsis.woot.Woot;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.modules.squeezer.DyeMakeup;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public record DyeSqueezerRecipe(Ingredient input, int[] dyes, int energy) implements Recipe<RecipeInput> {




    public FluidStack getOutput() { return new FluidStack(FluidSetup.PUREDYE_FLUID.get(), DyeMakeup.LCM); }

    public int getRed(){
        return dyes[0];
    }
    public int getBlue(){
        return dyes[1];
    }
    public int getYellow(){
        return dyes[2];
    }
    public int getWhite(){
        return dyes[3];
    }


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

    /**
     * Jei
     */
    public List<List<ItemStack>> getInputs() {
        List<ItemStack> list = Arrays.asList(input.getItems());
        ArrayList<List<ItemStack>> arr = new ArrayList<>();
        arr.add(list);
        return arr;
    }


    /**
     * IRecipe
     */


    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return this.input.test(recipeInput.getItem(0));
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WootRecipes.DYE_SQUEEZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return WootRecipes.DYE_SQUEEZER_TYPE.get();
    }


    public static class DyeSqueezerRecipeType implements RecipeType<DyeSqueezerRecipe>, RecipeSerializer<DyeSqueezerRecipe> {


        public static final MapCodec<DyeSqueezerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(DyeSqueezerRecipe::input),
                Codec.INT.listOf().xmap(list -> list.stream().mapToInt(Integer::intValue).toArray(),
                        array -> Arrays.stream(array).boxed().toList()).fieldOf("dyes").forGetter(DyeSqueezerRecipe::dyes),
                Codec.INT.fieldOf("energy").forGetter(DyeSqueezerRecipe::energy)
        ).apply(inst, DyeSqueezerRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DyeSqueezerRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, DyeSqueezerRecipe::input,
                ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_INT)
                        .map(
                                list -> list.stream().mapToInt(Integer::intValue).toArray(), // List<Integer> to int[]
                                array -> (ArrayList<Integer>) Arrays.stream(array).boxed().toList() // int[] to List<Integer>
                        ), DyeSqueezerRecipe::dyes,
                ByteBufCodecs.VAR_INT, DyeSqueezerRecipe::energy,
                DyeSqueezerRecipe::new
        );

        @Override
        public MapCodec<DyeSqueezerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DyeSqueezerRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }

}
