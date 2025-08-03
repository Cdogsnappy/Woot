package ipsis.woot.crafting.factory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ipsis.woot.Woot;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipe;
import ipsis.woot.util.FakeMob;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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


public record FactoryRecipe(List<ItemStack> items, List<FluidStack> fluids, FakeMob fakeMob, List<Drop> drops) implements Recipe<RecipeInput> {




    public FakeMob getFakeMob() { return this.fakeMob; }
    public List<ItemStack> getItems() { return items; }
    public List<FluidStack> getFluids() { return this.fluids; }
    public List<Drop> getDrops() { return this.drops; }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
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
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WootRecipes.FACTORY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return WootRecipes.FACTORY_RECIPE_TYPE.get();
    }

    public static record Drop(ItemStack itemStack, List<Integer> stackSizes, List<Float> dropChance) {

        public static final Codec<Drop> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(inst ->
                inst.group(ItemStack.CODEC.fieldOf("itemstack").forGetter(Drop::itemStack),
                        ExtraCodecs.POSITIVE_INT.listOf().fieldOf("stacksizes").forGetter(Drop::stackSizes),
                        ExtraCodecs.POSITIVE_FLOAT.listOf().fieldOf("dropchance").forGetter(Drop::dropChance)).apply(inst, Drop::new)
        ));

        public static final StreamCodec<RegistryFriendlyByteBuf, Drop> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, Drop::itemStack,
                ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_INT), Drop::stackSizes,
                ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.FLOAT), Drop::dropChance,
                Drop::new);





    }

    public static class FactoryRecipeType implements RecipeType<FactoryRecipe>, RecipeSerializer<FactoryRecipe> {


        public static final MapCodec<FactoryRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                ItemStack.CODEC.listOf().fieldOf("ingredient").forGetter(FactoryRecipe::items),
                FluidStack.CODEC.listOf().fieldOf("fluids").forGetter(FactoryRecipe::fluids),
                FakeMob.CODEC.fieldOf("fakeMob").forGetter(FactoryRecipe::fakeMob),
                Drop.CODEC.listOf().fieldOf("drop").forGetter(FactoryRecipe::drops)

        ).apply(inst, FactoryRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FactoryRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC), FactoryRecipe::items,
                ByteBufCodecs.collection(ArrayList::new, FluidStack.STREAM_CODEC), FactoryRecipe::fluids,
                FakeMob.STREAM_CODEC, FactoryRecipe::fakeMob,
                ByteBufCodecs.collection(ArrayList::new, Drop.STREAM_CODEC), FactoryRecipe::drops,
                FactoryRecipe::new
        );

        @Override
        public MapCodec<FactoryRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FactoryRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }
}
