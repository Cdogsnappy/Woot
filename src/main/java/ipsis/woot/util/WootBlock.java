package ipsis.woot.util;

import ipsis.woot.Woot;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.property.Properties;

public class WootBlock extends Block {

    public WootBlock(Properties properties, String name) {
        super(properties);
        setRegistryName(Woot.MODID, name);
    }
}
