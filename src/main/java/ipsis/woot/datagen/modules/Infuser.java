package ipsis.woot.datagen.modules;

import ipsis.woot.Woot;
import ipsis.woot.crafting.infuser.InfuserRecipeBuilder;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.modules.generic.GenericSetup;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.modules.infuser.items.DyeCasingItem;
import ipsis.woot.modules.infuser.items.DyePlateItem;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Consumer;

public class Infuser {

    /**
     * For easy updating of json recipe energy and fluid costs
     */
    private static final ResourceLocation PRISM_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/prism");
    private static final int PRISM_ENERGY_COST = 1000;
    private static final int PRISM_FLUID_COST = 1000;

    private static final ResourceLocation MAGMA_BLOCK_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/magmablock1");
    private static final ResourceLocation MAGMA_BLOCK_RL2 = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/magmablock2");
    private static final int MAGMA_BLOCK_ENERGY_COST = 1000;
    private static final int MAGMA_BLOCK_FLUID_COST = 1000;

    private static final ResourceLocation ENCH_BOOK_1_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/ench_book_1");
    private static final ResourceLocation ENCH_BOOK_2_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/ench_book_2");
    private static final ResourceLocation ENCH_BOOK_3_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/ench_book_3");
    private static final ResourceLocation ENCH_BOOK_4_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/ench_book_4");
    private static final ResourceLocation ENCH_BOOK_5_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/ench_book_5");

    private static final int ENCH_BOOK_1_ENERGY_COST = 1000;
    private static final int ENCH_BOOK_2_ENERGY_COST = 2000;
    private static final int ENCH_BOOK_3_ENERGY_COST = 4000;
    private static final int ENCH_BOOK_4_ENERGY_COST = 8000;
    private static final int ENCH_BOOK_5_ENERGY_COST = 16000;
    private static final int ENCH_BOOK_1_FLUID_COST = 1000;
    private static final int ENCH_BOOK_2_FLUID_COST = 2000;
    private static final int ENCH_BOOK_3_FLUID_COST = 3000;
    private static final int ENCH_BOOK_4_FLUID_COST = 4000;
    private static final int ENCH_BOOK_5_FLUID_COST = 5000;

    private static final ResourceLocation ENCH_PLATE_1_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/ench_plate_1");
    private static final ResourceLocation ENCH_PLATE_2_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/ench_plate_2");
    private static final ResourceLocation ENCH_PLATE_3_RL = ResourceLocation.fromNamespaceAndPath(Woot.MODID,"infuser/ench_plate_3");

    private static final int ENCH_PLATE_1_ENERGY_COST = 10000;
    private static final int ENCH_PLATE_2_ENERGY_COST = 20000;
    private static final int ENCH_PLATE_3_ENERGY_COST = 30000;
    private static final int ENCH_PLATE_1_FLUID_COST = 1000;
    private static final int ENCH_PLATE_2_FLUID_COST = 2000;
    private static final int ENCH_PLATE_3_FLUID_COST = 3000;

    private static final int DYE_ENERGY_COST = 400;
    private static final int DYE_FLUID_COST = 72;

