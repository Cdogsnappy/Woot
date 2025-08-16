package ipsis.woot.modules.squeezer;

import ipsis.woot.Woot;
import ipsis.woot.modules.squeezer.blocks.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.stream.Collectors;


public class SqueezerSetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Woot.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Woot.MODID);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("SqueezerSetup: register");
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES.register(eventBus);
        CONTAINERS.register(eventBus);
    }

    public static final String SQUEEZER_TAG = "squeezer";
    public static final DeferredHolder<Block, DyeSqueezerBlock> SQUEEZER_BLOCK = BLOCKS.register(
            SQUEEZER_TAG, () -> new DyeSqueezerBlock(BlockBehaviour.Properties.of()));
    public static final DeferredHolder<Item, BlockItem> SQUEEZER_BLOCK_ITEM = ITEMS.register(
            SQUEEZER_TAG, () ->
                    new BlockItem(SQUEEZER_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DyeSqueezerBlockEntity>> SQUEEZER_BLOCK_TILE = TILES.register(
            SQUEEZER_TAG, () ->
                    BlockEntityType.Builder.of(DyeSqueezerBlockEntity::new, SQUEEZER_BLOCK.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<DyeSqueezerContainer>> SQUEEZER_BLOCK_CONTAINER = CONTAINERS.register(
            SQUEEZER_TAG, () -> IMenuTypeExtension.create(DyeSqueezerContainer::new));

    public static final String ENCHANT_SQUEEZER_TAG = "enchsqueezer";
    public static final DeferredHolder<Block, EnchantSqueezerBlock> ENCHANT_SQUEEZER_BLOCK = BLOCKS.register(
            ENCHANT_SQUEEZER_TAG, EnchantSqueezerBlock::new);
    public static final DeferredHolder<Item, BlockItem> ENCHANT_SQUEEZER_BLOCK_ITEM = ITEMS.register(
            ENCHANT_SQUEEZER_TAG, () ->
                    new BlockItem(ENCHANT_SQUEEZER_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchantSqueezerBlockEntity>> ENCHANT_SQUEEZER_BLOCK_TILE = TILES.register(
            ENCHANT_SQUEEZER_TAG, () ->
                    BlockEntityType.Builder.of(EnchantSqueezerBlockEntity::new, ENCHANT_SQUEEZER_BLOCK.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<EnchantSqueezerMenu>> ENCHANT_SQUEEZER_BLOCK_CONTAINER = CONTAINERS.register(
            ENCHANT_SQUEEZER_TAG, () -> IMenuTypeExtension.create(EnchantSqueezerMenu::new));


    public static List<Item> getItems(){
        return ITEMS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toUnmodifiableList());
    }
}
