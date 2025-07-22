package ipsis.woot.modules.oracle;

import ipsis.woot.Woot;
import ipsis.woot.modules.oracle.blocks.OracleBlock;
import ipsis.woot.modules.oracle.blocks.OracleContainer;
import ipsis.woot.modules.oracle.blocks.OracleTileEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;


public class OracleSetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Woot.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Woot.MODID);

    public static void register() {
        Woot.setup.getLogger().info("OracleSetup: register");
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final String ORACLE_TAG = "oracle";
    public static final RegistryObject<OracleBlock> ORACLE_BLOCK = BLOCKS.register(
            ORACLE_TAG, () -> new OracleBlock());
    public static final RegistryObject<Item> ORACLE_BLOCK_ITEM = ITEMS.register(
            ORACLE_TAG, () ->
                    new BlockItem(ORACLE_BLOCK.get(), Woot.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> ORACLE_BLOCK_TILE = TILES.register(
            ORACLE_TAG, () ->
                    TileEntityType.Builder.create(OracleTileEntity::new, ORACLE_BLOCK.get()).build(null));

    public static final RegistryObject<ContainerType<OracleContainer>> ORACLE_BLOCK_CONTAINER = CONTAINERS.register(
            ORACLE_TAG, () ->
                    IForgeContainerType.create((windowId, inv, data) -> {
                        return new OracleContainer(
                                windowId,
                                Woot.proxy.getClientWorld(),
                                data.readBlockPos(),
                                inv,
                                Woot.proxy.getClientPlayer());
                    }));
}
