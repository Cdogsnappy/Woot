package ipsis.woot.util;

import ipsis.woot.Woot;
import ipsis.woot.util.helper.RandomHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;


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
    public static ItemStack addRandomBookEnchant(ItemStack itemStack, int level, RegistryAccess access) {

        if (itemStack.getItem() != Items.BOOK)
            return new ItemStack(Items.ENCHANTED_BOOK);

        List<EnchantmentInstance> enchantments = new ArrayList<>();
        for (int l = level; enchantments.isEmpty() && l >= 1; l++) {
            List<EnchantmentInstance> list = getEnchantsAtLevel(l, access);
            if (!list.isEmpty())
                enchantments = list;
        }

        itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        if (!enchantments.isEmpty()) {
            //enchantments.forEach(h -> Woot.LOGGER.info("Enchant {} Level {}", h.enchantment, h.enchantmentLevel));
            EnchantmentInstance d = WeightedRandom.getRandomItem(RandomHelper.RANDOM, enchantments).get();

            // StoredEnchantments nbt
            itemStack = EnchantedBookItem.createForEnchantment(d);

            // Enchantments nbt
            // itemStack.addEnchantment(d.enchantment, d.enchantmentLevel);
        }
        return itemStack;
    }

    private static List<EnchantmentInstance> getEnchantsAtLevel(int level, RegistryAccess access) {
        List<EnchantmentInstance> list = new ArrayList<>();
        Registry<Enchantment> enchantmentRegistry = access.registryOrThrow(Registries.ENCHANTMENT);
        for (Enchantment enchantment : enchantmentRegistry) {
                if (level >= enchantment.getMinLevel() && level <= enchantment.getMaxLevel())
                    list.add(new EnchantmentInstance(Holder.direct(enchantment), level));
        }
        return list;
    }
}
