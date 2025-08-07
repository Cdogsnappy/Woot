package ipsis.woot.modules.fluidconvertor;

import ipsis.woot.Woot;
import ipsis.woot.modules.fluidconvertor.blocks.FluidConvertorBlock;
import ipsis.woot.modules.fluidconvertor.blocks.FluidConvertorMenu;
import ipsis.woot.modules.fluidconvertor.blocks.FluidConvertorBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class FluidConvertorSetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Woot.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Woot.MODID);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("FluidConvertor: register");
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES.register(eventBus);
        CONTAINERS.register(eventBus);
    }

    public static final String FLUID_CONVERTOR_TAG = "fluidconvertor";
    public static final DeferredHolder<Block, FluidConvertorBlock> FLUID_CONVERTOR_BLOCK = BLOCKS.register(
            FLUID_CONVERTOR_TAG, () -> new FluidConvertorBlock());
    public static final DeferredHolder<Item, BlockItem> FLUID_CONVERTOR_ITEM = ITEMS.register(
            FLUID_CONVERTOR_TAG, () ->
                    new BlockItem(FLUID_CONVERTOR_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidConvertorBlockEntity>> FLUID_CONVERTOR_BLOCK_TILE = TILES.register(
            FLUID_CONVERTOR_TAG, () ->
                    BlockEntityType.Builder.of(FluidConvertorBlockEntity::new,
                            FLUID_CONVERTOR_BLOCK.get()).build((null)));
    public static final DeferredHolder<MenuType<?>, MenuType<FluidConvertorMenu>> FLUID_CONVERTOR_BLOCK_CONTATAINER = CONTAINERS.register(
            FLUID_CONVERTOR_TAG, () ->
                    IForgeContainerType.create((windowId, inv, data) -> {
                        return new FluidConvertorMenu(
                                windowId,
                                Woot.proxy.getClientWorld(),
                                data.readBlockPos(),
                                inv,
                                Woot.proxy.getClientPlayer());
                    }));
}
