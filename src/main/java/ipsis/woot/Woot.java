package ipsis.woot;

import ipsis.woot.config.Config;
import ipsis.woot.setup.*;
import net.minecraft.world.item.Item;
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

    public Woot() {

        ModLoadingContext.get().(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        setup.registrySetup(NeoForge.EVENT_BUS);

        NeoForge.EVENT_BUS.register(new Registration());

        NeoForge.EVENT_BUS.addListener((FMLCommonSetupEvent e) -> setup.commonSetup(e));
        NeoForge.EVENT_BUS.addListener((FMLClientSetupEvent e) -> setup.clientSetup(e));

        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve("woot-client.toml"));
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("woot-common.toml"));
    }

    public static Item.Properties createStandardProperties() {
        return new Item.Properties();
    }

}
