package ipsis.woot.modules.factory;

import ipsis.woot.Woot;
import ipsis.woot.modules.factory.blocks.*;
import ipsis.woot.modules.factory.blocks.HeartBlock;
import ipsis.woot.modules.factory.blocks.HeartMenu;
import ipsis.woot.modules.factory.blocks.HeartBlockEntity;
import ipsis.woot.modules.factory.blocks.Cell1BlockEntity;
import ipsis.woot.modules.factory.blocks.Cell2BlockEntity;
import ipsis.woot.modules.factory.blocks.Cell3BlockEntity;
import ipsis.woot.modules.factory.blocks.CellBlock;
import ipsis.woot.modules.factory.items.ControllerBlockItem;
import ipsis.woot.modules.factory.items.MobShardItem;
import ipsis.woot.modules.factory.items.PerkItem;
import ipsis.woot.modules.factory.items.XpShardBaseItem;
import ipsis.woot.modules.factory.multiblock.MultiBlockBlockEntity;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.util.WootBaseEntityBlock;
import ipsis.woot.util.WootMachineBlockEntity;
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


public class FactorySetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCKENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Woot.MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Woot.MODID);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("FactorySetup: register");
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCKENTITIES.register(eventBus);
        CONTAINERS.register(eventBus);
    }

    public static final DeferredHolder<Block, WootBaseEntityBlock> WOOT_BASE_BLOCK = BLOCKS.register(
            "wootbaseblock", () -> new WootBaseEntityBlock(BlockBehaviour.Properties.of()));


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WootMachineBlockEntity>> WOOT_MACHINE_ENTITY =
            BLOCKENTITIES.register("wootmachineentity", () ->
                    BlockEntityType.Builder.of(WootMachineBlockEntity::new, WOOT_BASE_BLOCK.get()).build(null));

    public static final String HEART_TAG = "heart";
    public static final DeferredHolder<Block, HeartBlock> HEART_BLOCK = BLOCKS.register(
            HEART_TAG, () -> new HeartBlock(BlockBehaviour.Properties.of()));
    public static final DeferredHolder<Item, Item> HEART_BLOCK_ITEM = ITEMS.register(
            HEART_TAG, () ->
                    new BlockItem(HEART_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeartBlockEntity>> HEART_BLOCK_TILE = BLOCKENTITIES.register(
            HEART_TAG, () ->
                    BlockEntityType.Builder.of(HeartBlockEntity::new, HEART_BLOCK.get()).build(null));

    public static final DeferredHolder<MenuType<?>, MenuType<HeartMenu>> HEART_BLOCK_CONTAINER = CONTAINERS.register(
            HEART_TAG, () -> IMenuTypeExtension.create(HeartMenu::new));

    public static final String CONTROLLER_TAG = "controller";
    public static final DeferredHolder<Block, ControllerBlock> CONTROLLER_BLOCK = BLOCKS.register(
            CONTROLLER_TAG, () -> new ControllerBlock(BlockBehaviour.Properties.of()));
    public static final DeferredHolder<Item, Item> CONTROLLER_BLOCK_ITEM = ITEMS.register(
            CONTROLLER_TAG, () ->
                    new ControllerBlockItem(CONTROLLER_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ControllerBlockEntity>> CONTROLLER_BLOCK_TILE = BLOCKENTITIES.register(
            CONTROLLER_TAG, () ->
                    BlockEntityType.Builder.of(ControllerBlockEntity::new, CONTROLLER_BLOCK.get()).build(null));

    public static final DeferredHolder<Block, FactoryBlock> FACTORY_A_BLOCK = BLOCKS.register(
            FactoryBlock.FACTORY_A_REGNAME, () -> new FactoryBlock(FactoryComponent.FACTORY_A));
    public static final DeferredHolder<Item, Item> FACTORY_A_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.FACTORY_A_REGNAME, () ->
                    new BlockItem(FACTORY_A_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> FACTORY_B_BLOCK = BLOCKS.register(
            FactoryBlock.FACTORY_B_REGNAME, () -> new FactoryBlock(FactoryComponent.FACTORY_B));
    public static final DeferredHolder<Item, Item> FACTORY_B_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.FACTORY_B_REGNAME, () ->
                    new BlockItem(FACTORY_B_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> FACTORY_C_BLOCK = BLOCKS.register(
            FactoryBlock.FACTORY_C_REGNAME, () -> new FactoryBlock(FactoryComponent.FACTORY_C));
    public static final DeferredHolder<Item, Item> FACTORY_C_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.FACTORY_C_REGNAME, () ->
                    new BlockItem(FACTORY_C_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> FACTORY_D_BLOCK = BLOCKS.register(
            FactoryBlock.FACTORY_D_REGNAME, () -> new FactoryBlock(FactoryComponent.FACTORY_D));
    public static final DeferredHolder<Item, Item> FACTORY_D_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.FACTORY_D_REGNAME, () ->
                    new BlockItem(FACTORY_D_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> FACTORY_E_BLOCK = BLOCKS.register(
            FactoryBlock.FACTORY_E_REGNAME, () -> new FactoryBlock(FactoryComponent.FACTORY_E));
    public static final DeferredHolder<Item, Item> FACTORY_E_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.FACTORY_E_REGNAME, () ->
                    new BlockItem(FACTORY_E_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> CAP_A_BLOCK = BLOCKS.register(
            FactoryBlock.CAP_A_REGNAME, () -> new FactoryBlock(FactoryComponent.CAP_A));
    public static final DeferredHolder<Item, Item> CAP_A_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.CAP_A_REGNAME, () ->
                    new BlockItem(CAP_A_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> CAP_B_BLOCK = BLOCKS.register(
            FactoryBlock.CAP_B_REGNAME, () -> new FactoryBlock(FactoryComponent.CAP_B));
    public static final DeferredHolder<Item, Item> CAP_B_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.CAP_B_REGNAME, () ->
                    new BlockItem(CAP_B_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> CAP_C_BLOCK = BLOCKS.register(
            FactoryBlock.CAP_C_REGNAME, () -> new FactoryBlock(FactoryComponent.CAP_C));
    public static final DeferredHolder<Item, Item> CAP_C_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.CAP_C_REGNAME, () ->
                    new BlockItem(CAP_C_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> CAP_D_BLOCK = BLOCKS.register(
            FactoryBlock.CAP_D_REGNAME, () -> new FactoryBlock(FactoryComponent.CAP_D));
    public static final DeferredHolder<Item, Item> CAP_D_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.CAP_D_REGNAME, () ->
                    new BlockItem(CAP_D_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> FACTORY_CONNECT_BLOCK = BLOCKS.register(
            FactoryBlock.FACTORY_CONNECT_REGNAME, () -> new FactoryBlock(FactoryComponent.FACTORY_CONNECT));
    public static final DeferredHolder<Item, Item> FACTORY_CONNECT_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.FACTORY_CONNECT_REGNAME, () ->
                    new BlockItem(FACTORY_CONNECT_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> FACTORY_CTR_BASE_PRI_BLOCK = BLOCKS.register(
            FactoryBlock.FACTORY_CTR_BASE_PRI_REGNAME, () -> new FactoryBlock(FactoryComponent.FACTORY_CTR_BASE_PRI));
    public static final DeferredHolder<Item, Item> FACTORY_CTR_BASE_PRI_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.FACTORY_CTR_BASE_PRI_REGNAME, () ->
                    new BlockItem(FACTORY_CTR_BASE_PRI_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> FACTORY_CTR_BASE_SEC_BLOCK = BLOCKS.register(
            FactoryBlock.FACTORY_CTR_BASE_SEC_REGNAME, () -> new FactoryBlock(FactoryComponent.FACTORY_CTR_BASE_SEC));
    public static final DeferredHolder<Item, Item> FACTORY_CTR_BASE_SEC_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.FACTORY_CTR_BASE_SEC_REGNAME, () ->
                    new BlockItem(FACTORY_CTR_BASE_SEC_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> IMPORT_BLOCK = BLOCKS.register(
            FactoryBlock.IMPORT_REGNAME, () -> new FactoryBlock(FactoryComponent.IMPORT));
    public static final DeferredHolder<Item, Item> IMPORT_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.IMPORT_REGNAME, () ->
                    new BlockItem(IMPORT_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, FactoryBlock> EXPORT_BLOCK = BLOCKS.register(
            FactoryBlock.EXPORT_REGNAME, () -> new FactoryBlock(FactoryComponent.EXPORT));
    public static final DeferredHolder<Item, Item> EXPORT_BLOCK_ITEM = ITEMS.register(
            FactoryBlock.EXPORT_REGNAME, () ->
                    new BlockItem(EXPORT_BLOCK.get(), Woot.createStandardProperties()));



    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> MULTIBLOCK_BLOCK_TILE = BLOCKENTITIES.register(
            "multiblock", () ->
                    BlockEntityType.Builder.of(MultiBlockBlockEntity::new,
                            IMPORT_BLOCK.get(),
                            EXPORT_BLOCK.get(),
                            FACTORY_A_BLOCK.get(),
                            FACTORY_B_BLOCK.get(),
                            FACTORY_C_BLOCK.get(),
                            FACTORY_D_BLOCK.get(),
                            FACTORY_E_BLOCK.get(),
                            CAP_A_BLOCK.get(),
                            CAP_B_BLOCK.get(),
                            CAP_C_BLOCK.get(),
                            CAP_D_BLOCK.get(),
                            FACTORY_CONNECT_BLOCK.get(),
                            FACTORY_CTR_BASE_PRI_BLOCK.get(),
                            FACTORY_CTR_BASE_SEC_BLOCK.get()
                            ).build(null));

    public static final String FACTORY_UPGRADE_TAG = "factory_upgrade";
    public static final DeferredHolder<Block, UpgradeBlock> FACTORY_UPGRADE_BLOCK = BLOCKS.register(
            FACTORY_UPGRADE_TAG, () -> new UpgradeBlock(FactoryComponent.FACTORY_UPGRADE));
    public static final DeferredHolder<Item, Item> FACTORY_UPGRADE_BLOCK_ITEM = ITEMS.register(
            FACTORY_UPGRADE_TAG, () ->
                    new BlockItem(FACTORY_UPGRADE_BLOCK.get(), Woot.createStandardProperties()));

    public static final String CELL_1_TAG = "cell_1";
    public static final DeferredHolder<Block, CellBlock> CELL_1_BLOCK = BLOCKS.register(
            CELL_1_TAG, () -> new CellBlock(Cell1BlockEntity.class));
    public static final DeferredHolder<Item, Item> CELL_1_BLOCK_ITEM = ITEMS.register(
            CELL_1_TAG, () ->
                    new BlockItem(CELL_1_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<Cell1BlockEntity>> CELL_1_BLOCK_TILE = BLOCKENTITIES.register(
            CELL_1_TAG, () ->
                    BlockEntityType.Builder.of(Cell1BlockEntity::new, CELL_1_BLOCK.get()).build(null));

    public static final String CELL_2_TAG = "cell_2";
    public static final DeferredHolder<Block, CellBlock> CELL_2_BLOCK = BLOCKS.register(
            CELL_2_TAG, () -> new CellBlock(Cell2BlockEntity.class));
    public static final DeferredHolder<Item, Item> CELL_2_BLOCK_ITEM = ITEMS.register(
            CELL_2_TAG, () ->
                    new BlockItem(CELL_2_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<Cell2BlockEntity>> CELL_2_BLOCK_TILE = BLOCKENTITIES.register(
            CELL_2_TAG, () ->
                    BlockEntityType.Builder.of(Cell2BlockEntity::new, CELL_2_BLOCK.get()).build(null));

    public static final String CELL_3_TAG = "cell_3";
    public static final DeferredHolder<Block, CellBlock> CELL_3_BLOCK = BLOCKS.register(
            CELL_3_TAG, () -> new CellBlock(Cell3BlockEntity.class));
    public static final DeferredHolder<Item, Item> CELL_3_BLOCK_ITEM = ITEMS.register(
            CELL_3_TAG, () ->
                    new BlockItem(CELL_3_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<Cell3BlockEntity>> CELL_3_BLOCK_TILE = BLOCKENTITIES.register(
            CELL_3_TAG, () ->
                    BlockEntityType.Builder.of(Cell3BlockEntity::new, CELL_3_BLOCK.get()).build(null));

    public static final String CELL_4_TAG = "cell_4";
    public static final DeferredHolder<Block, CellBlock> CELL_4_BLOCK = BLOCKS.register(
            CELL_4_TAG, () -> new CellBlock(Cell4BlockEntity.class));
    public static final DeferredHolder<Item, Item> CELL_4_BLOCK_ITEM = ITEMS.register(
            CELL_4_TAG, () ->
                    new BlockItem(CELL_4_BLOCK.get(), Woot.createStandardProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<Cell4BlockEntity>> CELL_4_BLOCK_TILE = BLOCKENTITIES.register(
            CELL_4_TAG, () ->
                    BlockEntityType.Builder.of(Cell4BlockEntity::new, CELL_4_BLOCK.get()).build(null));

    public static final DeferredHolder<Block, ExoticBlock> EXOTIC_A_BLOCK = BLOCKS.register(
            Exotic.EXOTIC_A.getName(), () -> new ExoticBlock(Exotic.EXOTIC_A));
    public static final DeferredHolder<Item, Item> EXOTIC_A_BLOCK_ITEM = ITEMS.register(
            Exotic.EXOTIC_A.getName(), () -> new BlockItem(EXOTIC_A_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, ExoticBlock> EXOTIC_B_BLOCK = BLOCKS.register(
            Exotic.EXOTIC_B.getName(), () -> new ExoticBlock(Exotic.EXOTIC_B));
    public static final DeferredHolder<Item, Item> EXOTIC_B_BLOCK_ITEM = ITEMS.register(
            Exotic.EXOTIC_B.getName(), () -> new BlockItem(EXOTIC_B_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, ExoticBlock> EXOTIC_C_BLOCK = BLOCKS.register(
            Exotic.EXOTIC_C.getName(), () -> new ExoticBlock(Exotic.EXOTIC_C));
    public static final DeferredHolder<Item, Item> EXOTIC_C_BLOCK_ITEM = ITEMS.register(
            Exotic.EXOTIC_C.getName(), () -> new BlockItem(EXOTIC_C_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, ExoticBlock> EXOTIC_D_BLOCK = BLOCKS.register(
            Exotic.EXOTIC_D.getName(), () -> new ExoticBlock(Exotic.EXOTIC_D));
    public static final DeferredHolder<Item, Item> EXOTIC_D_BLOCK_ITEM = ITEMS.register(
            Exotic.EXOTIC_D.getName(), () -> new BlockItem(EXOTIC_D_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Block, ExoticBlock> EXOTIC_E_BLOCK = BLOCKS.register(
            Exotic.EXOTIC_E.getName(), () -> new ExoticBlock(Exotic.EXOTIC_E));
    public static final DeferredHolder<Item, Item> EXOTIC_E_BLOCK_ITEM = ITEMS.register(
            Exotic.EXOTIC_E.getName(), () -> new BlockItem(EXOTIC_E_BLOCK.get(), Woot.createStandardProperties()));

    public static final DeferredHolder<Item, PerkItem> EFFICIENCY_1_ITEM = ITEMS.register(
            PerkItem.EFFICIENCY_1_REGNAME, () -> new PerkItem(Perk.efficiency_1));
    public static final DeferredHolder<Item, PerkItem> EFFICIENCY_2_ITEM = ITEMS.register(
            PerkItem.EFFICIENCY_2_REGNAME, () -> new PerkItem(Perk.efficiency_2));
    public static final DeferredHolder<Item, PerkItem> EFFICIENCY_3_ITEM = ITEMS.register(
            PerkItem.EFFICIENCY_3_REGNAME, () -> new PerkItem(Perk.efficiency_3));

    public static final DeferredHolder<Item, PerkItem> LOOTING_1_ITEM = ITEMS.register(
            PerkItem.LOOTING_1_REGNAME, () -> new PerkItem(Perk.looting_1));
    public static final DeferredHolder<Item, PerkItem> LOOTING_2_ITEM = ITEMS.register(
            PerkItem.LOOTING_2_REGNAME, () -> new PerkItem(Perk.looting_2));
    public static final DeferredHolder<Item, PerkItem> LOOTING_3_ITEM = ITEMS.register(
            PerkItem.LOOTING_3_REGNAME, () -> new PerkItem(Perk.looting_3));

    public static final DeferredHolder<Item, PerkItem> MASS_1_ITEM = ITEMS.register(
            PerkItem.MASS_1_REGNAME, () -> new PerkItem(Perk.mass_1));
    public static final DeferredHolder<Item, PerkItem> MASS_2_ITEM = ITEMS.register(
            PerkItem.MASS_2_REGNAME, () -> new PerkItem(Perk.mass_2));
    public static final DeferredHolder<Item, PerkItem> MASS_3_ITEM = ITEMS.register(
            PerkItem.MASS_3_REGNAME, () -> new PerkItem(Perk.mass_3));

    public static final DeferredHolder<Item, PerkItem> RATE_1_ITEM = ITEMS.register(
            PerkItem.RATE_1_REGNAME, () -> new PerkItem(Perk.rate_1));
    public static final DeferredHolder<Item, PerkItem> RATE_2_ITEM = ITEMS.register(
            PerkItem.RATE_2_REGNAME, () -> new PerkItem(Perk.rate_2));
    public static final DeferredHolder<Item, PerkItem> RATE_3_ITEM = ITEMS.register(
            PerkItem.RATE_3_REGNAME, () -> new PerkItem(Perk.rate_3));

    public static final DeferredHolder<Item, PerkItem> TIER_SHARD_1_ITEM = ITEMS.register(
            PerkItem.TIER_SHARD_1_REGNAME, () -> new PerkItem(Perk.tier_shard_1));
    public static final DeferredHolder<Item, PerkItem> TIER_SHARD_2_ITEM = ITEMS.register(
            PerkItem.TIER_SHARD_2_REGNAME, () -> new PerkItem(Perk.tier_shard_2));
    public static final DeferredHolder<Item, PerkItem> TIER_SHARD_3_ITEM = ITEMS.register(
            PerkItem.TIER_SHARD_3_REGNAME, () -> new PerkItem(Perk.tier_shard_3));

    public static final DeferredHolder<Item, PerkItem> XP_1_ITEM = ITEMS.register(
            PerkItem.XP_1_REGNAME, () -> new PerkItem(Perk.xp_1));
    public static final DeferredHolder<Item, PerkItem> XP_2_ITEM = ITEMS.register(
            PerkItem.XP_2_REGNAME, () -> new PerkItem(Perk.xp_2));
    public static final DeferredHolder<Item, PerkItem> XP_3_ITEM = ITEMS.register(
            PerkItem.XP_3_REGNAME, () -> new PerkItem(Perk.xp_3));

    public static final DeferredHolder<Item, PerkItem> HEADLESS_1_ITEM = ITEMS.register(
            PerkItem.HEADLESS_1_REGNAME, () -> new PerkItem(Perk.headless_1));
    public static final DeferredHolder<Item, PerkItem> HEADLESS_2_ITEM = ITEMS.register(
            PerkItem.HEADLESS_2_REGNAME, () -> new PerkItem(Perk.headless_2));
    public static final DeferredHolder<Item, PerkItem> HEADLESS_3_ITEM = ITEMS.register(
            PerkItem.HEADLESS_3_REGNAME, () -> new PerkItem(Perk.headless_3));

    public static final DeferredHolder<Item, PerkItem> SLAUGHTER_1_ITEM = ITEMS.register(
            PerkItem.SLAUGHTER_1_REGNAME, () -> new PerkItem(Perk.slaughter_1));
    public static final DeferredHolder<Item, PerkItem> SLAUGHTER_2_ITEM = ITEMS.register(
            PerkItem.SLAUGHTER_2_REGNAME, () -> new PerkItem(Perk.slaughter_2));
    public static final DeferredHolder<Item, PerkItem> SLAUGHTER_3_ITEM = ITEMS.register(
            PerkItem.SLAUGHTER_3_REGNAME, () -> new PerkItem(Perk.slaughter_3));

    public static final DeferredHolder<Item, PerkItem> CRUSHER_1_ITEM = ITEMS.register(
            PerkItem.CRUSHER_1_REGNAME, () -> new PerkItem(Perk.crusher_1));
    public static final DeferredHolder<Item, PerkItem> CRUSHER_2_ITEM = ITEMS.register(
            PerkItem.CRUSHER_2_REGNAME, () -> new PerkItem(Perk.crusher_2));
    public static final DeferredHolder<Item, PerkItem> CRUSHER_3_ITEM = ITEMS.register(
            PerkItem.CRUSHER_3_REGNAME, () -> new PerkItem(Perk.crusher_3));

    public static final DeferredHolder<Item, PerkItem> LASER_1_ITEM = ITEMS.register(
            PerkItem.LASER_1_REGNAME, () -> new PerkItem(Perk.laser_1));
    public static final DeferredHolder<Item, PerkItem> LASER_2_ITEM = ITEMS.register(
            PerkItem.LASER_2_REGNAME, () -> new PerkItem(Perk.laser_2));
    public static final DeferredHolder<Item, PerkItem> LASER_3_ITEM = ITEMS.register(
            PerkItem.LASER_3_REGNAME, () -> new PerkItem(Perk.laser_3));

    public static final DeferredHolder<Item, PerkItem> FLAYED_1_ITEM = ITEMS.register(
            PerkItem.FLAYED_1_REGNAME, () -> new PerkItem(Perk.flayed_1));
    public static final DeferredHolder<Item, PerkItem> FLAYED_2_ITEM = ITEMS.register(
            PerkItem.FLAYED_2_REGNAME, () -> new PerkItem(Perk.flayed_2));
    public static final DeferredHolder<Item, PerkItem> FLAYED_3_ITEM = ITEMS.register(
            PerkItem.FLAYED_3_REGNAME, () -> new PerkItem(Perk.flayed_3));

    public static final DeferredHolder<Item, MobShardItem> MOB_SHARD_ITEM = ITEMS.register(
            "mobshard", MobShardItem::new);

    public static final DeferredHolder<Item, XpShardBaseItem> XP_SHARD_ITEM = ITEMS.register(
            XpShardBaseItem.SHARD_REGNAME, () -> new XpShardBaseItem(XpShardBaseItem.Variant.SHARD));
    public static final DeferredHolder<Item, XpShardBaseItem> XP_SPLINTER_ITEM = ITEMS.register(
            XpShardBaseItem.SPLINTER_REGNAME, () -> new XpShardBaseItem(XpShardBaseItem.Variant.SPLINTER));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<UpgradeBlockEntity>> FACTORY_UPGRADE_BLOCK_ENTITY = BLOCKENTITIES.register(
            FACTORY_UPGRADE_TAG, () ->
                   BlockEntityType.Builder.of(UpgradeBlockEntity::new, FACTORY_UPGRADE_BLOCK.get()).build(null));

    public static List<Item> getItems(){
        return ITEMS.getEntries().stream().map(i -> i.get()).collect(Collectors.toUnmodifiableList());
    }
}
