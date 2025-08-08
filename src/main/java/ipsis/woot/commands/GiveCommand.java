package ipsis.woot.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import ipsis.woot.modules.factory.blocks.ControllerBlockEntity;
import ipsis.woot.simulator.spawning.SpawnController;
import ipsis.woot.util.FakeMob;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;


public class GiveCommand {

    static final SuggestionProvider<CommandSourceStack> ENTITY_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(
                    BuiltInRegistries.ENTITY_TYPE.keySet().stream(),
                    builder);

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("give")
                .requires(cs -> cs.hasPermission(CommandConfiguration.COMMAND_LEVEL_GIVE.get()))
                .then(
                        Commands.argument("target", EntityArgument.player())
                        .then(
                                Commands.argument("entity", ResourceLocationArgument.id()).suggests(ENTITY_SUGGESTIONS)
                                    .executes(ctx -> giveItem(
                                        ctx.getSource().source,
                                        EntityArgument.getPlayer(ctx, "target"),
                                        ResourceLocationArgument.getId(ctx, "entity"), ""))
                                .then(
                                        Commands.argument("tag", StringArgumentType.string())
                                                .executes(ctx -> giveItem(
                                                        ctx.getSource().source,
                                                        EntityArgument.getPlayer(ctx, "target"),
                                                        ResourceLocationArgument.getId(ctx, "entity"),
                                                        StringArgumentType.getString(ctx, "tag")))
                                )
                        )
                );
    }

    private static int giveItem(CommandSource source, ServerPlayer target, ResourceLocation resourceLocation, String tag) {

        FakeMob fakeMob = new FakeMob();
        if (tag.equalsIgnoreCase(""))
            fakeMob = new FakeMob(resourceLocation.toString());
        else
            fakeMob = new FakeMob(resourceLocation.toString() + "," + tag);
        if (fakeMob.isValid() && SpawnController.get().isLivingEntity(fakeMob, target.level())) {
            ItemStack itemStack = ControllerBlockEntity.getItemStack(fakeMob);

            /**
             * Straight from vanilla GiveCommand
             */
            boolean added = target.getInventory().add(itemStack);
            if (added && itemStack.isEmpty()) {
                itemStack.setCount(1);
                ItemEntity itemEntity = target.drop(itemStack, false);
                if (itemEntity != null)
                    itemEntity.makeFakeItem();

                target.level().playSound(null,
                        target.getX(), target.getY(), target.getZ(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.PLAYERS,
                        0.2F,
                        ((target.getRandom().nextFloat() - target.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                target.containerMenu.broadcastChanges();
            } else {
                ItemEntity itemEntity = target.drop(itemStack, false);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                }
            }
            source.sendSystemMessage(Component.translatable("commands.woot.give.ok",
                    target.getDisplayName(), resourceLocation.toString()));
        } else {
            source.sendSystemMessage(Component.translatable("commands.woot.give.fail",
                    resourceLocation.toString()));
        }

        return 1;
    }
}
