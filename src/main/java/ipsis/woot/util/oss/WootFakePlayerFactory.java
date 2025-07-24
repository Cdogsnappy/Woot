package ipsis.woot.util.oss;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import ipsis.woot.Woot;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;


import java.util.Map;

/**
 * Darkhax Dark-Utilities
 * Temporary FakePlayer extension
 * Currently Forge 6358 causes a NPE when potion effects
 * are applied to FakePlayer
 */
@Mod.EventBusSubscriber(modid = Woot.MODID, bus = EventBusSubscriber.Bus.MOD)
public class WootFakePlayerFactory {

    private static Map<GameProfile, WootFakePlayer> fakePlayers = Maps.newHashMap();

    public static WootFakePlayer get(ServerLevel world, GameProfile username) {

        if (!fakePlayers.containsKey(username)) {

            final WootFakePlayer fakePlayer = new WootFakePlayer(world, username);
            fakePlayers.put(username, fakePlayer);
        }

        return fakePlayers.get(username);
    }

    public static void unloadLevel (ServerLevel world) {

        fakePlayers.entrySet().removeIf(entry -> entry.getValue().level() == world);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onDimensionUnload (LevelEvent.Unload event) {

        if (event.getLevel() instanceof ServerLevel) {

            unloadLevel((ServerLevel) event.getLevel());
        }
    }
}
