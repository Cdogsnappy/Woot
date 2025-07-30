package ipsis.woot.util.helper;



import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PlayerHelper {

   public  static boolean playerHasFactoryComponent(Player playerEntity, List<ItemStack> validStacks) {
        if (playerEntity.isCreative())
            return true;

        for (ItemStack itemStack : playerEntity.getInventory().items) {
            if (itemStack.isEmpty())
                continue;

            for (ItemStack c : validStacks)
                if (c.getItem() == itemStack.getItem())
                    return true;
        }

        return false;
    }

    public static ItemStack takeFactoryComponent(Player playerEntity, List<ItemStack> validStacks) {
        if (validStacks.isEmpty())
            return ItemStack.EMPTY;

        if (playerEntity.isCreative())
            return validStacks.get(0);

        for (ItemStack itemStack : playerEntity.getInventory().items) {
            if (itemStack.isEmpty())
                continue;

            for (ItemStack c : validStacks) {
                if (c.getItem() == itemStack.getItem()) {
                    ItemStack returnStack = itemStack.copy();
                    itemStack.shrink(1);
                    playerEntity.getInventory().setChanged();
                    return returnStack;
                }
            }
        }

        return ItemStack.EMPTY;
    }
}
