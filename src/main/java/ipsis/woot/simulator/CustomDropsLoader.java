package ipsis.woot.simulator;


import ipsis.woot.crafting.factory.FactoryRecipe;
import ipsis.woot.util.FakeMobKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;


import java.util.HashMap;


public class CustomDropsLoader {

    public static void load(RecipeManager recipeManager) {

        for (RecipeHolder recipeHolder : recipeManager.getRecipes()) {
            if (recipeHolder.value() instanceof FactoryRecipe) {
                FactoryRecipe factoryRecipe = (FactoryRecipe) recipeHolder.value();
                if (factoryRecipe.getFakeMob().isValid()) {
                    for (FactoryRecipe.Drop drop : factoryRecipe.getDrops()) {
                        ItemStack itemStack = drop.itemStack;
                        for (int i = 0; i < 4; i++) {
                            itemStack.setCount(1);
                            HashMap<Integer, Integer> stackSizes = new HashMap<>();
                            stackSizes.put(drop.stackSizes[i], 1);
                            MobSimulator.getInstance().learnCustomDrop(
                                    new FakeMobKey(factoryRecipe.getFakeMob(), i),
                                    itemStack,
                                    drop.dropChance[i], stackSizes);
                        }
                    }
                }
            }
        }
    }
}
