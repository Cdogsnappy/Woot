package ipsis.woot.crafting.anvil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ipsis.woot.Woot;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.items.MobShardItem;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ipsis.woot.crafting.anvil.AnvilRecipeBuilder.SERIALIZER;

public record AnvilRecipe(Ingredient baseItem, ItemStack output) implements Recipe<AnvilRecipeInput> {


    public NonNullList<Ingredient> getIngredients(){
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(baseItem);
        return list;
    }

    @Override
    public boolean matches(AnvilRecipeInput anvilRecipeInput, Level level) {
        if(level.isClientSide){
            return false;
        }

        return baseItem.test(anvilRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(AnvilRecipeInput anvilRecipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    public static class Serializer implements RecipeSerializer<AnvilRecipe> {
        public static final MapCodec<AnvilRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(AnvilRecipe::baseItem),
                ItemStack.CODEC.fieldOf("result").forGetter(AnvilRecipe::output)
        ).apply(inst, AnvilRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AnvilRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, AnvilRecipe::baseItem,
                        ItemStack.STREAM_CODEC, AnvilRecipe::output,
                        AnvilRecipe::new);
        @Override
        public MapCodec<AnvilRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AnvilRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
