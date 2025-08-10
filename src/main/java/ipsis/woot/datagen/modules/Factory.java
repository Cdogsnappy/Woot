package ipsis.woot.datagen.modules;

import ipsis.woot.Woot;
import ipsis.woot.crafting.factory.FactoryRecipe;
import ipsis.woot.crafting.factory.FactoryRecipeBuilder;
import ipsis.woot.mod.ModTags;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.items.PerkItem;
import ipsis.woot.modules.generic.GenericSetup;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.util.FakeMob;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Arrays;

public class Factory {

    public static void registerRecipes(RecipeOutput recipeOutput) {

        /**
         * Basic factory blocks
         */
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.FACTORY_A_BLOCK.get(), 4)
                .pattern(" s ")
                .pattern("sps")
                .pattern(" s ")
                .define('s', Tags.Items.STONES)
                .define('p', InfuserSetup.MAGENTA_DYE_PLATE_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.FACTORY_B_BLOCK.get(), 4)
                .pattern(" s ")
                .pattern("sps")
                .pattern(" s ")
                .define('s', Tags.Items.STONES)
                .define('p', InfuserSetup.BROWN_DYE_PLATE_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.FACTORY_C_BLOCK.get(), 4)
                .pattern("zs ")
                .pattern("sps")
                .pattern(" s ")
                .define('s', Tags.Items.STONES)
                .define('p', InfuserSetup.LIGHT_BLUE_DYE_PLATE_ITEM.get())
                .define('z', GenericSetup.T1_SHARD_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.FACTORY_D_BLOCK.get(), 2)
                .pattern("zsz")
                .pattern("sps")
                .pattern(" s ")
                .define('s', Tags.Items.STONES)
                .define('p', InfuserSetup.GREEN_DYE_PLATE_ITEM.get())
                .define('z', GenericSetup.T2_SHARD_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.FACTORY_E_BLOCK.get(), 2)
                .pattern("zsz")
                .pattern("sps")
                .pattern("zsz")
                .define('s', Tags.Items.STONES)
                .define('p', InfuserSetup.BLUE_DYE_PLATE_ITEM.get())
                .define('z', GenericSetup.T3_SHARD_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.FACTORY_UPGRADE_BLOCK.get())
                .pattern(" s ")
                .pattern("sps")
                .pattern(" s ")
                .define('s', Tags.Items.STONES)
                .define('p', InfuserSetup.PURPLE_DYE_PLATE_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.FACTORY_CTR_BASE_PRI_BLOCK.get())
                .pattern(" s ")
                .pattern("s s")
                .pattern("psx")
                .define('s', Tags.Items.STONES)
                .define('p', InfuserSetup.MAGENTA_DYE_PLATE_ITEM.get())
                .define('x', InfuserSetup.PINK_DYE_PLATE_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.FACTORY_CTR_BASE_SEC_BLOCK.get())
                .pattern(" s ")
                .pattern("s s")
                .pattern("psx")
                .define('s', Tags.Items.STONES)
                .define('p', InfuserSetup.BLUE_DYE_PLATE_ITEM.get())
                .define('x', InfuserSetup.LIGHT_BLUE_DYE_PLATE_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,FactorySetup.FACTORY_CONNECT_BLOCK.get())
                .requires(ModTags.Items.FACTORY_BLOCK)
                .requires(Items.REDSTONE)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,FactorySetup.CAP_A_BLOCK.get())
                .requires(ModTags.Items.FACTORY_BLOCK)
                .requires(Items.IRON_NUGGET)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,FactorySetup.CAP_B_BLOCK.get())
                .requires(ModTags.Items.FACTORY_BLOCK)
                .requires(Items.GOLD_INGOT)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,FactorySetup.CAP_C_BLOCK.get())
                .requires(ModTags.Items.FACTORY_BLOCK)
                .requires(Items.DIAMOND)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,FactorySetup.CAP_D_BLOCK.get())
                .requires(ModTags.Items.FACTORY_BLOCK)
                .requires(Items.EMERALD)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        /**
         * Cells
         */
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.CELL_1_BLOCK.get())
                .pattern("igi")
                .pattern("gcg")
                .pattern("igi")
                .define('g', Tags.Items.GLASS_BLOCKS)
                .define('i', Items.IRON_BARS)
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,FactorySetup.CELL_2_BLOCK.get())
                .requires(FactorySetup.CELL_1_BLOCK.get())
                .requires(GenericSetup.ENCH_PLATE_1.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,FactorySetup.CELL_3_BLOCK.get())
                .requires(FactorySetup.CELL_2_BLOCK.get())
                .requires(GenericSetup.ENCH_PLATE_2.get())
                .requires(GenericSetup.ENCH_PLATE_2.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,FactorySetup.CELL_4_BLOCK.get())
                .requires(FactorySetup.CELL_3_BLOCK.get())
                .requires(GenericSetup.ENCH_PLATE_3.get())
                .requires(GenericSetup.ENCH_PLATE_3.get())
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        /**
         * Perks
         */
        class RecipePerk {
            DeferredHolder<Item, PerkItem> perkItem1;
            DeferredHolder<Item, PerkItem> perkItem2;
            DeferredHolder<Item, PerkItem> perkItem3;
            ItemLike i1;
            ItemLike i2;
            public RecipePerk(DeferredHolder<Item, PerkItem> perkItem1, DeferredHolder<Item, PerkItem> perkItem2,
                              DeferredHolder<Item, PerkItem> perkItem3, ItemLike i1, ItemLike i2) {
                this.perkItem1 = perkItem1;
                this.perkItem2 = perkItem2;
                this.perkItem3 = perkItem3;
                this.i1 = i1;
                this.i2 = i2;
            }

        }
        RecipePerk[] recipePerks = {
                new RecipePerk(
                        FactorySetup.EFFICIENCY_1_ITEM,
                        FactorySetup.EFFICIENCY_2_ITEM,
                        FactorySetup.EFFICIENCY_3_ITEM,
                        Blocks.REDSTONE_BLOCK,
                        Blocks.BLAST_FURNACE),
                new RecipePerk(
                        FactorySetup.LOOTING_1_ITEM,
                        FactorySetup.LOOTING_2_ITEM,
                        FactorySetup.LOOTING_3_ITEM,
                        Items.DIAMOND_SWORD,
                        Blocks.LAPIS_BLOCK),
                new RecipePerk(
                        FactorySetup.MASS_1_ITEM,
                        FactorySetup.MASS_2_ITEM,
                        FactorySetup.MASS_3_ITEM,
                        Items.MAGMA_CREAM,
                        Blocks.NETHER_WART),
                new RecipePerk(
                        FactorySetup.RATE_1_ITEM,
                        FactorySetup.RATE_2_ITEM,
                        FactorySetup.RATE_3_ITEM,
                        Blocks.REDSTONE_BLOCK,
                        Blocks.REDSTONE_BLOCK),
                new RecipePerk(
                        FactorySetup.XP_1_ITEM,
                        FactorySetup.XP_2_ITEM,
                        FactorySetup.XP_3_ITEM,
                        Blocks.ENCHANTING_TABLE,
                        Items.LAPIS_LAZULI),
                new RecipePerk(
                        FactorySetup.TIER_SHARD_1_ITEM,
                        FactorySetup.TIER_SHARD_2_ITEM,
                        FactorySetup.TIER_SHARD_3_ITEM,
                        Items.GOLDEN_CARROT,
                        Items.NETHER_WART),
                new RecipePerk(
                        FactorySetup.HEADLESS_1_ITEM,
                        FactorySetup.HEADLESS_2_ITEM,
                        FactorySetup.HEADLESS_3_ITEM,
                        Items.ZOMBIE_HEAD,
                        Items.SKELETON_SKULL)
        };

        for (RecipePerk recipePerk : recipePerks) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, recipePerk.perkItem1.get())
                    .pattern(" 1 ")
                    .pattern(" e ")
                    .pattern(" 2 ")
                    .define('e', GenericSetup.ENCH_PLATE_1.get())
                    .define('1', recipePerk.i1)
                    .define('2', recipePerk.i2)
                    .group(Woot.MODID)
                    .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                    .save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC,recipePerk.perkItem2.get())
                    .pattern(" 1 ")
                    .pattern("e e")
                    .pattern(" 2 ")
                    .define('e', GenericSetup.ENCH_PLATE_2.get())
                    .define('1', recipePerk.i1)
                    .define('2', recipePerk.i2)
                    .group(Woot.MODID)
                    .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                    .save(recipeOutput);
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC,recipePerk.perkItem3.get())
                    .pattern(" 1 ")
                    .pattern("eee")
                    .pattern(" 2 ")
                    .define('e', GenericSetup.ENCH_PLATE_3.get())
                    .define('1', recipePerk.i1)
                    .define('2', recipePerk.i2)
                    .group(Woot.MODID)
                    .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                    .save(recipeOutput);
        }

        /**
         * Other
         */
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.HEART_BLOCK.get())
                .pattern(" s ")
                .pattern("scs")
                .pattern(" s ")
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .define('s', ItemTags.SKULLS)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.IMPORT_BLOCK.get())
                .pattern(" h ")
                .pattern(" c ")
                .pattern("   ")
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .define('h', Blocks.HOPPER)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.EXPORT_BLOCK.get())
                .pattern("   ")
                .pattern(" c ")
                .pattern(" h ")
                .define('c', GenericSetup.MACHINE_CASING_ITEM.get())
                .define('h', Blocks.HOPPER)
                .group(Woot.MODID)
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,FactorySetup.XP_SHARD_ITEM.get())
                .pattern("sss")
                .pattern("sss")
                .pattern("sss")
                .define('s', FactorySetup.XP_SPLINTER_ITEM.get())
                .unlockedBy("cobblestone", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.COBBLESTONE))
                .save(recipeOutput);

        /* Now using modded drops as well
        FactoryRecipeBuilder.factoryRecipe(new FakeMob("minecraft:ender_dragon"))
                .requires(new ItemStack(Items.END_CRYSTAL, 4))
                .addDrop(new FactoryRecipe.Drop(
                        new ItemStack(Blocks.DRAGON_EGG),
                        1, 1, 1, 1,
                        100.0F, 100.0F, 100.0F, 100.0F))
                .addDrop(new FactoryRecipe.Drop(
                        new ItemStack(Items.DRAGON_BREATH),
                        2, 4, 6, 8,
                        100.0F, 100.0F, 100.0F, 100.0F))
                .save(recipeOutput, "ender_dragon"); */


    }
}
