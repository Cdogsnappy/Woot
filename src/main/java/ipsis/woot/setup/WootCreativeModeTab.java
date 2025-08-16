package ipsis.woot.setup;

import ipsis.woot.Woot;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.modules.anvil.AnvilSetup;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.generic.GenericSetup;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.modules.layout.LayoutSetup;
import ipsis.woot.modules.oracle.OracleSetup;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WootCreativeModeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Woot.MODID);


    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }

    public static final Supplier<CreativeModeTab> WOOT_ITEMS_TAB = CREATIVE_MODE_TAB.register("bismuth_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(FactorySetup.HEART_BLOCK_ITEM.get()))
                    .title(Component.translatable("woot.creativetab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        FactorySetup.getItems().forEach(output::accept);
                        InfuserSetup.getItems().forEach(output::accept);
                        FluidConvertorSetup.getItems().forEach(output::accept);
                        LayoutSetup.getItems().forEach(output::accept);
                        GenericSetup.getItems().forEach(output::accept);
                        OracleSetup.getItems().forEach(output::accept);
                        AnvilSetup.getItems().forEach(output::accept);
                        SqueezerSetup.getItems().forEach(output::accept);
                        FluidSetup.getItems().forEach(output::accept);

                    }).build());
}
