package ipsis.woot.crafting.anvil;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record AnvilRecipeInput(ItemStack baseIngredient, NonNullList<ItemStack> ingredients) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}
