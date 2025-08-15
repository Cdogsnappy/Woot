package ipsis.woot.modules.infuser.items;


import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class DyePlateItem extends Item {

    final DyeColor color;

    public DyePlateItem(DyeColor color) {
        super(new Item.Properties().stacksTo(64));
        this.color = color;
    }

    public DyeColor getColor() {
        return this.color; }
}
