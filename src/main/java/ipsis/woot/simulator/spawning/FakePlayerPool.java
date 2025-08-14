package ipsis.woot.simulator.spawning;

import com.mojang.authlib.GameProfile;
import ipsis.woot.Woot;
import ipsis.woot.util.helper.MathHelper;
import ipsis.woot.util.oss.WootFakePlayer;
import ipsis.woot.util.oss.WootFakePlayerFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.util.FakePlayer;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakePlayerPool {

    private static final String LOOT_0 = "[woot_0]";
    private static final String LOOT_1 = "[woot_1]";
    private static final String LOOT_2 = "[woot_2]";
    private static final String LOOT_3 = "[woot_3]";

    private static final GameProfile GP_LOOT_0 = new GameProfile(UUID.nameUUIDFromBytes(LOOT_0.getBytes()), LOOT_0);
    private static final GameProfile GP_LOOT_1 = new GameProfile(UUID.nameUUIDFromBytes(LOOT_1.getBytes()), LOOT_1);
    private static final GameProfile GP_LOOT_2 = new GameProfile(UUID.nameUUIDFromBytes(LOOT_2.getBytes()), LOOT_2);
    private static final GameProfile GP_LOOT_3 = new GameProfile(UUID.nameUUIDFromBytes(LOOT_3.getBytes()), LOOT_3);

    private static Map<Integer, WootFakePlayer> fakePlayerMap;

    private static void addFakePlayer(@Nonnull WootFakePlayer fakePlayer, int looting, Holder<Enchantment> enchantment) {
        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        if (looting > 0 && enchantment != null)
            sword.enchant(enchantment, looting);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, sword);
        fakePlayerMap.put(looting, fakePlayer);
    }

    private static void init(@Nonnull ServerLevel level) {
        fakePlayerMap = new HashMap<>();
        RegistryAccess registryAccess = level.registryAccess(); // or player.level().registryAccess()
        Registry<Enchantment> enchantmentRegistry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> enchantment = enchantmentRegistry.getHolderOrThrow(Enchantments.LOOTING);
        if (enchantment == null)
            Woot.setup.getLogger().warn("FakePlayerPool failed to find looting enchantment");

        addFakePlayer(WootFakePlayerFactory.get(level, GP_LOOT_0), 0, enchantment);
        addFakePlayer(WootFakePlayerFactory.get(level, GP_LOOT_1), 1, enchantment);
        addFakePlayer(WootFakePlayerFactory.get(level, GP_LOOT_2), 2, enchantment);
        addFakePlayer(WootFakePlayerFactory.get(level, GP_LOOT_3), 3, enchantment);
    }

    public static @Nullable FakePlayer getFakePlayer(@Nonnull ServerLevel level, int looting) {
        if (fakePlayerMap == null)
            init(level);

        return fakePlayerMap.get(MathHelper.clampLooting(looting));
    }

    public static boolean isFakePlayer(@Nonnull Entity entity) {
        if (!(entity instanceof FakePlayer))
            return false;

        FakePlayer fp = (FakePlayer)entity;
        UUID uuid = fp.getUUID();
        return GP_LOOT_0.getId().equals(uuid) || GP_LOOT_1.getId().equals(uuid) || GP_LOOT_2.getId().equals(uuid) || GP_LOOT_3.getId().equals(uuid);
    }



}
