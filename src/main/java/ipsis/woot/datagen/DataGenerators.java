package ipsis.woot.datagen;

import ipsis.woot.Woot;
import ipsis.woot.simulator.WorldGenProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;


@EventBusSubscriber(modid= Woot.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(true, new Recipes(generator.getPackOutput(), event.getLookupProvider()));
            IntrinsicHolderTagsProvider<Block> blockTagsProvider = new BlockTagsGen(generator.getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper());
            generator.addProvider(true, blockTagsProvider);
            generator.addProvider(true, new WorldGenProvider(generator.getPackOutput(), event.getLookupProvider()));
        }
        if (event.includeClient()) {
            generator.addProvider(true, new Items(generator, event.getExistingFileHelper()));
            generator.addProvider(true, new Blocks(generator, event.getExistingFileHelper()));
            generator.addProvider(true, new Languages(generator.getPackOutput(), "en_us"));
        }
    }
}
