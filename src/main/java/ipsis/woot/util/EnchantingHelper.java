package ipsis.woot.util;

import ipsis.woot.Woot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantingHelper {

    /**
     * Vanilla enchantment helper uses both the level and player experience to decide
     * on suitability of the enchantment. This one only cares about applying an
     * enchant of a specific level
     * Always reutrns an enchanted book
     * Manually check with "data get entity @s SelectedItem"
     */
    public static ItemStack addRandomBookEnchant(ItemStack itemStack, int level) {

        if (itemStack.getItem() != Items.BOOK)
            return new ItemStack(Items.ENCHANTED_BOOK);

        List<EnchantmentData> enchantments = new ArrayList<>();
        for (int l = level; enchantments.isEmpty() && l >= 1; l++) {
            List<EnchantmentData> list = getEnchantsAtLevel(l);
            if (!list.isEmpty())
                enchantments = list;
        }

        itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        if (!enchantments.isEmpty()) {
            //enchantments.forEach(h -> Woot.LOGGER.info("Enchant {} Level {}", h.enchantment, h.enchantmentLevel));
            EnchantmentData d = WeightedRandom.getRandomItem(new Random(), enchantments);

            // StoredEnchantments nbt
            EnchantedBookItem.addEnchantment(itemStack, d);

            // Enchantments nbt
            // itemStack.addEnchantment(d.enchantment, d.enchantmentLevel);
        }
        return itemStack;
    }

    private static List<EnchantmentData> getEnchantsAtLevel(int level) {
        List<EnchantmentData> list = new ArrayList<>();
        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            if (!enchantment.isTreasureEnchantment() && enchantment.isAllowedOnBooks()) {
                if (level >= enchantment.getMinLevel() && level <= enchantment.getMaxLevel())
                    list.add(new EnchantmentData(enchantment, level));
            }
        }
        return list;
    }
}
