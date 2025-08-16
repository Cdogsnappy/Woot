package ipsis.woot.modules.factory.items;

import ipsis.woot.modules.factory.*;
import ipsis.woot.modules.factory.perks.Helper;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;


import java.util.List;

public class PerkItem extends Item {

    public static final String EFFICIENCY_1_REGNAME = "efficiency_1";
    public static final String EFFICIENCY_2_REGNAME = "efficiency_2";
    public static final String EFFICIENCY_3_REGNAME = "efficiency_3";
    public static final String LOOTING_1_REGNAME = "looting_1";
    public static final String LOOTING_2_REGNAME = "looting_2";
    public static final String LOOTING_3_REGNAME = "looting_3";
    public static final String MASS_1_REGNAME = "mass_1";
    public static final String MASS_2_REGNAME = "mass_2";
    public static final String MASS_3_REGNAME = "mass_3";
    public static final String RATE_1_REGNAME = "rate_1";
    public static final String RATE_2_REGNAME = "rate_2";
    public static final String RATE_3_REGNAME = "rate_3";
    public static final String XP_1_REGNAME = "xp_1";
    public static final String XP_2_REGNAME = "xp_2";
    public static final String XP_3_REGNAME = "xp_3";
    public static final String TIER_SHARD_1_REGNAME = "tier_shard_1";
    public static final String TIER_SHARD_2_REGNAME = "tier_shard_2";
    public static final String TIER_SHARD_3_REGNAME = "tier_shard_3";
    public static final String HEADLESS_1_REGNAME = "headless_1";
    public static final String HEADLESS_2_REGNAME = "headless_2";
    public static final String HEADLESS_3_REGNAME = "headless_3";
    public static final String SLAUGHTER_1_REGNAME = "slaughter_1";
    public static final String SLAUGHTER_2_REGNAME = "slaughter_2";
    public static final String SLAUGHTER_3_REGNAME = "slaughter_3";
    public static final String CRUSHER_1_REGNAME = "crusher_1";
    public static final String CRUSHER_2_REGNAME = "crusher_2";
    public static final String CRUSHER_3_REGNAME = "crusher_3";
    public static final String LASER_1_REGNAME = "laser_1";
    public static final String LASER_2_REGNAME = "laser_2";
    public static final String LASER_3_REGNAME = "laser_3";
    public static final String FLAYED_1_REGNAME = "flayed_1";
    public static final String FLAYED_2_REGNAME = "flayed_2";
    public static final String FLAYED_3_REGNAME = "flayed_3";

    final Perk perk;

    public PerkItem(Perk perk) {
        super(new Item.Properties());
        this.perk = perk;
    }

    public Perk getFactoryUpgrade() { return this.perk; }

    public static ItemStack getItemStack(Perk perk) {

        if (perk == Perk.efficiency_1)
            return new ItemStack(FactorySetup.EFFICIENCY_1_ITEM.get());
        else if (perk == Perk.efficiency_2)
            return new ItemStack(FactorySetup.EFFICIENCY_2_ITEM.get());
        else if (perk == Perk.efficiency_3)
            return new ItemStack(FactorySetup.EFFICIENCY_3_ITEM.get());
        else if (perk == Perk.looting_1)
            return new ItemStack(FactorySetup.LOOTING_1_ITEM.get());
        else if (perk == Perk.looting_2)
            return new ItemStack(FactorySetup.LOOTING_2_ITEM.get());
        else if (perk == Perk.looting_3)
            return new ItemStack(FactorySetup.LOOTING_3_ITEM.get());
        else if (perk == Perk.mass_1)
            return new ItemStack(FactorySetup.MASS_1_ITEM.get());
        else if (perk == Perk.mass_2)
            return new ItemStack(FactorySetup.MASS_2_ITEM.get());
        else if (perk == Perk.mass_3)
            return new ItemStack(FactorySetup.MASS_3_ITEM.get());
        else if (perk == Perk.rate_1)
            return new ItemStack(FactorySetup.RATE_1_ITEM.get());
        else if (perk == Perk.rate_2)
            return new ItemStack(FactorySetup.RATE_2_ITEM.get());
        else if (perk == Perk.rate_3)
            return new ItemStack(FactorySetup.RATE_3_ITEM.get());
        else if (perk == Perk.tier_shard_1)
            return new ItemStack(FactorySetup.TIER_SHARD_1_ITEM.get());
        else if (perk == Perk.tier_shard_2)
            return new ItemStack(FactorySetup.TIER_SHARD_2_ITEM.get());
        else if (perk == Perk.tier_shard_3)
            return new ItemStack(FactorySetup.TIER_SHARD_3_ITEM.get());
        else if (perk == Perk.xp_1)
            return new ItemStack(FactorySetup.XP_1_ITEM.get());
        else if (perk == Perk.xp_2)
            return new ItemStack(FactorySetup.XP_2_ITEM.get());
        else if (perk == Perk.xp_3)
            return new ItemStack(FactorySetup.XP_3_ITEM.get());
        else if (perk == Perk.headless_1)
            return new ItemStack(FactorySetup.HEADLESS_1_ITEM.get());
        else if (perk == Perk.headless_2)
            return new ItemStack(FactorySetup.HEADLESS_2_ITEM.get());
        else if (perk == Perk.headless_3)
            return new ItemStack(FactorySetup.HEADLESS_3_ITEM.get());
        else if (perk == Perk.slaughter_1)
            return new ItemStack(FactorySetup.SLAUGHTER_1_ITEM.get());
        else if (perk == Perk.slaughter_2)
            return new ItemStack(FactorySetup.SLAUGHTER_2_ITEM.get());
        else if (perk == Perk.slaughter_3)
            return new ItemStack(FactorySetup.SLAUGHTER_3_ITEM.get());
        else if (perk == Perk.crusher_1)
            return new ItemStack(FactorySetup.CRUSHER_1_ITEM.get());
        else if (perk == Perk.crusher_2)
            return new ItemStack(FactorySetup.CRUSHER_2_ITEM.get());
        else if (perk == Perk.crusher_3)
            return new ItemStack(FactorySetup.CRUSHER_3_ITEM.get());
        else if (perk == Perk.laser_1)
            return new ItemStack(FactorySetup.LASER_1_ITEM.get());
        else if (perk == Perk.laser_2)
            return new ItemStack(FactorySetup.LASER_2_ITEM.get());
        else if (perk == Perk.laser_3)
            return new ItemStack(FactorySetup.LASER_3_ITEM.get());

        else if (perk == Perk.flayed_1)
            return new ItemStack(FactorySetup.FLAYED_1_ITEM.get());
        else if (perk == Perk.flayed_2)
            return new ItemStack(FactorySetup.FLAYED_2_ITEM.get());
        else if (perk == Perk.flayed_3)
            return new ItemStack(FactorySetup.FLAYED_3_ITEM.get());

        return ItemStack.EMPTY;
    }

