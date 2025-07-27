package ipsis.woot.util;



import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;


/**
 * This is from McJty. src/main/java/mcjty/lib/container/GenericContainer.java
 * It splits the integer over two shorts which are synced via the standard vanilla code
 * Vanilla does not send integer data over for the containrs
 */

public class WootContainer extends AbstractContainerMenu {

    protected WootContainer(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    public void addShortListener(DataSlot holder) {
        addDataSlot(holder);
    }

    public void addIntegerListener(DataSlot holder) {
        addDataSlot(new DataSlot() {
            private int lastKnown;

            @Override
            public int get() {
                return holder.get() & 0xffff;
            }

            @Override
            public void set(int val) {
                int full = holder.get();
                holder.set((full & 0xffff0000) | (val & 0xffff));
            }

            @Override
            public boolean checkAndClearUpdateFlag() {
                int i = this.get();
                boolean flag = i != this.lastKnown;
                this.lastKnown = i;
                return flag;
            }

        });
        addDataSlot(new DataSlot() {
            private int lastKnown;

            @Override
            public int get() {
                return (holder.get() >> 16) & 0xffff;
            }

            @Override
            public void set(int val) {
                int full = holder.get();
                holder.set((full & 0x0000ffff) | ((val & 0xffff) << 16));
            }

            @Override
            public boolean checkAndClearUpdateFlag() {
                int i = this.get();
                boolean flag = i != this.lastKnown;
                this.lastKnown = i;
                return flag;
            }
        });
    }



    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
