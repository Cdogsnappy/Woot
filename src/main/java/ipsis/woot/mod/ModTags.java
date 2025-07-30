package ipsis.woot.mod;

import ipsis.woot.Woot;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks
    {
        public static final Tag.INamedTag<Block> FACTORY_BLOCK = BlockTags.makeWrapperTag(Woot.MODID + ":" + "factory_blocks");
    }

    public static class Items {
        public static final ITag.INamedTag<Item> FACTORY_BLOCK = ItemTags.makeWrapperTag(Woot.MODID + ":" + "factory_blocks");
    }
}
