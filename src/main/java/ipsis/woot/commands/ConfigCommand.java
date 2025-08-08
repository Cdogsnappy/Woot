package ipsis.woot.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ipsis.woot.config.Config;
import ipsis.woot.util.FakeMob;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;


public class ConfigCommand {

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("config")
                .requires(cs -> cs.hasPermission(CommandHelper.CONFIG_COMMAND_LEVEL))
                .then(Commands.argument("entity", StringArgumentType.string())
                        .then(Commands.argument("key", StringArgumentType.string())
                                .executes(ctx -> showConfig(ctx.getSource().source, StringArgumentType.getString(ctx, "entity"), StringArgumentType.getString(ctx, "key")))));
    }

    private static int showConfig(CommandSource source, String entityKey, String key) throws CommandSyntaxException {
        FakeMob fakeMob = new FakeMob(entityKey);
        source.sendSystemMessage(Component.translatable(Config.OVERRIDE.getConfigAsString(fakeMob, key)));
        return 0;
    }
}
