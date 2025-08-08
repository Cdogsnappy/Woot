package ipsis.woot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import ipsis.woot.config.Config;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.spawning.SpawnController;
import ipsis.woot.util.FakeMob;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


public class MobCommand {

    private static final String TAG = "commands.woot.mob.";

    static final SuggestionProvider<CommandSourceStack> ENTITY_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(
                    BuiltInRegistries.ENTITY_TYPE.keySet().stream(),
                    builder);

    static ArgumentBuilder<CommandSourceStack, ?> register()  {
        return Commands.literal("mob")
                .then(InfoCommand.register());
    }

    private static class InfoCommand {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("info")
                    .requires(cs -> cs.hasPermission(CommandHelper.MOB_INFO_COMMAND_LEVEL))
                    .then(
                            Commands.argument("entity", ResourceLocationArgument.id()).suggests(ENTITY_SUGGESTIONS)
                                    .executes(ctx -> mobInfo(
                                            ctx,
                                            ResourceLocationArgument.getId(ctx, "entity"), ""))
                                    .then(
                                            Commands.argument("tag", StringArgumentType.string())
                                                    .executes(ctx -> mobInfo(
                                                            ctx,
                                                            ResourceLocationArgument.getId(ctx, "entity"),
                                                            StringArgumentType.getString(ctx, "tag")))
                                    )
                    );
        }
    }

    private static int mobInfo(CommandContext<CommandSourceStack> source, ResourceLocation resourceLocation, String tag) throws CommandSyntaxException {
        FakeMob fakeMob;
        if (tag.equalsIgnoreCase(""))
            fakeMob = new FakeMob(resourceLocation.toString());
        else
            fakeMob = new FakeMob(resourceLocation.toString() + "," + tag);

        if (fakeMob.isValid() && SpawnController.get().isLivingEntity(fakeMob, source.getSource().getLevel())) {
            int health = SpawnController.get().getMobHealth(fakeMob, source.getSource().getLevel());
            int xp = SpawnController.get().getMobExperience(fakeMob, source.getSource().getLevel());
            Tier mobTier = Config.OVERRIDE.getMobTier(fakeMob, source.getSource().getLevel());

            source.getSource().sendSystemMessage(Component.translatable(TAG + "info.summary", fakeMob, health, xp, mobTier));
        }

        return 0;
    }

}
