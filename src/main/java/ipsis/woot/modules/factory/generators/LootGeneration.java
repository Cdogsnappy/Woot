package ipsis.woot.modules.factory.generators;

import ipsis.woot.modules.factory.FormedSetup;
import ipsis.woot.modules.factory.blocks.HeartBlockEntity;
import ipsis.woot.modules.factory.items.XpShardBaseItem;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.modules.generic.GenericSetup;
import ipsis.woot.policy.PolicyConfiguration;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.spawning.SpawnController;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.FakeMobKey;
import ipsis.woot.util.helper.RandomHelper;
import ipsis.woot.util.helper.StorageHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class LootGeneration {

    static final Logger LOGGER = LogManager.getLogger();
    static final RandomSource RANDOM = RandomSource.create();
    static final SkullGenerator SKULL_GENERATOR = new SkullGenerator();

    public static LootGeneration get() { return INSTANCE; }
    static LootGeneration INSTANCE;
    static { INSTANCE = new LootGeneration(); }

    public void loadFromConfig() {
        SKULL_GENERATOR.loadFromConfig(PolicyConfiguration.MOB_PERK_HEADLESS_SKULLS.get());
    }

    public void generate(HeartBlockEntity heartTileEntity, FormedSetup setup) {

        /**
         * Get the output options
         */
        List<Optional<IItemHandler>> itemHandlers = setup.getExportHandlers();
        List<Optional<IFluidHandler>> fluidHandlers = setup.getExportFluidHandlers();

        int looting = setup.getLootingLevel();

        // Drops
        List<ItemStack> rolledDrops = new ArrayList<>();
        for (FakeMob mob : setup.getAllMobs()) {
            int mobCount = setup.getAllMobParams().get(mob).getMobCount(setup.getAllPerks().containsKey(Perk.Group.mass), setup.hasMassExotic());
            //LOGGER.debug("generate: {} * {}", mob, mobCount);

            FakeMobKey fakeMobKey = new FakeMobKey(mob, looting);
            for (int i = 0; i < mobCount; i++)
                rolledDrops.addAll(MobSimulator.getInstance().getRolledDrops(fakeMobKey));




            for (ItemStack itemStack : rolledDrops) {
                if (itemStack.isDamageableItem()) {
                    int dmg = RandomHelper.RANDOM.nextInt(itemStack.getMaxDamage() + 1);
                    dmg = Math.clamp(dmg, 1, itemStack.getMaxDamage());
                    itemStack.setDamageValue(dmg);
                }
                if (itemStack.isEnchanted()) {
                    if (itemStack.has(DataComponents.ENCHANTMENTS))
                        itemStack.remove(DataComponents.ENCHANTMENTS);

                    float f = setup.getWorld().getCurrentDifficultyAt(heartTileEntity.getBlockPos()).getEffectiveDifficulty();
                    EnchantmentHelper.enchantItem(RandomHelper.RANDOM, itemStack,
                            (int)(5.0F + f * (float)RandomHelper.RANDOM.nextInt(18)),heartTileEntity.getLevel().registryAccess(),  Optional.empty());
                }
            }

            // Strip out all learned wool drops for vanilla sheep
            // Add custom drop wool drops
            if (fakeMobKey.getMob().isSheep()) {
                for (ItemStack itemStack : rolledDrops) {
                    if (WoolGenerator.isWoolDrop(itemStack))
                        itemStack.setCount(0);
                }

                for (int i = 0; i < mobCount; i++)
                    rolledDrops.add(WoolGenerator.getWoolDrop(fakeMobKey.getMob()));
            }
        }
        StorageHelper.insertItems(rolledDrops, itemHandlers);


        // Experience
        if (setup.getAllPerks().containsKey(Perk.Group.xp)) {
            int genXp = 0;
            for (FakeMob mob : setup.getAllMobs()) {
                int xpPercent = setup.getAllMobParams().get(mob).getPerkXpValue();
                int mobCount = setup.getAllMobParams().get(mob).getMobCount(setup.getAllPerks().containsKey(Perk.Group.mass), setup.hasMassExotic());
                int x = (int) ((SpawnController.get().getMobExperience(mob, setup.getWorld()) / 100.0F) * xpPercent);
                genXp += (x * mobCount);
            }

            // Unused xp just thrown away
            List<ItemStack> shards = XpShardBaseItem.getShards(genXp);
            StorageHelper.insertItems(shards, itemHandlers);
        }

        // Shard gen
        if (setup.getAllPerks().containsKey(Perk.Group.tier_shard)) {

            List<WeightedEntry.Wrapper<ItemStack>> shards = new ArrayList<>();
            shards.add(WeightedEntry.wrap(new ItemStack(GenericSetup.T1_SHARD_ITEM.get()), setup.getBasicShardWeight()));
            shards.add(WeightedEntry.wrap(new ItemStack(GenericSetup.T2_SHARD_ITEM.get()), setup.getAdvancedShardWeight()));
            shards.add(WeightedEntry.wrap(new ItemStack(GenericSetup.T3_SHARD_ITEM.get()), setup.getEliteShardWeight()));

            int rolls = setup.getPerkTierShardValue();
            List<ItemStack> dropShards = new ArrayList<>();

            //Woot.setup.getLogger().debug("Shard gen installed");
            //Woot.setup.getLogger().debug("Level:{} Drop:{} Basic:{} Advanced:{} Elite:{} Rolls:{}",
            //        setup.getAllPerks().get(PerkType.TIER_SHARD),
            //        setup.getShardDropChance(), setup.getBasicShardWeight(), setup.getAdvancedShardWeight(), setup.getEliteShardWeight(), rolls);

            for (int i = 0; i < rolls; i++) {
                if (RandomHelper.rollPercentage(setup.getShardDropChance(), "shardGen")) {
                    ItemStack chosenShard = WeightedRandom.getRandomItem(RANDOM, shards).get().data();
                    dropShards.add(chosenShard);
                }
            }

            // Unused shards just thrown away
            StorageHelper.insertItems(dropShards, itemHandlers);
        }

        // Skull gen
        if (setup.getAllPerks().containsKey(Perk.Group.headless)) {
            List<ItemStack> skulls = new ArrayList<>();
            List<FakeMob> countAdjustedMobParams = setup.getAllMobs().stream().map(m -> {
                int mobCount = setup.getAllMobParams().get(m).getMobCount(setup.getAllPerks().containsKey(Perk.Group.mass), setup.hasMassExotic());
                return Collections.nCopies(mobCount, m);
            }).flatMap(List::stream).collect(Collectors.toList());
            countAdjustedMobParams.forEach(m -> skulls.add(SKULL_GENERATOR.getSkullDrop(m, setup.getAllMobParams().get(m).getPerkHeadlessValue())));
            StorageHelper.insertItems(skulls, itemHandlers);
        }

        // Industrial Foregoing


    }
}
