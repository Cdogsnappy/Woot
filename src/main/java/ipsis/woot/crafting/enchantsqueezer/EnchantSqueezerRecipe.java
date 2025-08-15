package ipsis.woot.crafting.enchantsqueezer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.util.helper.EnchantmentHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;



public record EnchantSqueezerRecipe(Ingredient itemStack, FluidStack output, int energy) implements Recipe<SingleRecipeInput> {





    public FluidStack getOutput() { return output; }
    public Ingredient getInput() { return itemStack; }
    public int getEnergy() { return energy; }



    @Override
    public boolean matches(SingleRecipeInput singleRecipeInput, Level level) {
        ItemStack itemStack = singleRecipeInput.item();
        return !itemStack.isEmpty() && (EnchantmentHelper.isEnchanted(itemStack) || itemStack.is(Items.ENCHANTED_BOOK));

    }

    @Override
    public ItemStack assemble(SingleRecipeInput singleRecipeInput, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WootRecipes.ENCHANT_SQUEEZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return WootRecipes.ENCHANT_SQUEEZER_TYPE.get();
    }


    public static class EnchantSqueezerRecipeType implements RecipeSerializer<EnchantSqueezerRecipe>, RecipeType<EnchantSqueezerRecipe> {

        public static final MapCodec<EnchantSqueezerRecipe> CODEC = RecordCodecBuilder.mapCodec((inst) -> inst.group(
                Ingredient.CODEC.fieldOf("item").forGetter(EnchantSqueezerRecipe::itemStack),
                FluidStack.CODEC.fieldOf("outputFluid").forGetter(EnchantSqueezerRecipe::output),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("energy").forGetter(EnchantSqueezerRecipe::energy)
        ).apply(inst, EnchantSqueezerRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantSqueezerRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, EnchantSqueezerRecipe::itemStack,
                FluidStack.STREAM_CODEC, EnchantSqueezerRecipe::output,
                ByteBufCodecs.VAR_INT, EnchantSqueezerRecipe::energy,
                EnchantSqueezerRecipe::new
        );

        @Override
        public MapCodec<EnchantSqueezerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchantSqueezerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
