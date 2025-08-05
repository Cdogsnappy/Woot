package ipsis.woot.datagen;

import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(true, new Recipes(generator.getPackOutput(), event.getLookupProvider()));
            BlockTagsProvider blockTagsProvider = new BlockTagsGen(generator, event.getExistingFileHelper());
            generator.addProvider(true, blockTagsProvider);
            generator.addProvider(new ItemTagsGen(generator, blockTagsProvider, event.getExistingFileHelper()));
            generator.addProvider(new Advancements(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new Items(generator, event.getExistingFileHelper()));
            generator.addProvider(new Blocks(generator, event.getExistingFileHelper()));
            generator.addProvider(new Languages(generator, "en_us"));
        }
    }
}
