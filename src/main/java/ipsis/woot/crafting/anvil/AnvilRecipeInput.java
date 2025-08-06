package ipsis.woot.crafting.anvil;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record AnvilRecipeInput(ItemStack baseItem, List<ItemStack> ingredients) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return ingredients.get(i);
    }

    public List<ItemStack> getIngredients(){
        return ingredients;
    }

    public ItemStack getBase(){
        return baseItem;
    }

    @Override
    public int size() {
        return 0;
    }
}
