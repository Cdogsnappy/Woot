package ipsis.woot.setup;

import ipsis.woot.Woot;
import ipsis.woot.modules.anvil.AnvilSetup;
import ipsis.woot.modules.anvil.client.AnvilTileEntitySpecialRenderer;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.fluidconvertor.client.FluidConvertorScreen;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.modules.infuser.client.InfuserScreen;
import ipsis.woot.modules.infuser.items.DyeCasingItem;
import ipsis.woot.modules.infuser.items.DyePlateItem;
import ipsis.woot.modules.layout.LayoutSetup;
import ipsis.woot.modules.layout.client.LayoutTileEntitySpecialRenderer;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.oracle.client.OracleScreen;
import ipsis.woot.modules.factory.client.HeartScreen;
import ipsis.woot.modules.oracle.OracleSetup;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import ipsis.woot.modules.squeezer.client.DyeSqueezerScreen;
import ipsis.woot.modules.squeezer.client.EnchantSqueezerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = Woot.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        Woot.setup.getLogger().debug("FMLClientSetupEvent");
        ClientRegistry.bindTileEntityRenderer(LayoutSetup.LAYOUT_BLOCK_TILE.get(), LayoutTileEntitySpecialRenderer::new);
        ClientRegistry.bindTileEntityRenderer(AnvilSetup.ANVIL_BLOCK_TILE.get(), AnvilTileEntitySpecialRenderer::new);
        ScreenManager.registerFactory(FactorySetup.HEART_BLOCK_CONTAINER.get(), HeartScreen::new);
        ScreenManager.registerFactory(OracleSetup.ORACLE_BLOCK_CONTAINER.get(), OracleScreen::new);
        ScreenManager.registerFactory(SqueezerSetup.SQUEEZER_BLOCK_CONTAINER.get(), DyeSqueezerScreen::new);
        ScreenManager.registerFactory(SqueezerSetup.ENCHANT_SQUEEZER_BLOCK_CONTAINER.get(), EnchantSqueezerScreen::new);
        ScreenManager.registerFactory(InfuserSetup.INFUSER_BLOCK_CONTAINER.get(), InfuserScreen::new);
        ScreenManager.registerFactory(FluidConvertorSetup.FLUID_CONVERTOR_BLOCK_CONTATAINER.get(), FluidConvertorScreen::new);
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event) {
        Woot.setup.getLogger().debug("registerItemColors");
        ItemColors items = event.get;
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.WHITE_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.ORANGE_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.MAGENTA_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.LIGHT_BLUE_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.YELLOW_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.LIME_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.PINK_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.GRAY_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.LIGHT_GRAY_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.CYAN_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.PURPLE_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.BLUE_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.BROWN_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.GREEN_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.RED_DYE_PLATE_ITEM.get());
        items.register((s, t) -> ((DyePlateItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.BLACK_DYE_PLATE_ITEM.get());

        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.WHITE_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.ORANGE_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.MAGENTA_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.LIGHT_BLUE_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.YELLOW_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.LIME_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.PINK_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.GRAY_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.LIGHT_GRAY_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.CYAN_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.PURPLE_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.BLUE_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.BROWN_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.GREEN_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.RED_DYE_CASING_ITEM.get());
        items.register((s, t) -> ((DyeCasingItem)s.getItem()).getColor().getMapColor().col, InfuserSetup.BLACK_DYE_CASING_ITEM.get());
    }
}
