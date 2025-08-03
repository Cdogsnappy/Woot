package ipsis.woot.crafting.factory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ipsis.woot.Woot;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.anvil.AnvilRecipe;
import ipsis.woot.util.FakeMob;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;


import java.util.function.Consumer;

public class FactoryRecipeBuilder implements RecipeBuilder {

    private final FakeMob result;
    private final NonNullList<ItemStack> items;
    private final NonNullList<FluidStack> fluids;
    private final NonNullList<FactoryRecipe.Drop> drops;

    public FactoryRecipeBuilder(FakeMob result) {
        this.result = result;
        this.items = NonNullList.create();
        this.fluids = NonNullList.create();
        this.drops = NonNullList.create();
    }

    public static FactoryRecipeBuilder factoryRecipe(FakeMob result) {
        return new FactoryRecipeBuilder(result);
    }

    public FactoryRecipeBuilder addIngredient(ItemStack itemStack) {
        this.items.add(itemStack);
        return this;
    }

    public FactoryRecipeBuilder addIngredient(FluidStack fluidStack) {
        this.fluids.add(fluidStack);
        return this;
    }

    public FactoryRecipeBuilder addDrop(FactoryRecipe.Drop drop) {
        this.drops.add(drop);
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String s) {
        return null;
    }

    @Override
    public Item getResult() {
        return null;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        recipeOutput.accept(resourceLocation, new FactoryRecipe(items,fluids,result, drops), null);

    }




}
