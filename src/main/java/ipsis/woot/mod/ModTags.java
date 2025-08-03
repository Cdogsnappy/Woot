package ipsis.woot.mod;

import ipsis.woot.Woot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks
    {
        public static final TagKey<Block> FACTORY_BLOCK = BlockTags.create(ResourceLocation.parse(Woot.MODID + ":" + "factory_blocks"));
    }

    public static class Items {
        public static final TagKey<Item> FACTORY_BLOCK = ItemTags.create(ResourceLocation.parse(Woot.MODID + ":" + "factory_blocks"));
    }
}
