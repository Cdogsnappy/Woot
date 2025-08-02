package ipsis.woot.util.oss;


import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


/**
 * McJtyMods/McJtyLib/1.15/src/main/java/mcjty/lib/datagen/BaseItemModelProvider.java
 */

public abstract class BaseItemModelProvider extends ItemModelProvider {

    public BaseItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), modid, existingFileHelper);
    }

    public void parentedBlock(Block block, String model) {
        getBuilder(block.getDescriptionId())
                .parent(new ModelFile.UncheckedModelFile(modLoc(model)));
    }

    public void parentedItem(Item item, String model) {
        getBuilder(item.getDescriptionId())
                .parent(new ModelFile.UncheckedModelFile(modLoc(model)));
    }

    public void itemGenerated(Item item, String texture) {
        getBuilder(item.getDescriptionId()).parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", texture);
    }

    public void itemHandheld(Item item, String texture) {
        getBuilder(item.getDescriptionId()).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", texture);
    }


}
