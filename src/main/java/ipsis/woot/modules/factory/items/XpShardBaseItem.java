package ipsis.woot.modules.factory.items;

import ipsis.woot.Woot;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.util.helper.RandomHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.FakePlayer;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * xp shards are created by the factory when killing mobs and the appropriate upgrade is present.
 * Each xp shard is equal to one experience.
 * 9 Shards can be combined into xp orbs/blocks with
 * The factory will create xp orbs/blocks and give xp shards as change
 */
public class XpShardBaseItem extends Item {

    public static final String SHARD_REGNAME = "xpshard";
    public static final String SPLINTER_REGNAME = "xpsplinter";

    private static final int STACK_SIZE = 64;
    public static final int SPLINTERS_IN_STACK = 9;
    private static final int SHARD_XP = 9;
    private static final int SPLINTER_XP = 1;

    final Variant variant;
    public XpShardBaseItem(Variant variant) {
        super(new Item.Properties().stacksTo(STACK_SIZE));
        this.variant = variant;
    }

    public Variant getVariant() { return this.variant; }

    public enum Variant {
        SHARD,
        SPLINTER
    }

    public static ItemStack getItemStack(Variant variant) {
        if (variant == Variant.SHARD)
            return new ItemStack(FactorySetup.XP_SHARD_ITEM.get());
        return new ItemStack(FactorySetup.XP_SPLINTER_ITEM.get());
    }

    public static List<ItemStack> getShards(int xp) {
        List<ItemStack> shards = new ArrayList<>();

        int xpShards = xp / SPLINTERS_IN_STACK;
        int xpSplinters =  xp % SPLINTERS_IN_STACK;
        int fullStacks = xpShards / STACK_SIZE;
        int leftoverShard = xpShards % STACK_SIZE;

        Woot.setup.getLogger().debug("getShards: xp {} xpShards {} xpSplinters {} fullStacks {} leftoverShards {}",
                xp, xpShards, xpSplinters, fullStacks, leftoverShard);

        for (int i = 0; i < fullStacks; i++) {
            ItemStack itemStack = getItemStack(Variant.SHARD);
            itemStack.setCount(STACK_SIZE);
            shards.add(itemStack);
        }

        if (leftoverShard > 0) {
            ItemStack itemStack = getItemStack(Variant.SHARD);
            itemStack.setCount(leftoverShard);
            shards.add(itemStack);
        }

        if (xpSplinters > 0) {
            ItemStack itemStack = getItemStack(Variant.SPLINTER);
            itemStack.setCount(xpSplinters);
            shards.add(itemStack);
        }

        return shards;
    }

    private int getXp(ItemStack itemStack) {
        if (itemStack.getItem() == FactorySetup.XP_SHARD_ITEM.get())
            return SHARD_XP;
        else if (itemStack.getItem() == FactorySetup.XP_SPLINTER_ITEM.get())
            return SPLINTER_XP;
        return 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide)
            return InteractionResultHolder.pass(itemStack);

        if (itemStack.isEmpty())
            return InteractionResultHolder.pass(itemStack);

        ItemStack advancementStack = itemStack.copy();

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS,
                0.2F,
                0.5F * ((RandomHelper.RANDOM.nextFloat() - RandomHelper.RANDOM.nextFloat()) * 0.7F + 1.8F));

        if (player instanceof FakePlayer) {
            // Fake player can only use one at a time
            level.addFreshEntity(new ExperienceOrb(
                            level,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                            1));
            itemStack.shrink(1);
        } else {
            int xp = 0;
            if (player.isCrouching()) {
                // Consume the whole stack
                xp = getXp(itemStack) * itemStack.getCount();
                if (!player.isCreative())
                    itemStack.setCount(0);
            } else {
                xp = getXp(itemStack);
                if (!player.isCreative())
                    itemStack.shrink(1);
            }
            if (xp > 0) {
                player.giveExperiencePoints(xp);
                if (player instanceof ServerPlayer)
                    CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, advancementStack);
            }
        }
        return InteractionResultHolder.success(itemStack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        tooltip.add(Component.translatable("info.woot.shard.0"));
        tooltip.add(Component.translatable("info.woot.shard.1"));
    }
}
