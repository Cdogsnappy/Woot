package ipsis.woot.datagen.modules;

import ipsis.woot.Woot;
import ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipeBuilder;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.generic.GenericSetup;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidConvertor {

    public static void registerRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FluidConvertorSetup.FLUID_CONVERTOR_BLOCK.get())
                .pattern(" s ")
                .pattern(" c ")
                .pattern("bfb")
                .define('s', Blocks.BREWING_STAND)
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .define('b', Items.BUCKET)
                .define('f', Blocks.FURNACE)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, FluidConvertorSetup.FLUID_CONVERTOR_BLOCK.get())
                .requires(FluidConvertorSetup.FLUID_CONVERTOR_BLOCK.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput, "fluid_conv_1");

        /**
         * Contaus Fluid
         */
        Ingredient[] conatus_ingredients = {
                Ingredient.of(FactorySetup.XP_SHARD_ITEM.get()),
                Ingredient.of(FactorySetup.XP_SPLINTER_ITEM.get()),
                Ingredient.of(Items.REDSTONE),
                Ingredient.of(GenericSetup.T1_SHARD_ITEM.get()),
                Ingredient.of(GenericSetup.T2_SHARD_ITEM.get()),
                Ingredient.of(GenericSetup.T3_SHARD_ITEM.get())
        };
        ResourceLocation rl;
        int[] conatus_outputAmount = { 1000, 100, 1000, 1250, 2500, 5000 };
        if (conatus_ingredients.length == conatus_outputAmount.length) {
            for (int i = 0; i < conatus_ingredients.length; i++) {
                FluidConvertorRecipeBuilder.fluidConvertorRecipe(
                        new FluidStack(FluidSetup.CONATUS_FLUID.get(), conatus_outputAmount[i]),
                        1000,
                        conatus_ingredients[i], 1,
                        new FluidStack(FluidSetup.MOB_ESSENCE_FLUID.get(), 1000))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Woot.MODID, "fluidconvertor/conatus" + i));
            }
        } else {
            Woot.setup.getLogger().error("FluidConvertor recipes ingredients != outputAmount");
        }

        rl = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "fluidconvertor/conatus_ench1");
        FluidConvertorRecipeBuilder.fluidConvertorRecipe(
                new FluidStack(FluidSetup.CONATUS_FLUID.get(), 1250),
                1000,
                Ingredient.of(Items.MAGMA_BLOCK), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), 1000))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Woot.MODID, "fluidconvertor/conatus_ench1"));

        FluidConvertorRecipeBuilder.fluidConvertorRecipe(
                new FluidStack(FluidSetup.CONATUS_FLUID.get(), 1450),
                1000,
                Ingredient.of(Items.END_STONE), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), 1000))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Woot.MODID, "fluidconvertor/conatus_ench2"));

        /**
         * Purge Fluid
         */
        Ingredient[] ingredients = {
                Ingredient.of(Items.ROTTEN_FLESH),
                Ingredient.of(Items.BONE),
                Ingredient.of(Items.BLAZE_ROD),
                Ingredient.of(Items.ENDER_PEARL),
        };
        int[] outputAmount = { 1000, 1000, 2000, 4000 };

        if (ingredients.length == outputAmount.length) {
            for (int i = 0; i < ingredients.length; i++) {
                FluidConvertorRecipeBuilder.fluidConvertorRecipe(
                        new FluidStack(FluidSetup.MOB_ESSENCE_FLUID.get(), outputAmount[i]),
                        1000,
                        ingredients[i], 1,
                        new FluidStack(Fluids.WATER, 1000))
                        .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(Woot.MODID, "fluidconvertor/purge" + i));
            }
        } else {
            Woot.setup.getLogger().error("FluidConvertor recipes ingredients != outputAmount");
        }
    }
}
