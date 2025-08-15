package ipsis.woot.modules.factory.calculators;

import ipsis.woot.Woot;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.factory.FactoryRecipe;
import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.modules.factory.FormedSetup;
import ipsis.woot.modules.factory.blocks.ControllerBlockEntity;
import ipsis.woot.modules.factory.blocks.HeartRecipe;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.FluidStackHelper;
import ipsis.woot.util.ItemStackHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CalculatorVersion2 {

    private static final Logger LOGGER = LogManager.getLogger();

    private static double getEfficiency(FakeMob fakeMob, FormedSetup setup) {
        double v = 0.0F;
        if (setup.hasConatusExotic()) {
            v = FactoryConfiguration.EXOTIC_C.get();
            LOGGER.debug("Calculator: EXOTIC {} conatus efficiency", v);
        } else if (setup.getAllPerks().containsKey(Perk.Group.efficiency)) {
            v = setup.getAllMobParams().get(fakeMob).getPerkEfficiencyValue();
            LOGGER.debug("Calculator: PERK {} conatus efficiency", v);
        }
        return v;
    }

    private static int calcConatus(FormedSetup setup) {
        int fluidCost = 0;
        for (FakeMob fakeMob : setup.getAllMobs()) {
            int mobCost = setup.getAllMobParams().get(fakeMob).baseFluidCost * setup.getAllMobParams().get(fakeMob).getMobCount(setup.getAllPerks().containsKey(Perk.Group.mass), setup.hasMassExotic());
            LOGGER.debug("Calculator mob:{} fluidCost:{}", fakeMob, mobCost);

            int fluidSaving = (int)((mobCost / 100.0F) * getEfficiency(fakeMob, setup));
            mobCost -= fluidSaving;
            mobCost = Math.clamp(mobCost, 0, Integer.MAX_VALUE);
            LOGGER.debug("Calculator: {} saving of {}mB -> {}mB", fakeMob, fluidSaving, mobCost);
            fluidCost += mobCost;
        }
        return fluidCost;
    }

    private static int calcSpawnTick(int actualSpawnTicks, int baseSpawnTicks, FormedSetup setup) {
        int ticks = actualSpawnTicks;
        if (setup.hasSpawnTimExotic()) {
            ticks = FactoryConfiguration.EXOTIC_D.get();
            LOGGER.debug("Calculator: EXOTIC {} ticks", ticks);
        } else {
            int rateSaving = (int)((baseSpawnTicks / 100.0F) * setup.getMinRateValue());
            LOGGER.debug("Calculator: PERK {} rate", rateSaving);
            // Lowest reduction in rate across all mobs
            ticks -= rateSaving;
            LOGGER.debug("Calculator: {} @ {}% -> {}", baseSpawnTicks, setup.getMinRateValue(), actualSpawnTicks);
        }

        return ticks;
    }

    /**
     * Returns all the items, with exotic reductions and mass settings, to spawn the count of fakemob
     */
    public static List<ItemStack> getRecipeItems(FakeMob fakeMob, NonNullList<ItemStack> recipeItems, FormedSetup setup) {
        int mobCount = setup.getAllMobParams().get(fakeMob).getMobCount(setup.getAllPerks().containsKey(Perk.Group.mass), setup.hasMassExotic());
        List<ItemStack> items = new ArrayList<>();
        if (setup.hasItemIngredientExotic()) {
            for (ItemStack itemStack : recipeItems) {
                ItemStack itemStack1 = itemStack.copy();
                itemStack1.setCount(itemStack1.getCount() * mobCount);
                itemStack1 = ItemStackHelper.reduceByPercentage(itemStack1, FactoryConfiguration.EXOTIC_B.get());
                if (!itemStack1.isEmpty())
                    items.add(itemStack1);
            }
        } else {
            for (ItemStack itemStack : recipeItems) {
                if (!itemStack.isEmpty()) {
                    Woot.setup.getLogger().debug("getRecipeItems: {} {} {} {}",
                            fakeMob, itemStack.getDescriptionId(), itemStack.getCount(), mobCount);
                    ItemStack itemStack1 = itemStack.copy();
                    itemStack1.setCount(itemStack1.getCount() * mobCount);
                    items.add(itemStack1);
                }
            }
        }

        for (ItemStack itemStack : items)
            Woot.setup.getLogger().debug("getRecipeItems: {} ", itemStack);
        return items;
    }

    /**
     * Returns all the fluids, with exotic reductions and mass settings, to spawn the count of fakemob
     */
    public static List<FluidStack> getRecipeFluids(FakeMob fakeMob, NonNullList<FluidStack> recipeFluids, FormedSetup setup) {
        int mobCount = setup.getAllMobParams().get(fakeMob).getMobCount(setup.getAllPerks().containsKey(Perk.Group.mass), setup.hasMassExotic());
        List<FluidStack> fluids = new ArrayList<>();
        if (setup.hasFluidIngredientExotic()) {
            for (FluidStack fluidStack : recipeFluids) {
                FluidStack fluidStack1 = fluidStack.copy();
                fluidStack1.setAmount(fluidStack1.getAmount() * mobCount);
                fluidStack1 = FluidStackHelper.reduceByPercentage(fluidStack1, FactoryConfiguration.EXOTIC_A.get());
                if (!fluidStack1.isEmpty())
                    fluids.add(fluidStack1);
            }
        } else {
            for (FluidStack fluidStack : recipeFluids) {
                if (!fluidStack.isEmpty()) {
                    Woot.setup.getLogger().debug("getRecipeFluids: {} {} {} {}",
                            fakeMob, fluidStack.getDescriptionId(), fluidStack.getAmount(), mobCount);
                    FluidStack fluidStack1 = fluidStack.copy();
                    fluidStack1.setAmount(fluidStack1.getAmount() * mobCount);
                    fluids.add(fluidStack1);
                }
            }
        }

        for (FluidStack fluidStack : fluids)
            Woot.setup.getLogger().debug("getRecipeItems: {} ", fluidStack);
        return fluids;
    }


    public static HeartRecipe calculate(FormedSetup setup) {

        for (FakeMob fakeMob : setup.getAllMobs())
            LOGGER.debug("Calulator mob:{} params:{}", fakeMob, setup.getAllMobParams().get(fakeMob));

        /**
         * Conatus Cost
         * Impacted by efficiency or exotic C
         */
        int fluidCost = calcConatus(setup);
        LOGGER.debug("Calculator fluidCost:{}", fluidCost);

        /**
         * Spawn ticks
         * Impacted by rate or exotic D
         */
        int baseSpawnTicks = setup.getMaxSpawnTime(); // Highest spawn time across all mobs
        int actualSpawnTicks = baseSpawnTicks;
        LOGGER.debug("Calculator baseSpawnTicks:{} actualSpawnTick:{}", baseSpawnTicks, actualSpawnTicks);
        actualSpawnTicks = calcSpawnTick(actualSpawnTicks, baseSpawnTicks, setup);
        LOGGER.debug("Calculator actualSpawnTick:{}", actualSpawnTicks);

        HeartRecipe recipe = new HeartRecipe( Math.clamp(actualSpawnTicks, 1, Integer.MAX_VALUE), Math.clamp(fluidCost, 1, Integer.MAX_VALUE), new ArrayList<>(), new ArrayList<>());

        /**
         * Recipe ingredients
         * Impacted by exotic A and exotic B
         */
        for (FakeMob fakeMob : setup.getAllMobs()) {
            ItemStack controller = ControllerBlockEntity.getItemStack(fakeMob);
            List<RecipeHolder<FactoryRecipe>> recipes = setup.getWorld().getRecipeManager().getRecipesFor(
                    WootRecipes.FACTORY_RECIPE_TYPE.get(), new SingleRecipeInput(controller),setup.getWorld());

            for (RecipeHolder<FactoryRecipe> factoryRecipe : recipes) {
                if (factoryRecipe.value().getFakeMob().equals(fakeMob)) {
                    List<ItemStack> recipeItems = getRecipeItems(fakeMob, (NonNullList<ItemStack>) factoryRecipe.value().getItems(), setup);
                    List<FluidStack> recipeFluids = getRecipeFluids(fakeMob, (NonNullList<FluidStack>) factoryRecipe.value().getFluids(), setup);
                    recipeItems.forEach(i -> recipe.addItem(i.copy()));
                    recipeFluids.forEach(i -> recipe.addFluid(i.copy()));
                    break;
                }
            }
        }

        return recipe;
    }
}
