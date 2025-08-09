package ipsis.woot.datagen;

import ipsis.woot.Woot;
import ipsis.woot.modules.factory.FactorySetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ItemTagsGen extends IntrinsicHolderTagsProvider<Item> {

    public static final TagKey<Item> FACTORY_BLOCK = ItemTags.create(ResourceLocation.parse(Woot.MODID + ":factory_blocks"));
    //public static final ITag.INamedTag<Item> BLACK_DYE = ItemTags.makeWrapperTag("forge:dyes/black");

    public ItemTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                       ExistingFileHelper existingFileHelper) {
        super(output, Registries.ITEM, lookupProvider,
                item -> item.builtInRegistryHolder().key(), Woot.MODID, existingFileHelper);
    }


    // Straight from forge
    private void addColored(Consumer<TagKey<Item>> consumer, TagKey<Item> group, String pattern) {
        String prefix = group.location().getPath().toUpperCase(Locale.ENGLISH) + '_';
        for (DyeColor dyeColor : DyeColor.values()) {
            ResourceLocation key = ResourceLocation.fromNamespaceAndPath(Woot.MODID, pattern.replace("{color}", dyeColor.getName()));
            TagKey<Item> tag = getForgeItemTag(prefix + dyeColor.getName());
            Item item = BuiltInRegistries.ITEM.get(key);
            tag(tag).add(item);
            consumer.accept(tag);
        }
    }

    // Straight from forge
    private TagKey<Item> getForgeItemTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", name));
    }

    @Override
    public String getName() {
        return "Woot Item Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_A_BLOCK_ITEM.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_B_BLOCK_ITEM.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_C_BLOCK_ITEM.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_D_BLOCK_ITEM.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_E_BLOCK_ITEM.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_CONNECT_BLOCK_ITEM.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_CTR_BASE_PRI_BLOCK_ITEM.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_CTR_BASE_SEC_BLOCK_ITEM.get());
        tag(FACTORY_BLOCK).add(FactorySetup.FACTORY_UPGRADE_BLOCK_ITEM.get());

        addColored(iTag -> tag(Tags.Items.DYES).addTag(iTag) , Tags.Items.DYES, "{color}_dyeplate");

    }
}
