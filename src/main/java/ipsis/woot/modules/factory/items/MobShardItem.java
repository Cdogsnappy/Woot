package ipsis.woot.modules.factory.items;

import ipsis.woot.Woot;
import ipsis.woot.config.Config;
import ipsis.woot.config.ConfigOverride;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.policy.PolicyRegistry;
import ipsis.woot.policy.PolicyConfiguration;
import ipsis.woot.util.FakeMob;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Mob shard is used to hold the mob kills until the shard is turned into a mob controller.
 * A shard is full when its kill count hits a predefined level.
 */

public class MobShardItem extends Item {

    public MobShardItem() { super(new Item.Properties().stacksTo(1)); }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Woot.setup.getLogger().debug(target.getName());
        if (attacker.level().isClientSide || !(attacker instanceof Player))
            return false;

        if (!(target instanceof Mob))
            return false;

        if (isProgrammed(stack))
            return false;

        // status messages for success
        FakeMob fakeMob = new FakeMob((Mob)target);
        if (!fakeMob.isValid())
            return false;

        if (!PolicyRegistry.get().canCaptureEntity(fakeMob.getResourceLocation()) || !canShardCaptureMob(fakeMob.getResourceLocation())) {
            attacker.sendSystemMessage(Component.translatable("chat.woot.mobshard.failure"));
            return false;
        }

        setProgrammedMob(stack, fakeMob);
        attacker.sendSystemMessage(Component.translatable("chat.woot.mobshard.success"));
        return true;

    }

    private static boolean canShardCaptureMob(ResourceLocation resourceLocation) {
        for (String s : PolicyConfiguration.SHARD_BLACKLIST_FULL_MOD.get())
            if (s.equalsIgnoreCase(resourceLocation.getNamespace()))
                return false;

        for (String s : PolicyConfiguration.SHARD_BLACKLIST_ENTITY.get())
            if (s.equalsIgnoreCase(resourceLocation.toString()))
                return false;
        return true;
    }

    /**
     * NBT
     */
    private static final String NBT_MOB = "Mob";
    private static final String NBT_KILLS = "Kills";

    public static boolean isProgrammed(ItemStack itemStack) {
        return getProgrammedMob(itemStack).isValid();
    }

    public static FakeMob getProgrammedMob(ItemStack itemStack) {
        FakeMob fakeMob = new FakeMob();
        CustomData data =  itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = data == null ? new CompoundTag() : data.copyTag();
        if (tag.contains(NBT_MOB))
            fakeMob = new FakeMob(tag.getCompound(NBT_MOB));

        return fakeMob;
    }

    private void setProgrammedMob(ItemStack itemStack, FakeMob fakeMob) {
        CompoundTag mobNbt = new CompoundTag();
        FakeMob.writeToNBT(fakeMob, mobNbt);
        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = data == null ? new CompoundTag() : data.copyTag();
        tag.put(NBT_MOB, mobNbt);
        tag.putInt(NBT_KILLS, 0);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static boolean isMatchingMob(ItemStack itemStack, FakeMob fakeMob) {

        if (itemStack.getItem() != FactorySetup.MOB_SHARD_ITEM.get())
            return false;

        if (!isProgrammed(itemStack))
            return false;

        FakeMob programmedMob = getProgrammedMob(itemStack);
        if (!programmedMob.isValid())
            return false;

        return programmedMob.equals(fakeMob);
    }

    public static void handleKill(Player playerEntity, FakeMob fakeMob) {
        ItemStack foundStack = ItemStack.EMPTY;

        // Hotbar only
        for (int i = 0; i <= 9; i++) {
            ItemStack itemStack = playerEntity.getInventory().getItem(i);
            if (!itemStack.isEmpty() && isMatchingMob(itemStack, fakeMob)) {
                foundStack = itemStack;
                break;
            }
        }

        if (!foundStack.isEmpty()) {
            incrementKills(foundStack, 1);
        }
    }

    private static void incrementKills(ItemStack itemStack, int v) {

        if (itemStack.getItem() != FactorySetup.MOB_SHARD_ITEM.get())
            return;

        FakeMob fakeMob = getProgrammedMob(itemStack);
        if (!fakeMob.isValid())
            return;

        if (!PolicyRegistry.get().canCaptureEntity(fakeMob.getResourceLocation()) || !canShardCaptureMob(fakeMob.getResourceLocation()))
            return;

        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = data == null ? new CompoundTag() : data.copyTag();
        tag.putInt(NBT_KILLS, tag.getInt(NBT_KILLS) + v);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        if (isFull(itemStack) && isProgrammed(itemStack)){
            itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
    }

    private static boolean isFull(ItemStack itemStack) {

        int killCount = itemStack.get(DataComponents.CUSTOM_DATA).copyTag().getInt(NBT_KILLS);
        FakeMob fakeMob = getProgrammedMob(itemStack);
        if (!fakeMob.isValid())
            return false;

        return killCount >= Config.OVERRIDE.getIntegerOrDefault(fakeMob, ConfigOverride.OverrideKey.SHARD_KILLS);
    }

    public static boolean isFullyProgrammed(ItemStack itemStack) {
        return isProgrammed(itemStack) && isFull(itemStack);
    }

    public static void setJEIEnderShard(ItemStack itemStack) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("nbt_jei_shard", 1);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
    }

    /**
     * Tooltip
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        tooltip.add(Component.translatable("info.woot.mobshard.0"));
        tooltip.add(Component.translatable("info.woot.mobshard.1"));
        tooltip.add(Component.translatable("info.woot.mobshard.2"));
        tooltip.add(Component.translatable("info.woot.mobshard.3"));

        FakeMob fakeMob = getProgrammedMob(stack);
        if (!fakeMob.isValid()) {
            tooltip.add(Component.translatable("info.woot.mobshard.a.0"));
            return;
        }

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(fakeMob.getResourceLocation());
        if (entityType != null)
            tooltip.add(Component.translatable(entityType.getDescriptionId()));
        if (fakeMob.hasTag())
            tooltip.add(Component.translatable("[" + fakeMob.getTag() + "]"));

        int killCount = stack.get(DataComponents.CUSTOM_DATA).copyTag().getInt(NBT_KILLS);
        if (isFull(stack)) {
            tooltip.add(Component.translatable("info.woot.mobshard.a.1"));
        } else {
            tooltip.add(Component.translatable("info.woot.mobshard.b.0",
                    killCount,
                    Config.OVERRIDE.getIntegerOrDefault(fakeMob, ConfigOverride.OverrideKey.SHARD_KILLS)));
        }
    }
}
