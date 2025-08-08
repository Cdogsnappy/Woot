package ipsis.woot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;

/**
 * The main command for the mod.
 * All other commands are sub-commands of this.
 */
public class WootCommand {

    public WootCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("woot")
                .then(SimulationCommand.register())
                .then(GiveCommand.register())
                .then(ConfigCommand.register())
                .then(MobCommand.register())
        );
    }
}
