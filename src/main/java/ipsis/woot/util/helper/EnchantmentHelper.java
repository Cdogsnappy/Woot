package ipsis.woot.util.helper;



import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

/**
 * Vanilla has multiple tag schemes for enchanted "items"
 * The ItemStack.isEnchanted only checks for the !book version
 *
 * Enchanted Books -> StoredEnchantments id/(short)lvl
 * Enchanted Item  -> Enchantments id/(short)lvl
 */
public class EnchantmentHelper {

    public static boolean isEnchanted(@Nullable ItemStack itemStack) {
        if(itemStack == null){
            return false;
        }
        return itemStack.isEnchanted();
    }
}
