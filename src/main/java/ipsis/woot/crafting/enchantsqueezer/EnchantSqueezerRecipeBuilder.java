package ipsis.woot.crafting.enchantsqueezer;

import ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipe;
import ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipeBuilder;
import ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipeBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;

public class EnchantSqueezerRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final int energy;
    private final FluidStack output;


    public EnchantSqueezerRecipeBuilder(Ingredient ingredient,FluidStack out, int energy) {
        this.ingredient = ingredient;
        this.energy = energy;
        this.output = out;
    }


    public static EnchantSqueezerRecipeBuilder enchantSqueezerRecipe(
            Ingredient input, FluidStack output, int energy) {

        return new EnchantSqueezerRecipeBuilder(input, output, energy);
    }




    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@org.jetbrains.annotations.Nullable String s) {
        return null;
    }

    @Override
    public Item getResult() {
        return ItemStack.EMPTY.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        recipeOutput.accept(resourceLocation, new EnchantSqueezerRecipe(ingredient, output, energy), null);

    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        ResourceLocation res = ResourceLocation.parse(ingredient + id.toLowerCase());
        save(recipeOutput, res);

    }
}
