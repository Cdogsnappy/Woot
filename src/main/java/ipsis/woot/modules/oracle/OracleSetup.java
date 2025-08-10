package ipsis.woot.modules.oracle;

import ipsis.woot.Woot;
import ipsis.woot.modules.oracle.blocks.OracleBlock;
import ipsis.woot.modules.oracle.blocks.OracleContainer;
import ipsis.woot.modules.oracle.blocks.OracleTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.stream.Collectors;


public class OracleSetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Woot.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Woot.MODID);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("OracleSetup: register");
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILES.register(eventBus);
        CONTAINERS.register(eventBus);
    }

    public static final String ORACLE_TAG = "oracle";
    public static final DeferredHolder<Block, OracleBlock> ORACLE_BLOCK = BLOCKS.register(
            ORACLE_TAG, () -> new OracleBlock(BlockBehaviour.Properties.of()));
    public static final DeferredHolder<Item, Item> ORACLE_BLOCK_ITEM = ITEMS.register(
            ORACLE_TAG, () ->
                    new BlockItem(ORACLE_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ORACLE_BLOCK_TILE = TILES.register(
            ORACLE_TAG, () ->
                    BlockEntityType.Builder.of(OracleTileEntity::new, ORACLE_BLOCK.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<OracleContainer>> ORACLE_BLOCK_CONTAINER = CONTAINERS.register(
            ORACLE_TAG, () -> IMenuTypeExtension.create(OracleContainer::new));


    public static List<Item> getItems(){
        return ITEMS.getEntries().stream().map(i -> i.get()).collect(Collectors.toUnmodifiableList());
    }
}