    public static ItemStack getItemStack(Perk.Group type, int level) {

        if (level < 1 || level > 3)
            return ItemStack.EMPTY;

        return getItemStack(Helper.getPerk(type, level));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        tooltip.add(Helper.getTooltip(perk));
        tooltip.addAll(Helper.getTooltip(perk, Perk.getLevel(perk)));
        if (Perk.TIER_SHARD_PERKS.contains(perk)) {
            {
                // tier 1 factory
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.1",
                        StringHelper.translate(Tier.TIER_1.getTranslationKey()),
                        FactoryConfiguration.T1_FARM_DROP_CHANCE.get())));
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.2",
                        FactoryConfiguration.T1_FARM_DROP_SHARD_WEIGHTS.get().get(0),
                        FactoryConfiguration.T1_FARM_DROP_SHARD_WEIGHTS.get().get(1),
                        FactoryConfiguration.T1_FARM_DROP_SHARD_WEIGHTS.get().get(2))));
            }
            {
                // tier 2 factory
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.1",
                        StringHelper.translate(Tier.TIER_2.getTranslationKey()),
                        FactoryConfiguration.T2_FARM_DROP_CHANCE.get())));
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.2",
                        FactoryConfiguration.T2_FARM_DROP_SHARD_WEIGHTS.get().get(0),
                        FactoryConfiguration.T2_FARM_DROP_SHARD_WEIGHTS.get().get(1),
                        FactoryConfiguration.T2_FARM_DROP_SHARD_WEIGHTS.get().get(2))));
            }
            {
                // tier 3 factory
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.1",
                        StringHelper.translate(Tier.TIER_3.getTranslationKey()),
                        FactoryConfiguration.T3_FARM_DROP_CHANCE.get())));
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.2",
                        FactoryConfiguration.T3_FARM_DROP_SHARD_WEIGHTS.get().get(0),
                        FactoryConfiguration.T3_FARM_DROP_SHARD_WEIGHTS.get().get(1),
                        FactoryConfiguration.T3_FARM_DROP_SHARD_WEIGHTS.get().get(2))));
            }
            {
                // tier 4 factory
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.1",
                        StringHelper.translate(Tier.TIER_4.getTranslationKey()),
                        FactoryConfiguration.T4_FARM_DROP_CHANCE.get())));
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.2",
                        FactoryConfiguration.T4_FARM_DROP_SHARD_WEIGHTS.get().get(0),
                        FactoryConfiguration.T4_FARM_DROP_SHARD_WEIGHTS.get().get(1),
                        FactoryConfiguration.T4_FARM_DROP_SHARD_WEIGHTS.get().get(2))));
            }
            {
                // tier 5 factory
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.1",
                        StringHelper.translate(Tier.TIER_5.getTranslationKey()),
                        FactoryConfiguration.T5_FARM_DROP_CHANCE.get())));
                tooltip.add(Component.translatable(StringHelper.translateFormat(
                        "info.woot.perk.tier_shard.2",
                        FactoryConfiguration.T5_FARM_DROP_SHARD_WEIGHTS.get().get(0),
                        FactoryConfiguration.T5_FARM_DROP_SHARD_WEIGHTS.get().get(1),
                        FactoryConfiguration.T5_FARM_DROP_SHARD_WEIGHTS.get().get(2))));
            }
        }
    }
}
