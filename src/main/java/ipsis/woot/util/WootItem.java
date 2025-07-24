package ipsis.woot.util;

import ipsis.woot.Woot;
import ipsis.woot.setup.ModSetup;
import net.minecraft.world.item.Item;


public class WootItem extends Item {

    public WootItem(Properties properties, String name) {
        super(properties);
        setRegistryName(Woot.MODID, name);
    }
}
