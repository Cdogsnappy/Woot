package ipsis.woot.modules.debug;

import ipsis.woot.Woot;
import ipsis.woot.modules.debug.blocks.*;
import ipsis.woot.modules.debug.items.DebugItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DebugSetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Woot.MODID);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("DebugSetup: register");
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES.register(eventBus);
    }

    public static final DeferredHolder<Item, DebugItem> DEBUG_ITEM = ITEMS.register(
            "debug", () -> new DebugItem());

    public static final String CREATIVE_POWER_TAG = "creative_power";
    public static final DeferredHolder<Block, CreativePowerBlock> CREATIVE_POWER_BLOCK = BLOCKS.register(
            CREATIVE_POWER_TAG, () -> new CreativePowerBlock());
    public static final DeferredHolder<Item, BlockItem> CREATIVE_POWER_BLOCK_ITEM = ITEMS.register(
            CREATIVE_POWER_TAG, () ->
                    new BlockItem(CREATIVE_POWER_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativePowerBlockEntity>> CREATIVE_POWER_BLOCK_TILE = TILES.register(
            CREATIVE_POWER_TAG, () ->
                    BlockEntityType.Builder.of(CreativePowerBlockEntity::new, CREATIVE_POWER_BLOCK.get()).build(null));

    public static final String CREATIVE_CONATUS_TAG = "creative_conatus";
    public static final DeferredHolder<Block, TickConvertorBlock> CREATIVE_CONATUS_BLOCK = BLOCKS.register(
            CREATIVE_CONATUS_TAG, () -> new TickConvertorBlock());
    public static final DeferredHolder<Item, BlockItem> CREATIVE_CONATUS_BLOCK_ITEM = ITEMS.register(
            CREATIVE_CONATUS_TAG, () ->
                    new BlockItem(CREATIVE_CONATUS_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TickConvertorBlockEntity>> CREATIVE_CONATUS_BLOCK_TILE = TILES.register(
            CREATIVE_CONATUS_TAG, () ->
                    BlockEntityType.Builder.of(TickConvertorBlockEntity::new, CREATIVE_CONATUS_BLOCK.get()).build(null));

    public static final String DEBUG_TANK_TAG = "debug_tank";
    public static final DeferredHolder<Block, DebugTankBlock> DEBUG_TANK_BLOCK = BLOCKS.register(
            DEBUG_TANK_TAG, () -> new DebugTankBlock());
    public static final DeferredHolder<Item, BlockItem> DEBUG_TANK_BLOCK_ITEM = ITEMS.register(
            DEBUG_TANK_TAG, () ->
                    new BlockItem(DEBUG_TANK_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DebugTankBlockEntity>> DEBUG_TANK_BLOCK_TILE = TILES.register(
            DEBUG_TANK_TAG, () ->
                    BlockEntityType.Builder.of(DebugTankBlockEntity::new, DEBUG_TANK_BLOCK.get()).build(null));
}
