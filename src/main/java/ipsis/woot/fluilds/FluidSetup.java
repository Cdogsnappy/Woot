package ipsis.woot.fluilds;

import ipsis.woot.Woot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


public class FluidSetup {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, Woot.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, Woot.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Woot.MODID);

    /**
     * Conatus
     */
    public static final ResourceLocation CONATUS = ResourceLocation.parse("woot:fluid/conatus");
    public static final ResourceLocation CONATUS_OVERLAY = ResourceLocation.parse("woot:block/conatus_overlay");
    public static final ResourceLocation CONATUS_STILL = ResourceLocation.parse("woot:block/conatus_still");
    public static final ResourceLocation CONATUS_FLOWING = ResourceLocation.parse("woot:block/conatus_flow");

    public static final DeferredHolder<FluidType, FluidType> CONATUS_FLUID_TYPE = FLUID_TYPES.register("conatus_fluid_type",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.woot.conatus")
                    .canSwim(false)
                    .canDrown(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));



    public static DeferredHolder<Fluid, BaseFlowingFluid> CONATUS_FLUID = FLUIDS.register("conatus_fluid", () -> new BaseFlowingFluid.Source(FluidSetup.CONATUS_FLUID_PROPERTIES));
    public static DeferredHolder<Fluid, BaseFlowingFluid> CONATUS_FLUID_FLOWING = FLUIDS.register("conatus_fluid_flowing", () -> new BaseFlowingFluid.Flowing(FluidSetup.CONATUS_FLUID_PROPERTIES));
    public static DeferredHolder<Block, LiquidBlock > CONATUS_FLUID_BLOCK = BLOCKS.register("conatus_fluid_block",
            () -> new LiquidBlock(CONATUS_FLUID.get(),
                    Block.Properties.of()
                            .strength(100.0F)
                            .noLootTable()));
    public static DeferredHolder<Item, Item> CONATUS_FLUID_BUCKET = ITEMS.register("conatus_fluid_bucket",
            () -> new BucketItem(CONATUS_FLUID.get(),
                    new Item.Properties()
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)));
    public static final BaseFlowingFluid.Properties CONATUS_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            CONATUS_FLUID_TYPE,
            CONATUS_FLUID,
            CONATUS_FLUID_FLOWING).bucket(CONATUS_FLUID_BUCKET);


    /**
     * Pure Dye
     */
    public static final ResourceLocation PUREDYE = ResourceLocation.parse("woot:fluid/puredye");
    public static final ResourceLocation PUREDYE_STILL = ResourceLocation.parse("woot:block/puredye_still");
    public static final ResourceLocation PUREDYE_FLOWING = ResourceLocation.parse("woot:block/puredye_flow");

    public static DeferredHolder<FluidType, FluidType> PUREDYE_FLUID_TYPE = FLUID_TYPES.register("puredye_fluid_type",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.woot.puredye")
                    .canSwim(false)
                    .canDrown(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static DeferredHolder<Fluid, Fluid> PUREDYE_FLUID = FLUIDS.register("puredye_fluid", () -> new BaseFlowingFluid.Source(FluidSetup.PUREDYE_FLUID_PROPERTIES));
    public static DeferredHolder<Fluid, FlowingFluid> PUREDYE_FLUID_FLOWING = FLUIDS.register("puredye_fluid_flowing", () -> new BaseFlowingFluid.Flowing(FluidSetup.PUREDYE_FLUID_PROPERTIES));
    public static DeferredHolder<Block, LiquidBlock> PUREDYE_FLUID_BLOCK = BLOCKS.register("puredye_fluid_block",
            () -> new LiquidBlock(PUREDYE_FLUID_FLOWING.get(),
                    Block.Properties.of()
                            .strength(100.0F)
                            .noLootTable()));
    public static DeferredHolder<Item, BucketItem> PUREDYE_FLUID_BUCKET = ITEMS.register("puredye_fluid_bucket",
            () -> new BucketItem(PUREDYE_FLUID.get(),
                    new Item.Properties()
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)));
    public static final BaseFlowingFluid.Properties PUREDYE_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            PUREDYE_FLUID_TYPE,
            PUREDYE_FLUID,
            PUREDYE_FLUID_FLOWING).bucket(PUREDYE_FLUID_BUCKET);

    /**
     * Liquid Enchantment
     */
    public static final ResourceLocation ENCHANT = ResourceLocation.parse("woot:fluid/enchant");
    public static final ResourceLocation ENCHANT_STILL = ResourceLocation.parse("woot:block/enchant_still");
    public static final ResourceLocation ENCHANT_FLOWING = ResourceLocation.parse("woot:block/enchant_flow");

    public static DeferredHolder<FluidType, FluidType> ENCHANT_FLUID_TYPE = FLUID_TYPES.register("enchant_fluid_type",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.woot.enchant")
                    .canSwim(false)
                    .canDrown(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static DeferredHolder<Fluid, Fluid> ENCHANT_FLUID = FLUIDS.register("enchant_fluid", () -> new BaseFlowingFluid.Source(FluidSetup.ENCHANT_FLUID_PROPERTIES));
    public static DeferredHolder<Fluid, FlowingFluid> ENCHANT_FLUID_FLOWING = FLUIDS.register("enchant_flow", () -> new BaseFlowingFluid.Flowing(FluidSetup.ENCHANT_FLUID_PROPERTIES));
    public static DeferredHolder<Block, LiquidBlock> ENCHANT_FLUID_BLOCK = BLOCKS.register("enchant_fluid_block",
            () -> new LiquidBlock(ENCHANT_FLUID_FLOWING.get(),
                    Block.Properties.of()
                            .strength(100.0F)
                            .noLootTable()));
    public static DeferredHolder<Item, BucketItem> ENCHANT_FLUID_BUCKET = ITEMS.register("enchant_fluid_bucket",
            () -> new BucketItem(ENCHANT_FLUID.get(),
                    new Item.Properties()
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)));
    public static final BaseFlowingFluid.Properties ENCHANT_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ENCHANT_FLUID_TYPE,
            ENCHANT_FLUID,
            ENCHANT_FLUID_FLOWING).bucket(ENCHANT_FLUID_BUCKET);

    /**
     * Mob Essence
     */
    public static final ResourceLocation MOB_ESSENCE_STILL =ResourceLocation.parse("woot:block/mob_essence_still");
    public static final ResourceLocation MOB_ESSENCE_FLOWING = ResourceLocation.parse("woot:block/mob_essence_flow");

    public static DeferredHolder<FluidType, FluidType> MOB_ESSENCE_FLUID_TYPE = FLUID_TYPES.register("mobessence_fluid_type",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.woot.mobessence")
                    .canSwim(false)
                    .canDrown(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

    public static DeferredHolder<Fluid, Fluid> MOB_ESSENCE_FLUID = FLUIDS.register("mob_essence_fluid", () -> new BaseFlowingFluid.Source(FluidSetup.MOB_ESSENCE_FLUID_PROPERTIES));
    public static DeferredHolder<Fluid, FlowingFluid> MOB_ESSENCE_FLUID_FLOWING = FLUIDS.register("mob_essence_fluid_flowing", () -> new BaseFlowingFluid.Flowing(FluidSetup.MOB_ESSENCE_FLUID_PROPERTIES));
    public static DeferredHolder<Block, LiquidBlock> MOB_ESSENCE_FLUID_BLOCK = BLOCKS.register("mob_essence_fluid_block",
            () -> new LiquidBlock(MOB_ESSENCE_FLUID_FLOWING.get(),
                    Block.Properties.of()
                            .strength(100.0F)
                            .noLootTable()));
    public static DeferredHolder<Item, BucketItem> MOB_ESSENCE_FLUID_BUCKET = ITEMS.register("mob_essence_fluid_bucket",
            () -> new BucketItem(MOB_ESSENCE_FLUID.get(),
                    new Item.Properties()
                            .craftRemainder(Items.BUCKET)
                            .stacksTo(1)));
    public static final BaseFlowingFluid.Properties MOB_ESSENCE_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            MOB_ESSENCE_FLUID_TYPE,
            MOB_ESSENCE_FLUID,
            MOB_ESSENCE_FLUID_FLOWING).bucket(MOB_ESSENCE_FLUID_BUCKET);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("FluidSetup: register");
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        FLUIDS.register(eventBus);
        FLUID_TYPES.register(eventBus);
    }

}
