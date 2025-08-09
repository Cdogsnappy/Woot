package ipsis.woot.datagen;

import ipsis.woot.Woot;
import ipsis.woot.modules.factory.FactorySetup;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;


public class BlockTagsGen extends IntrinsicHolderTagsProvider<Block> {

    public static final TagKey<Block> FACTORY_BLOCK = TagKey.create(Registries.BLOCK, ResourceLocation.parse(Woot.MODID + ":factory_blocks"));

    public BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                        ExistingFileHelper existingFileHelper) {
        super(output, Registries.BLOCK, lookupProvider,
                block -> block.builtInRegistryHolder().key(), Woot.MODID, existingFileHelper);
    }


    @Override
    public String getName() {
        return "Woot Block Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_A_BLOCK.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_B_BLOCK.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_C_BLOCK.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_D_BLOCK.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_E_BLOCK.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_CONNECT_BLOCK.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_CTR_BASE_PRI_BLOCK.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_CTR_BASE_SEC_BLOCK.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_UPGRADE_BLOCK.get());

    }


}
