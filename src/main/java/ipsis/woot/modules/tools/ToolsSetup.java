package ipsis.woot.modules.tools;

import ipsis.woot.Woot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ToolsSetup {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Woot.MODID);

    public static void register(IEventBus eventBus) {
        Woot.setup.getLogger().info("ToolsSetup: register");
        ITEMS.register(eventBus);
    }
}
