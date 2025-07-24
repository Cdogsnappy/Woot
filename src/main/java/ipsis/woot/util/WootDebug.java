package ipsis.woot.util;


import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public interface WootDebug {

    List<String> getDebugText(List<String> debug, UseOnContext itemUseContext);
}
