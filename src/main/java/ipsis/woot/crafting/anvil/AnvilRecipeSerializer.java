package ipsis.woot.crafting.anvil;

import com.google.gson.JsonArray;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import ipsis.woot.Woot;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;


import javax.annotation.Nullable;

import static ipsis.woot.util.oss.NetworkTools.writeItemStack;

public class AnvilRecipeSerializer<T extends AnvilRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final AnvilRecipeSerializer.IFactory<T> factory;

    public AnvilRecipeSerializer(AnvilRecipeSerializer.IFactory<T> factory) {
        this.factory = factory;
    }

    @Nullable
    @Override
    public T read(ResourceLocation recipeId, RegistryFriendlyByteBuf buffer) {
        try {
            Ingredient baseIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            NonNullList<Ingredient> ingredients = NonNullList.create();
            int ingCount = buffer.readShort();
            if (ingCount != 0) {
                for (int i = 0; i < ingCount; i++)
                    ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            }

            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            return this.factory.create(recipeId, baseIngredient, result.getItem(), result.getCount(), ingredients);

        } catch (Exception e) {
            Woot.setup.getLogger().error("AnvilRecipeSerializer:read", e);
            throw e;
        }
    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer, T recipe) {
        //Woot.setup.getLogger().debug("AnvilRecipeSerializer:write");
        try {
            recipe.getBaseIngredient().write(buffer);
            buffer.writeShort(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients())
                ingredient.write(buffer);
            writeItemStack(buffer, recipe.getOutput());
        } catch (Exception e) {
            Woot.setup.getLogger().error("AnvilRecipeSerializer:write", e);
            throw e;
        }

    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {

        JsonElement jsonelement = (JsonElement) (JSONUtils.isJsonArray(json, "base") ? JSONUtils.getJsonArray(json, "base") : JSONUtils.getJsonObject(json, "base"));
        Ingredient baseIngredient = Ingredient.deserialize(jsonelement);

        NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
        if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for anvil recipe");
        } else if (nonnulllist.size() > 4) {
            throw new JsonParseException("Too many ingredients for anvil recipe the max is 4");
        } else {
            ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return this.factory.create(recipeId, baseIngredient, result.getItem(), result.getCount(), nonnulllist);
        }
    }

    private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        for (int i = 0; i < p_199568_0_.size(); ++i) {
            Ingredient ingredient = Ingredient.deserialize(p_199568_0_.get(i));
            if (!ingredient.hasNoMatchingItems())
                nonnulllist.add(ingredient);
        }
        return nonnulllist;
    }

    public interface IFactory<T extends AnvilRecipe> {
        T create(ResourceLocation resourceLocation, Ingredient base, ItemProvider result, int count, NonNullList<Ingredient> ingredients);
    }
}