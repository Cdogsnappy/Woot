package ipsis.woot.datagen.modules;

import ipsis.woot.Woot;
import ipsis.woot.modules.anvil.AnvilSetup;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.generic.GenericSetup;
import ipsis.woot.modules.layout.LayoutSetup;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;


import java.util.function.Consumer;

public class Layout {
    public static void registerRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LayoutSetup.LAYOUT_BLOCK.get())
                .pattern("grg")
                .pattern("ytb")
                .pattern("gwg")
                .define('g', Tags.Items.GLASS_BLOCKS)
                .define('r', Tags.Items.DYES_RED)
                .define('y', Tags.Items.DYES_YELLOW)
                .define('b', Tags.Items.DYES_BLACK)
                .define('w', Tags.Items.DYES_WHITE)
                .define('t', Items.TORCH)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, LayoutSetup.INTERN_ITEM.get())
                .pattern(" si")
                .pattern(" ws")
                .pattern("w  ")
                .define('i', GenericSetup.SI_INGOT_ITEM.get())
                .define('s', Tags.Items.DUSTS_REDSTONE)
                .define('w', Items.STICK)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);
    }
}
