package ipsis.woot.crafting.anvil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ipsis.woot.Woot;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;


import java.util.function.Consumer;

public class AnvilRecipeBuilder implements RecipeBuilder{

    private final Ingredient baseIngredient;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;

    public AnvilRecipeBuilder(ItemStack result, Ingredient baseIngredient) {
        this.baseIngredient = baseIngredient;
        this.result = result;
        this.ingredients = NonNullList.create();
    }

    public static AnvilRecipeBuilder anvilRecipe(ItemStack result, Ingredient baseIngredient) {
        return new AnvilRecipeBuilder(result, baseIngredient);
    }

    public AnvilRecipeBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public void build(Consumer<CraftingRecipe> recipe, String name) {
        recipe.accept(new AnvilRecipeBuilder.Result(
                ResourceLocation.fromNamespaceAndPath(Woot.MODID, "anvil/" + name),
                this.baseIngredient,
                this.result,
                this.ingredients
        ));
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

    }

    public static class Result implements CraftingRecipe {

        private final NonNullList<Ingredient> ingredients;
        private final Ingredient baseIngredient;
        private final ItemStack result;
        private final ResourceLocation id;

        public Result(ResourceLocation id, Ingredient baseIngredient, ItemStack result, NonNullList<Ingredient> ingredients) {
            this.id = id;
            this.baseIngredient = baseIngredient;
            this.result = result;
            this.ingredients = ingredients;
        }

        @Override
        public void serialize(JsonObject json) {
            json.add("base", this.baseIngredient.serialize());
            JsonArray array = new JsonArray();
            for (Ingredient i : this.ingredients)
                array.add(i.serialize());
            json.add("ingredients", array);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1)
                jsonObject.addProperty("count", this.count);
            json.add("result", jsonObject);
        }

        @Override
        public CraftingBookCategory category() {
            return null;
        }

        @Override
        public boolean matches(CraftingInput craftingInput, Level level) {
            return false;
        }

        @Override
        public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
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
            return SERIALIZER;
        }
    }

    @ObjectHolder("woot:anvil")
    public static final RecipeSerializer<Recipe<?>> SERIALIZER = null;
}
