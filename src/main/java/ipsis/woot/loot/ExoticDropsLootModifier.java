package ipsis.woot.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ipsis.woot.Woot;
import ipsis.woot.modules.factory.Exotic;
import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.util.helper.RandomHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Testing
 * /execute in minecraft:the_end run teleport ~ ~ ~
 * /locate endcity
 * /loot insert x y z loot minecraft:chests/end_city_treasure
 * /setblock ~ ~ ~ minecraft:chest{LootTable:"minecraft:chests/end_city_treasure"}
 */

public class ExoticDropsLootModifier extends LootModifier {

    private int rolls;
    private double dropChance;
    private int[] weights;

    private List<WeightedEntry.Wrapper<ItemStack>> drops = new ArrayList<>();

    public ExoticDropsLootModifier(LootItemCondition[] conditions, int rolls, double chance, int[] weights) {
        super(conditions);
        // pull the rolls and drop chances from the config
        this.rolls = rolls;
        this.dropChance = FactoryConfiguration.EXOTIC.get();
        this.weights = weights;
        drops.add(WeightedEntry.wrap(Exotic.EXOTIC_A.getItemStack(), weights[0]));
        drops.add(WeightedEntry.wrap(Exotic.EXOTIC_B.getItemStack(), weights[1]));
        drops.add(WeightedEntry.wrap(Exotic.EXOTIC_C.getItemStack(), weights[2]));
        drops.add(WeightedEntry.wrap(Exotic.EXOTIC_D.getItemStack(), weights[3]));
        drops.add(WeightedEntry.wrap(Exotic.EXOTIC_E.getItemStack(), weights[4]));
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (int roll = 0; roll < rolls; roll++) {
            if (RandomHelper.rollPercentage(dropChance, "exoticDrops")) {
                ItemStack drop = getWeightedDrop();
                if (!drop.isEmpty())
                    generatedLoot.add(drop.copy());
                else
                    Woot.setup.getLogger().error("Rolled exotic dropping as empty stack");
            }
        }
        return generatedLoot;
    }

    private ItemStack getWeightedDrop() {
        if (WeightedRandom.getTotalWeight(drops) > 0) {
            ItemStack item = WeightedRandom.getRandomItem(RandomHelper.RANDOM, drops).get().data();

        }
        return ItemStack.EMPTY;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return MAP_CODEC;
    }

    public static final MapCodec<ExoticDropsLootModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    LootModifier.LOOT_CONDITIONS_CODEC.fieldOf("loot").forGetter(modifier -> modifier.conditions),
                    Codec.INT.fieldOf("rolls").forGetter(modifier -> modifier.rolls),
                    Codec.DOUBLE.fieldOf("chance").forGetter(modifier -> modifier.dropChance),
                    Codec.INT.listOf().fieldOf("weights").forGetter(modifier -> Arrays.stream(modifier.weights).boxed().toList())

            ).apply(inst, (l, r, c, w) -> new ExoticDropsLootModifier(l,r,c,w.stream().mapToInt(Integer::intValue).toArray())));


}
