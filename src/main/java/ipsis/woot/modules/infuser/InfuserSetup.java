package ipsis.woot.modules.infuser;

import ipsis.woot.Woot;
import ipsis.woot.modules.infuser.blocks.InfuserBlock;
import ipsis.woot.modules.infuser.blocks.InfuserContainer;
import ipsis.woot.modules.infuser.blocks.InfuserBlockEntity;
import ipsis.woot.modules.infuser.items.DyeCasingItem;
import ipsis.woot.modules.infuser.items.DyePlateItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class InfuserSetup {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Woot.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Woot.MODID);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("InfuserSetup: register");
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES.register(eventBus);
        CONTAINERS.register(eventBus);
    }

    public static final String INFUSER_TAG = "infuser";
    public static final DeferredHolder<Block, InfuserBlock> INFUSER_BLOCK = BLOCKS.register(
            INFUSER_TAG, () -> new InfuserBlock());
    public static final DeferredHolder<Item, BlockItem> INFUSER_BLOCK_ITEM = ITEMS.register(
            INFUSER_TAG, () ->
                    new BlockItem(INFUSER_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfuserBlockEntity>> INFUSER_BLOCK_TILE = TILES.register(
            INFUSER_TAG, () ->
                    BlockEntityType.Builder.of(InfuserBlockEntity::new, INFUSER_BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<InfuserContainer>> INFUSER_BLOCK_CONTAINER = CONTAINERS.register(
            INFUSER_TAG, () ->
                    IForgeMenuType.create((windowId, inv, data) -> {
                        return new InfuserContainer(
                                windowId,
                                Woot.proxy.getClientWorld(),
                                data.readBlockPos(),
                                inv,
                                Woot.proxy.getClientPlayer());
                    }));

    /**
     * Dye Shards
     */
    public static final DeferredHolder<Item, DyePlateItem> WHITE_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.WHITE);
    public static final DeferredHolder<Item, DyePlateItem> ORANGE_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.ORANGE);
    public static final DeferredHolder<Item, DyePlateItem> MAGENTA_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.MAGENTA);
    public static final DeferredHolder<Item, DyePlateItem> LIGHT_BLUE_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.LIGHT_BLUE);
    public static final DeferredHolder<Item, DyePlateItem> YELLOW_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.YELLOW);
    public static final DeferredHolder<Item, DyePlateItem> LIME_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.LIME);
    public static final DeferredHolder<Item, DyePlateItem> PINK_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.PINK);
    public static final DeferredHolder<Item, DyePlateItem> GRAY_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.GRAY);
    public static final DeferredHolder<Item, DyePlateItem> LIGHT_GRAY_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.LIGHT_GRAY);
    public static final DeferredHolder<Item, DyePlateItem> CYAN_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.CYAN);
    public static final DeferredHolder<Item, DyePlateItem> PURPLE_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.PURPLE);
    public static final DeferredHolder<Item, DyePlateItem> BLUE_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.BLUE);
    public static final DeferredHolder<Item, DyePlateItem> BROWN_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.BROWN);
    public static final DeferredHolder<Item, DyePlateItem> GREEN_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.GREEN);
    public static final DeferredHolder<Item, DyePlateItem> RED_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.RED);
    public static final DeferredHolder<Item, DyePlateItem> BLACK_DYE_PLATE_ITEM = getDyePlateItem(ITEMS, DyeColor.BLACK);

    private static DeferredHolder<Item, DyePlateItem> getDyePlateItem(DeferredRegister<Item> reg, DyeColor color) {
        return reg.register(color.getName() + "_dyeplate", () -> new DyePlateItem(color));
    }

    /**
     * Dye Casings
     */
    public static final DeferredHolder<Item, DyeCasingItem> WHITE_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.WHITE);
    public static final DeferredHolder<Item, DyeCasingItem> ORANGE_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.ORANGE);
    public static final DeferredHolder<Item, DyeCasingItem> MAGENTA_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.MAGENTA);
    public static final DeferredHolder<Item, DyeCasingItem> LIGHT_BLUE_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.LIGHT_BLUE);
    public static final DeferredHolder<Item, DyeCasingItem> YELLOW_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.YELLOW);
    public static final DeferredHolder<Item, DyeCasingItem> LIME_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.LIME);
    public static final DeferredHolder<Item, DyeCasingItem> PINK_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.PINK);
    public static final DeferredHolder<Item, DyeCasingItem> GRAY_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.GRAY);
    public static final DeferredHolder<Item, DyeCasingItem> LIGHT_GRAY_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.LIGHT_GRAY);
    public static final DeferredHolder<Item, DyeCasingItem> CYAN_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.CYAN);
    public static final DeferredHolder<Item, DyeCasingItem> PURPLE_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.PURPLE);
    public static final DeferredHolder<Item, DyeCasingItem> BLUE_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.BLUE);
    public static final DeferredHolder<Item, DyeCasingItem> BROWN_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.BROWN);
    public static final DeferredHolder<Item, DyeCasingItem> GREEN_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.GREEN);
    public static final DeferredHolder<Item, DyeCasingItem> RED_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.RED);
    public static final DeferredHolder<Item, DyeCasingItem> BLACK_DYE_CASING_ITEM = getDyeCasingItem(ITEMS, DyeColor.BLACK);

    private static DeferredHolder<Item, DyeCasingItem> getDyeCasingItem(DeferredRegister<Item> reg, DyeColor color) {
        return reg.register(color.getName() + "_dyecasing", () -> new DyeCasingItem(color));

    }
}
