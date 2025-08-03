package ipsis.woot.datagen.modules;

import ipsis.woot.Woot;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.generic.GenericSetup;
import ipsis.woot.modules.layout.LayoutSetup;
import ipsis.woot.modules.oracle.OracleSetup;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.block.Blocks;


import java.util.function.Consumer;

public class Oracle {
    public static void registerRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, OracleSetup.ORACLE_BLOCK.get())
                .pattern(" b ")
                .pattern("bcb")
                .pattern(" b ")
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .define('b', Blocks.BOOKSHELF)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);
    }
}
