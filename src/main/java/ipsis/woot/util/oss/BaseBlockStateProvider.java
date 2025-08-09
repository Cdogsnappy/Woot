package ipsis.woot.util.oss;

import ipsis.woot.Woot;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * A subset of McJty's BaseBlockStateProvider from McJtyLib
 */
public abstract class BaseBlockStateProvider extends BlockStateProvider {

    public BaseBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen.getPackOutput(), modid, exFileHelper);
    }

    private ModelFile frontBasedModel(String modelName, ResourceLocation texture) {
        return models().orientable(
                modelName,
                ResourceLocation.fromNamespaceAndPath(Woot.MODID, "block/factory"),
                texture,
                ResourceLocation.fromNamespaceAndPath(Woot.MODID, "block/factory"));
    }

    private ModelFile sideOnlyBasedModel(String modelName, ResourceLocation texture) {
        return models().cubeBottomTop(modelName,
                texture,
                ResourceLocation.fromNamespaceAndPath(Woot.MODID, "block/factory"),
                ResourceLocation.fromNamespaceAndPath(Woot.MODID, "block/factory"));
    }

    public void singleTextureBlock(Block block, String modelName, String textureName) {
        ModelFile model = models().cubeAll(modelName, modLoc(textureName));
        simpleBlock(block, model);
    }

    public void machineBlock(Block block, String modelName, String textureName) {
        ModelFile modelFile = frontBasedModel(modelName, modLoc(textureName));
        horizontalBlock(block,
                ResourceLocation.fromNamespaceAndPath(Woot.MODID, "block/factory"),
                modLoc(textureName),
                ResourceLocation.fromNamespaceAndPath(Woot.MODID, "block/factory"));
    }

    public void sideOnlyBlock(Block block, String modelName, String textureName) {
        ModelFile modelFile = sideOnlyBasedModel(modelName, modLoc(textureName));
        simpleBlock(block, modelFile);
    }
}
