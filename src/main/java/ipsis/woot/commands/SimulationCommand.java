package ipsis.woot.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.SimulatedMobDropSummary;
import ipsis.woot.simulator.spawning.SpawnController;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.FakeMobKey;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;


import java.util.List;
import java.util.stream.Collectors;

public class SimulationCommand {

    private static final String TAG = "commands.woot.simulation.";

    static final SuggestionProvider<CommandSourceStack> ENTITY_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(
                    BuiltInRegistries.ENTITY_TYPE.keySet().stream(),
                    builder);

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("simulation")
                .then(LearnCommand.register())
                .then(DumpCommand.register())
                .then(FlushCommand.register())
                .then(StatusCommand.register())
                .then(RollDropsCommand.register());
    }

    private static class LearnCommand {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("learn")
                    .requires(cs -> cs.hasPermission(CommandConfiguration.COMMAND_LEVEL_SIM_LEARN.get()))
                    .then(
                            Commands.argument("entity", ResourceLocationArgument.id()).suggests(ENTITY_SUGGESTIONS)
                                .executes(ctx -> learnEntity(
                                                ctx,
                                                ResourceLocationArgument.getId(ctx, "entity"), ""))
                            .then(
                                    Commands.argument("tag", StringArgumentType.string())
                                        .executes(ctx -> learnEntity(
                                                ctx,
                                                ResourceLocationArgument.getId(ctx, "entity"),
                                                StringArgumentType.getString(ctx, "tag")))
                            )

                    );
        }
    }

    private static class RollDropsCommand {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("roll")
                    .requires(cs -> cs.hasPermission(CommandConfiguration.COMMAND_LEVEL_SIM_ROLL_DROPS.get()))
                    .then(
                            Commands.argument("entity", ResourceLocationArgument.id()).suggests(ENTITY_SUGGESTIONS)
                                    .then (
                                            Commands.argument("looting", IntegerArgumentType.integer(0, 3))
                                                .executes(ctx -> rollDrops(
                                                        ctx,
                                                        ResourceLocationArgument.getId(ctx, "entity"), "",
                                                        IntegerArgumentType.getInteger(ctx, "looting"))))
                                    )
                                    .then(
                                            Commands.argument("tag", StringArgumentType.string())
                                                    .then (
                                                            Commands.argument("looting", IntegerArgumentType.integer(0, 3))
                                                                    .executes(ctx -> rollDrops(
                                                                            ctx,
                                                                            ResourceLocationArgument.getId(ctx, "entity"), "",
                                                                            IntegerArgumentType.getInteger(ctx, "looting"))
                                                                    )
                                    )

                    );
        }
    }

    private static class DumpCommand {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("dump")
                    .requires(cs -> cs.hasPermission(CommandConfiguration.COMMAND_LEVEL_SIM_DUMP.get()))
                    .then(
                            Commands.argument("entity", ResourceLocationArgument.id()).suggests(ENTITY_SUGGESTIONS)
                                    .executes(ctx -> dumpEntity(
                                            ctx,
                                            ResourceLocationArgument.getId(ctx, "entity"), ""))
                            .then(
                                    Commands.argument("tag", StringArgumentType.string())
                                            .executes(ctx -> dumpEntity(
                                                    ctx,
                                                    ResourceLocationArgument.getId(ctx, "entity"),
                                                    StringArgumentType.getString(ctx, "tag")))
                            )

                    );
        }
    }

    private static class FlushCommand {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("flush")
                    .requires(cs -> cs.hasPermission(CommandConfiguration.COMMAND_LEVEL_SIM_FLUSH.get()))
                    .then(
                            Commands.argument("entity", ResourceLocationArgument.id()).suggests(ENTITY_SUGGESTIONS)
                                    .executes(ctx -> flushEntity(
                                            ctx,
                                            ResourceLocationArgument.getId(ctx, "entity"), ""))
                                    .then(
                                            Commands.argument("tag", StringArgumentType.string())
                                                    .executes(ctx -> flushEntity(
                                                            ctx,
                                                            ResourceLocationArgument.getId(ctx, "entity"),
                                                            StringArgumentType.getString(ctx, "tag")))
                                    )

                    );
        }
    }

    private static int rollDrops(CommandContext<CommandSourceStack> source, ResourceLocation resourceLocation, String tag, int looting) throws CommandSyntaxException {

        FakeMob fakeMob;
        if (tag.equalsIgnoreCase(""))
            fakeMob = new FakeMob(resourceLocation.toString());
        else
            fakeMob = new FakeMob(resourceLocation.toString() + "," + tag);

        if (fakeMob.isValid() && SpawnController.get().isLivingEntity(fakeMob, source.getSource().getLevel())) {
            List<ItemStack> drops = MobSimulator.getInstance().getRolledDrops(new FakeMobKey(fakeMob, looting));
            source.getSource().sendSystemMessage(Component.translatable(TAG + "roll",
                    fakeMob, looting,
                    drops.stream().map(ItemStack::toString).collect(Collectors.joining(","))));
        }

        return 0;
    }

    private static int learnEntity(CommandContext<CommandSourceStack> source, ResourceLocation resourceLocation, String tag) throws CommandSyntaxException {

        FakeMob fakeMob;
        if (tag.equalsIgnoreCase(""))
            fakeMob = new FakeMob(resourceLocation.toString());
        else
            fakeMob = new FakeMob(resourceLocation.toString() + "," + tag);

        if (fakeMob.isValid() && SpawnController.get().isLivingEntity(fakeMob, source.getSource().getLevel())) {
            boolean result = ipsis.woot.simulator.MobSimulator.getInstance().learn(fakeMob);
            if (result)
                source.getSource().sendSystemMessage(Component.translatable(TAG + "learn.ok", resourceLocation.toString()));
            else
                source.getSource().sendSystemMessage(Component.translatable(TAG + "learn.fail", resourceLocation.toString()));
        } else {
            source.getSource().sendSystemMessage(Component.translatable(TAG + "learn.fail", resourceLocation.toString()));
        }

        return 0;
    }

    private static int dumpEntity(CommandContext<CommandSourceStack> source, ResourceLocation resourceLocation, String tag) throws CommandSyntaxException {

        FakeMob fakeMob;
        if (tag.equalsIgnoreCase(""))
            fakeMob = new FakeMob(resourceLocation.toString());
        else
            fakeMob = new FakeMob(resourceLocation.toString() + "," + tag);

        if (fakeMob.isValid() && SpawnController.get().isLivingEntity(fakeMob, source.getSource().getLevel())) {
            for (SimulatedMobDropSummary summary : ipsis.woot.simulator.MobSimulator.getInstance().getDropSummary(fakeMob))
                source.getSource().sendSystemMessage(Component.translatable(TAG + "dump.drop", summary));
        }

        return 0;
    }

    private static class StatusCommand {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("status")
                    .requires(cs -> cs.hasPermission(CommandConfiguration.COMMAND_LEVEL_SIM_STATUS.get()))
                    .executes(ctx -> status(ctx));
        }
    }

     private static int status (CommandContext<CommandSourceStack> source) throws CommandSyntaxException {
        source.getSource().sendSystemMessage(Component.translatable(TAG + "status.simulating",
                ipsis.woot.simulator.MobSimulator.getInstance().getSimulations().stream().map(
                        FakeMobKey::toString).collect(Collectors.joining(","))));
         source.getSource().sendSystemMessage(Component.translatable(TAG + "status.waiting",
                 ipsis.woot.simulator.MobSimulator.getInstance().getWaiting().stream().map(
                         FakeMobKey::toString).collect(Collectors.joining(","))));

         return 0;
     }

     private static int flushEntity(CommandContext<CommandSourceStack> source, ResourceLocation resourceLocation, String tag) throws CommandSyntaxException {
         FakeMob fakeMob;
         if (tag.equalsIgnoreCase(""))
             fakeMob = new FakeMob(resourceLocation.toString());
         else
             fakeMob = new FakeMob(resourceLocation.toString() + "," + tag);

         if (fakeMob.isValid() && SpawnController.get().isLivingEntity(fakeMob, source.getSource().getLevel())) {
             MobSimulator.getInstance().flush(fakeMob);
             MobSimulator.getInstance().learn(fakeMob);
         }
         return 0;
     }
}
