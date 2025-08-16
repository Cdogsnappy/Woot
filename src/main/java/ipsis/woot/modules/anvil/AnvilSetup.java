package ipsis.woot.modules.anvil;

import ipsis.woot.Woot;
import ipsis.woot.modules.anvil.blocks.AnvilBlock;
import ipsis.woot.modules.anvil.blocks.AnvilBlockEntity;
import ipsis.woot.modules.anvil.items.DieItem;
import ipsis.woot.modules.anvil.items.HammerItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.stream.Collectors;


public class AnvilSetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Woot.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, Woot.MODID);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("AnvilSetup: register");
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES.register(eventBus);
        MENUS.register(eventBus);
    }

    public static final String ANVIL_TAG = "anvil";
    public static final DeferredHolder<Block, AnvilBlock> ANVIL_BLOCK = BLOCKS.register(
            ANVIL_TAG, AnvilBlock::new);
    public static final DeferredHolder<Item, Item> ANVIL_BLOCK_ITEM = ITEMS.register(
            ANVIL_TAG, () ->
                    new BlockItem(ANVIL_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AnvilBlockEntity>> ANVIL_BLOCK_TILE = TILES.register(
            ANVIL_TAG, () ->
                    BlockEntityType.Builder.of(AnvilBlockEntity::new, ANVIL_BLOCK.get()).build(null));

    public static final DeferredHolder<Item, HammerItem> HAMMER_ITEM = ITEMS.register(
            "hammer", HammerItem::new);
    public static final DeferredHolder<Item, DieItem> PLATE_DIE_ITEM = ITEMS.register(
            "plate_die", () -> new DieItem(DieItem.DieType.PLATE));
    public static final DeferredHolder<Item, DieItem> SHARD_DIE_ITEM = ITEMS.register(
            "shard_die", () -> new DieItem(DieItem.DieType.SHARD));
    public static final DeferredHolder<Item, DieItem> DYE_DIE_ITEM = ITEMS.register(
            "dye_die", () -> new DieItem(DieItem.DieType.DYE));

    public static List<Item> getItems(){
        return ITEMS.getEntries().stream().map(DeferredHolder::get).collect(Collectors.toUnmodifiableList());
    }
}
