package ipsis.woot.simulator;


import net.minecraft.world.entity.ai.behavior.ShufflingList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DropStackData extends ShufflingList.WeightedEntry<ItemStack> {

    public int stackSize;
    public DropStackData(ItemStack data, int weight) {
        this.stackSize = stackSize;
    }

    @Override
    public String toString() {
        return "DropStackData{" +
                "stackSize=" + stackSize +
                ", itemWeight=" + itemWeight +
                '}';
    }
}
