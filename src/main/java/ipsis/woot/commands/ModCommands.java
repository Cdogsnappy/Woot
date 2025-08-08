package ipsis.woot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        new WootCommand(dispatcher);
    }
}
