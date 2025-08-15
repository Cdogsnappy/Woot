package ipsis.woot.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ipsis.woot.Woot;
//import ipsis.woot.compat.top.WootTopPlugin;

import ipsis.woot.config.OverrideLoader;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.modules.anvil.AnvilSetup;
import ipsis.woot.modules.debug.DebugSetup;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.blocks.*;
import ipsis.woot.modules.factory.generators.LootGeneration;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.fluidconvertor.blocks.FluidConvertorBlock;
import ipsis.woot.modules.fluidconvertor.blocks.FluidConvertorBlockEntity;
import ipsis.woot.modules.generic.GenericSetup;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.modules.infuser.blocks.InfuserBlockEntity;
import ipsis.woot.modules.infuser.items.DyePlateItem;
import ipsis.woot.modules.layout.LayoutSetup;
import ipsis.woot.modules.oracle.OracleSetup;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import ipsis.woot.modules.squeezer.blocks.DyeSqueezerBlockEntity;
import ipsis.woot.modules.squeezer.blocks.EnchantSqueezerBlockEntity;
import ipsis.woot.modules.tools.ToolsSetup;
import ipsis.woot.policy.PolicyRegistry;
import ipsis.woot.modules.factory.layout.PatternRepository;
import ipsis.woot.mod.ModFiles;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.MobSimulatorSetup;
import ipsis.woot.util.WootEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;

@EventBusSubscriber
public class ModSetup {

    private Logger logger = LogManager.getLogger();
    private CreativeModeTab creativeTab;

    public ModSetup() {
        creativeTab = new CreativeModeTab(CreativeModeTab.builder().icon(() -> new ItemStack(FactorySetup.HEART_BLOCK.get().asItem())).title(Component.literal(Woot.MODID))) {
            @Override
            public @NotNull ItemStack getIconItem() {
                return new ItemStack(FactorySetup.HEART_BLOCK.get());
            }
        };
    }



    public static void registrySetup(IEventBus eventBus) {
        InfuserSetup.register(eventBus);
        SqueezerSetup.register(eventBus);
        OracleSetup.register(eventBus);
        LayoutSetup.register(eventBus);
        AnvilSetup.register(eventBus);
        FluidSetup.register(eventBus);
        ToolsSetup.register(eventBus);
        DebugSetup.register(eventBus);
        GenericSetup.register(eventBus);
        FluidConvertorSetup.register(eventBus);
        FactorySetup.register(eventBus);
        WootCreativeModeTab.register(eventBus);
        WootRecipes.register(eventBus);
        MobSimulatorSetup.register(eventBus);
    }


    @SubscribeEvent // on the mod event bus
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        EnchantSqueezerBlockEntity.registerCapabilities(event);
        DyeSqueezerBlockEntity.registerCapabilities(event);
        InfuserBlockEntity.registerCapabilities(event);
        FluidConvertorBlockEntity.registerCapabilities(event);
        Cell1BlockEntity.registerCapabilities(event);
        Cell2BlockEntity.registerCapabilities(event);
        Cell3BlockEntity.registerCapabilities(event);
        Cell4BlockEntity.registerCapabilities(event);


    }

    public void commonSetup(FMLCommonSetupEvent e) {

        PolicyRegistry.get().loadFromConfig();
        ModFiles.INSTANCE.init();
        PatternRepository.get().load();
        OverrideLoader.loadFromConfig();
        PolicyRegistry.get().loadFromConfig();
        LootGeneration.get().loadFromConfig();

        File dropFile = ModFiles.INSTANCE.getLootFile();
        Gson GSON = new Gson();
        try {
            JsonObject jsonObject = GSON.fromJson(new FileReader(dropFile), JsonObject.class);
            MobSimulator.getInstance().fromJson(jsonObject);
        } catch (Exception exception) {
            Woot.setup.getLogger().warn("Failed to load loot file {}", dropFile.getAbsolutePath());
        }
    }

    public void clientSetup(FMLClientSetupEvent e) {

    }


    public Logger getLogger() { return logger; }
    public CreativeModeTab getCreativeTab() { return creativeTab; }
}
