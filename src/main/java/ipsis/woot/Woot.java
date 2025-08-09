package ipsis.woot;

import ipsis.woot.config.Config;
import ipsis.woot.setup.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.Logging;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Woot.MODID)
public class Woot {

    public static final String MODID = "woot";
    public static ModSetup setup = new ModSetup();

    public Woot(IEventBus modEventBus, ModContainer modContainer) {

        ModSetup.registrySetup(modEventBus);


        modEventBus.addListener((FMLCommonSetupEvent e) -> setup.commonSetup(e));
        modEventBus.addListener((FMLClientSetupEvent e) -> setup.clientSetup(e));

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties();
    }

}