    public static void registerRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, InfuserSetup.INFUSER_BLOCK.get())
                .pattern(" d ")
                .pattern("pcp")
                .pattern(" b ")
                .define('d', Blocks.DROPPER)
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .define('b', Items.BUCKET)
                .define('p', Blocks.PISTON)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, InfuserSetup.INFUSER_BLOCK.get())
                .requires(InfuserSetup.INFUSER_BLOCK.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput, "infuser_1");


        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(GenericSetup.PRISM_ITEM.get(), 1),
                Ingredient.of(Tags.Items.GLASS_BLOCKS),
                Ingredient.EMPTY, 0,
                new FluidStack(FluidSetup.PUREDYE_FLUID.get(), PRISM_FLUID_COST),
                PRISM_ENERGY_COST).save(recipeOutput, "prism");

        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(Blocks.MAGMA_BLOCK.asItem(), 1),
                Ingredient.of(Tags.Items.STONES),
                Ingredient.EMPTY, 0,
                new FluidStack(Fluids.LAVA, MAGMA_BLOCK_FLUID_COST),
                MAGMA_BLOCK_ENERGY_COST).save(recipeOutput, "magmablock1");

        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(Blocks.MAGMA_BLOCK.asItem(), 2),
                Ingredient.of(Tags.Items.OBSIDIANS),
                Ingredient.EMPTY, 0,
                new FluidStack(Fluids.LAVA, MAGMA_BLOCK_FLUID_COST),
                MAGMA_BLOCK_ENERGY_COST).save(recipeOutput, "magmablock2");


        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(Items.ENCHANTED_BOOK, 1),
                Ingredient.of(Items.BOOK),
                Ingredient.of(Items.REDSTONE), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), ENCH_BOOK_1_FLUID_COST),
                ENCH_BOOK_1_ENERGY_COST).save(recipeOutput, "ench_book_1");
        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(Items.ENCHANTED_BOOK,  2),
                Ingredient.of(Items.BOOK),
                Ingredient.of(Items.QUARTZ), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), ENCH_BOOK_2_FLUID_COST),
                ENCH_BOOK_2_ENERGY_COST).save(recipeOutput, "ench_book_2");
        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(Items.ENCHANTED_BOOK,  3),
                Ingredient.of(Items.BOOK),
                Ingredient.of(Items.REDSTONE_BLOCK), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), ENCH_BOOK_3_FLUID_COST),
                ENCH_BOOK_3_ENERGY_COST).save(recipeOutput, "ench_book_3");
        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(Items.ENCHANTED_BOOK,  4),
                Ingredient.of(Items.BOOK),
                Ingredient.of(Items.QUARTZ_BLOCK), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), ENCH_BOOK_4_FLUID_COST),
                ENCH_BOOK_4_ENERGY_COST).save(recipeOutput, "ench_book_4");
        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(Items.ENCHANTED_BOOK,  5),
                Ingredient.of(Items.BOOK),
                Ingredient.of(Blocks.LAPIS_BLOCK), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), ENCH_BOOK_5_FLUID_COST),
                ENCH_BOOK_5_ENERGY_COST).save(recipeOutput, "ench_book_5");

        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(GenericSetup.ENCH_PLATE_1.get(), 1),
                Ingredient.of(GenericSetup.SI_PLATE_ITEM.get()),
                Ingredient.of(Tags.Items.INGOTS_IRON), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), ENCH_PLATE_1_FLUID_COST),
                ENCH_PLATE_1_ENERGY_COST).save(recipeOutput, "ench_plate_1");
        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(GenericSetup.ENCH_PLATE_2.get(), 1),
                Ingredient.of(GenericSetup.SI_PLATE_ITEM.get()),
                Ingredient.of(Tags.Items.INGOTS_GOLD), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), ENCH_PLATE_2_FLUID_COST),
                ENCH_PLATE_2_ENERGY_COST).save(recipeOutput, "ench_plate_2");
        InfuserRecipeBuilder.infuserRecipe(
                new ItemStack(GenericSetup.ENCH_PLATE_3.get(), 1),
                Ingredient.of(GenericSetup.SI_PLATE_ITEM.get()),
                Ingredient.of(Tags.Items.GEMS_DIAMOND), 1,
                new FluidStack(FluidSetup.ENCHANT_FLUID.get(), ENCH_PLATE_3_FLUID_COST),
                ENCH_PLATE_3_ENERGY_COST).save(recipeOutput, "ench_plate_3");


        /**
         * Plates
         */
        class Plate {
            DeferredHolder<Item, DyeCasingItem> casing;
            DeferredHolder<Item, DyePlateItem> plate;
            String name;
            public Plate(DeferredHolder<Item, DyeCasingItem> casing,  DeferredHolder<Item, DyePlateItem> plate, String name) {
                this.casing = casing;
                this.plate = plate;
                this.name = name + "_plate";
            }
        }
        Plate[] plates = {
                new Plate(InfuserSetup.WHITE_DYE_CASING_ITEM, InfuserSetup.WHITE_DYE_PLATE_ITEM, "white"),
                new Plate(InfuserSetup.ORANGE_DYE_CASING_ITEM, InfuserSetup.ORANGE_DYE_PLATE_ITEM, "orange"),
                new Plate(InfuserSetup.MAGENTA_DYE_CASING_ITEM, InfuserSetup.MAGENTA_DYE_PLATE_ITEM, "magenta"),
                new Plate(InfuserSetup.LIGHT_BLUE_DYE_CASING_ITEM, InfuserSetup.LIGHT_BLUE_DYE_PLATE_ITEM, "light_blue"),
                new Plate(InfuserSetup.YELLOW_DYE_CASING_ITEM, InfuserSetup.YELLOW_DYE_PLATE_ITEM, "yellow"),
                new Plate(InfuserSetup.LIME_DYE_CASING_ITEM, InfuserSetup.LIME_DYE_PLATE_ITEM, "lime"),
                new Plate(InfuserSetup.PINK_DYE_CASING_ITEM, InfuserSetup.PINK_DYE_PLATE_ITEM, "pink"),
                new Plate(InfuserSetup.GRAY_DYE_CASING_ITEM, InfuserSetup.GRAY_DYE_PLATE_ITEM, "gray"),
                new Plate(InfuserSetup.LIGHT_GRAY_DYE_CASING_ITEM, InfuserSetup.LIGHT_GRAY_DYE_PLATE_ITEM, "light_gray"),
                new Plate(InfuserSetup.CYAN_DYE_CASING_ITEM, InfuserSetup.CYAN_DYE_PLATE_ITEM, "cyan"),
                new Plate(InfuserSetup.PURPLE_DYE_CASING_ITEM, InfuserSetup.PURPLE_DYE_PLATE_ITEM, "purple"),
                new Plate(InfuserSetup.BLUE_DYE_CASING_ITEM, InfuserSetup.BLUE_DYE_PLATE_ITEM, "blue"),
                new Plate(InfuserSetup.BROWN_DYE_CASING_ITEM, InfuserSetup.BROWN_DYE_PLATE_ITEM, "brown"),
                new Plate(InfuserSetup.GREEN_DYE_CASING_ITEM, InfuserSetup.GREEN_DYE_PLATE_ITEM, "green"),
                new Plate(InfuserSetup.RED_DYE_CASING_ITEM, InfuserSetup.RED_DYE_PLATE_ITEM, "red"),
                new Plate(InfuserSetup.BLACK_DYE_CASING_ITEM, InfuserSetup.BLACK_DYE_PLATE_ITEM, "black")
        };

        for (Plate p : plates) {
            Woot.setup.getLogger().info("Generating Infuser recipe for {} plate", p.name);
            InfuserRecipeBuilder.infuserRecipe(
                    new ItemStack(p.plate.get(), 1),
                    Ingredient.of(p.casing.get()),
                    Ingredient.EMPTY, 0,
                    new FluidStack(FluidSetup.PUREDYE_FLUID.get(), DYE_FLUID_COST),
                    DYE_ENERGY_COST).save(recipeOutput, p.name);
        }

    }
}
