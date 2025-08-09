package ipsis.woot.util.oss;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.util.FakePlayer;


/**
 * Darkhax Dark-Utilities
 * Temporary FakePlayer extension
 * Currently Forge 6358 causes a NPE when potion effects
 * are applied to FakePlayer
 */

public class WootFakePlayer extends FakePlayer {

    public WootFakePlayer(ServerLevel world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return false;
    }
}
