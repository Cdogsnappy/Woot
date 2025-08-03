package ipsis.woot.simulator.library;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import ipsis.woot.Woot;
import ipsis.woot.simulator.DropStackData;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.SimulatedMobDropSummary;
import ipsis.woot.util.helper.JsonHelper;
import ipsis.woot.util.helper.MathHelper;
import ipsis.woot.util.helper.RandomHelper;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class SimulatedMobDrop {

    protected ItemStack itemStack;
    protected int[] simulatedDropCount;
    protected float[] customChanceToDrop;
    protected boolean hasCustom;
    protected SimulatedMob simulatedMob;

    protected List<HashMap<Integer, Integer>> simulatedStackSize;
    protected List<HashMap<Integer, Integer>> customStackSize;

    private SimulatedMobDrop(){}
    public SimulatedMobDrop(ItemStack itemStack, SimulatedMob simulatedMob) {
        this.simulatedMob = simulatedMob;
        this.hasCustom = false;
        this.itemStack = itemStack.copy();
        simulatedDropCount = new int[]{0, 0, 0, 0};
        customChanceToDrop = new float[]{ 0.0F, 0.0F, 0.0F, 0.0F};
        simulatedStackSize = new ArrayList<>(4);
        customStackSize = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            simulatedStackSize.add(i, new HashMap<>());
            customStackSize.add(i, new HashMap<>());
        }
    }

    @Override
    public String toString() {
        return "SimulatedMobDrop{" +
                "itemStack=" + itemStack +
                ", simulatedDropCount=" + Arrays.toString(simulatedDropCount) +
                ", customChanceToDrop=" + Arrays.toString(customChanceToDrop) +
                ", hasCustom=" + hasCustom +
                ", simulatedMob=" + simulatedMob +
                ", simulatedStackSize=" + simulatedStackSize +
                ", customStackSize=" + customStackSize +
                '}';
    }

    private float calculateDropChance(int looting) {
        looting = MathHelper.clampLooting(looting);
        if (hasCustom)
            return customChanceToDrop[looting];

        float dropChance = 0.0F;
        if (simulatedMob.getSimulatedKills(looting) > 0)
            dropChance = (100.0F / simulatedMob.getSimulatedKills(looting)) * simulatedDropCount[looting];

        return dropChance;
    }

    private int calculateDropSize(int looting) {
        int stackSize = 1;
        looting = MathHelper.clampLooting(looting);

        HashMap<Integer, Integer> stackWeights;
        if (hasCustom)
            stackWeights = customStackSize.get(looting);
        else
            stackWeights = simulatedStackSize.get(looting);


        if (!stackWeights.isEmpty()) {
            List<DropStackData> dropstacks = new ArrayList<>();
            //Woot.setup.getLogger().debug("calculateDropSize: {}", stackWeights);
            for (Map.Entry<Integer, Integer> entry : stackWeights.entrySet())
                dropstacks.add(new DropStackData(entry.getKey(), entry.getValue()));

            if (WeightedRandom.getTotalWeight(dropstacks) > 0) {
                DropStackData chosen = WeightedRandom.getRandomItem(RandomHelper.RANDOM, dropstacks);
                stackSize = chosen.stackSize;
            }
            //Woot.setup.getLogger().debug("calculateDropSize: custom:{} {} chosen {}",
            //        hasCustom, dropstacks, stackSize);
        }

        /*
        if (hasCustom) {
            HashMap<Integer, Integer> stackSizes = customStackSize.get(looting);
            if (stackSizes.isEmpty()) {
                Woot.setup.getLogger().debug("calculateDropSize: no custom stack sizes default to 1");
                stackSize = 1;
            } else {
                List<DropStackData> dropstacks = new ArrayList<>();
                Woot.setup.getLogger().debug("calculateDropSize: {}", stackSizes);
                for (Map.Entry<Integer, Integer> entry : stackSizes.entrySet())
                    dropstacks.add(new DropStackData(entry.getKey(), entry.getValue()));

                if (WeightedRandom.getTotalWeight(dropstacks) == 0) {
                    stackSize = 1;
                } else {
                    DropStackData chosen = WeightedRandom.getRandomItem(RandomHelper.RANDOM, dropstacks);
                    stackSize = chosen.stackSize;
                }
                Woot.setup.getLogger().debug("customDrop: {} chosen {}",
                        dropstacks, stackSize);
            }
        } else {
            HashMap<Integer, Integer> stackSizes = simulatedStackSize.get(looting);
            if (stackSizes.isEmpty()) {
                Woot.setup.getLogger().debug("calculateDropSize: no simulated stack sizes default to 1");
                stackSize = 1;
            } else {
                List<DropStackData> dropstacks = new ArrayList<>();
                Woot.setup.getLogger().debug("calculateDropSize: {}", stackSizes);
                for (Map.Entry<Integer, Integer> entry : stackSizes.entrySet())
                    dropstacks.add(new DropStackData(entry.getKey(), entry.getValue()));

                if (WeightedRandom.getTotalWeight(dropstacks) == 0) {
                    stackSize = 1;
                } else {
                    DropStackData chosen = WeightedRandom.getRandomItem(RandomHelper.RANDOM, dropstacks);
                    stackSize = chosen.stackSize;
                }

                Woot.setup.getLogger().debug("simulatedDrop: {} chosen {}",
                        dropstacks, stackSize);
            }
        } */

        return stackSize;
    }

    public void addSimulatedData(int looting, int stackSize) {
        looting = MathHelper.clampLooting(looting);
        simulatedDropCount[looting]++;

        Integer count = simulatedStackSize.get(looting).getOrDefault(stackSize, 0);
        simulatedStackSize.get(looting).put(stackSize, count + 1);
    }

    public void addCustomDrop(int looting, float dropChance, HashMap<Integer, Integer> stackSizes) {
        looting = MathHelper.clampLooting(looting);
        customChanceToDrop[looting] = dropChance;
        hasCustom = true;
        Woot.setup.getLogger().debug("SimulatedMobDrop looting:{} {} custom drop chance:{}", looting, itemStack, dropChance);
        for (Map.Entry<Integer, Integer> entry : stackSizes.entrySet()) {
            customStackSize.get(looting).put(entry.getKey(), entry.getValue());
            Woot.setup.getLogger().debug("SimulatedMobDrop custom drop size {} weight:{}", entry.getKey(), entry.getValue());
        }
    }

    public SimulatedMobDropSummary createSummary() {
        return new SimulatedMobDropSummary(itemStack.copy(), Arrays.asList(calculateDropChance(0),
                calculateDropChance(1),
                calculateDropChance(2),
                calculateDropChance(3))
                );
    }

    public @Nonnull ItemStack getRolledDrop(int looting) {
        ItemStack dropStack = ItemStack.EMPTY;
        float dropChance = calculateDropChance(looting);
        //MobSimulator.LOGGER.debug("getRolledDrop: rolling looting:{} for {} @ {}%", looting, itemStack, dropChance);

        if (RandomHelper.rollPercentage(dropChance, "getRolledDrop")) {
            dropStack = itemStack.copy();
            dropStack.setCount(calculateDropSize(looting));
        }

        return dropStack;
    }

    /**
     * Save/Load
     */
    private static final String TAG_DROPPED_ITEM = "drop";
    private static final String TAG_SIM_KILLS = "simulatedCount";
    private static final String[] TAG_STACK_SIZES = { "stack0", "stack1", "stack2", "stack3" };

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        {
            // Use ItemStack codec to serialize the ItemStack
            DataResult<JsonElement> itemResult = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, itemStack);
            if (itemResult.result().isPresent()) {
                jsonObject.add(TAG_DROPPED_ITEM, itemResult.result().get());
            } else {
                // Handle error case - you might want to log this
                jsonObject.add(TAG_DROPPED_ITEM, new JsonObject()); // or however you want to handle errors
            }
            JsonArray simulatedArray = new JsonArray();
            for (int i = 0; i < 4; i++)
                simulatedArray.add(simulatedDropCount[i]);
            jsonObject.add(TAG_SIM_KILLS, simulatedArray);

            for (int i = 0; i < 4; i++) {
                JsonArray stackArray = new JsonArray();
                for (Map.Entry<Integer, Integer> e : simulatedStackSize.get(i).entrySet()) {
                    stackArray.add(e.getKey());
                    stackArray.add(e.getValue());
                }
                jsonObject.add(TAG_STACK_SIZES[i], stackArray);
            }
        }
        return jsonObject;
    }

    public static @Nullable SimulatedMobDrop fromJson(SimulatedMob simulatedMob, JsonObject jsonObject) {
        ItemStack itemStack = null;
        try {
            JsonElement itemElement = jsonObject.get(TAG_DROPPED_ITEM);
            DataResult<ItemStack> itemResult = ItemStack.CODEC.parse(JsonOps.INSTANCE, itemElement);

            if (itemResult.result().isPresent()) {
                itemStack = itemResult.result().get();
            }
        } catch (JsonSyntaxException e) {
            Woot.setup.getLogger().error("Failed to parse itemstack");
            return null;
        }

        JsonArray dropsArray = GsonHelper.getAsJsonArray(jsonObject, TAG_SIM_KILLS);
        if (dropsArray.size() != 4)
            throw new JsonSyntaxException("Simulated count array must be of size 4");

        SimulatedMobDrop simulatedMobDrop = new SimulatedMobDrop(itemStack, simulatedMob);
        for (int i = 0; i < 4; i++)
            simulatedMobDrop.simulatedDropCount[i] = dropsArray.get(i).getAsInt();

        for (int i = 0; i < 4; i++) {
            JsonArray sizesArray = GsonHelper.getAsJsonArray(jsonObject, TAG_STACK_SIZES[i]);
            if (!sizesArray.isEmpty() && sizesArray.size() % 2 == 0) {
                for (int j = 0; j < sizesArray.size() / 2; j += 2) {
                    int size = sizesArray.get(j).getAsInt();
                    int count = sizesArray.get(j + 1).getAsInt();
                    simulatedMobDrop.simulatedStackSize.get(i).put(size, count);
                }
            }
        }

        return simulatedMobDrop;
    }

}
