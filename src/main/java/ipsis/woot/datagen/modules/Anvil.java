package ipsis.woot.datagen.modules;

import ipsis.woot.crafting.anvil.AnvilRecipeBuilder;
import ipsis.woot.modules.anvil.AnvilSetup;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.generic.GenericSetup;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Consumer;

public class Anvil {

    public static void registerRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,AnvilSetup.ANVIL_BLOCK.get())
                .pattern("iii")
                .pattern(" a ")
                .pattern("ooo")
                .define('i', GenericSetup.SI_INGOT_ITEM.get())
                .define('a', Blocks.ANVIL)
                .define('o', Tags.Items.OBSIDIANS)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, AnvilSetup.HAMMER_ITEM.get())
                .pattern(" si")
                .pattern(" ws")
                .pattern("w  ")
                .define('i', GenericSetup.SI_INGOT_ITEM.get())
                .define('s', Tags.Items.STRINGS)
                .define('w', Items.STICK)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        AnvilRecipeBuilder.anvilRecipe(
                new ItemStack(AnvilSetup.PLATE_DIE_ITEM.get()),
                Ingredient.of(Items.STONE_SLAB))
                .addIngredient(Ingredient.of(Blocks.OBSIDIAN))
                .save(recipeOutput, "plate_die");

        AnvilRecipeBuilder.anvilRecipe(
                new ItemStack(AnvilSetup.SHARD_DIE_ITEM.get()),
                Ingredient.of(Items.QUARTZ))
                .addIngredient(Ingredient.of(Blocks.OBSIDIAN))
                .save(recipeOutput, "shard_die");

        AnvilRecipeBuilder.anvilRecipe(
                new ItemStack(AnvilSetup.DYE_DIE_ITEM.get()),
                Ingredient.of(Items.GUNPOWDER))
                .addIngredient(Ingredient.of(Blocks.OBSIDIAN))
                .save(recipeOutput, "dye_die");

        AnvilRecipeBuilder.anvilRecipe(
                new ItemStack(FactorySetup.CONTROLLER_BLOCK.get()),
                Ingredient.of(FactorySetup.MOB_SHARD_ITEM.get()))
                .addIngredient(Ingredient.of(GenericSetup.PRISM_ITEM.get()))
                .addIngredient(Ingredient.of(GenericSetup.SI_PLATE_ITEM.get()))
                .save(recipeOutput, "controller");
    }
}
