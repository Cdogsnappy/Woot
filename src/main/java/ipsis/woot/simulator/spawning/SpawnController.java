package ipsis.woot.simulator.spawning;

import ipsis.woot.Woot;
import ipsis.woot.config.Config;
import ipsis.woot.config.ConfigOverride;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.FakeMobKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SpawnController {

    public static SpawnController get() { return INSTANCE; }
    static SpawnController INSTANCE;
    static { INSTANCE = new SpawnController(); }

    public void spawnKill(@Nonnull FakeMobKey fakeMobKey, @Nonnull ServerLevel world, @Nonnull BlockPos spawnPos) {

        if (!fakeMobKey.getMob().isValid())
            return;

        FakePlayer fakePlayer = FakePlayerPool.getFakePlayer(world, fakeMobKey.getLooting());
        if (fakePlayer == null)
            return;

        Entity entity = createEntity(fakeMobKey.getMob(), world, spawnPos);
        if (!(entity instanceof Mob mobEntity))
            return;

        mobEntity.finalizeSpawn(world,
                world.getCurrentDifficultyAt(new BlockPos(entity.getOnPos())),
                MobSpawnType.SPAWNER,
                null);

        mobEntity.setLastHurtByPlayer(fakePlayer);
        mobEntity.hurt(fakePlayer.damageSources().playerAttack(fakePlayer), 100);

        CustomSpawnController.get().apply(mobEntity, fakeMobKey.getMob(), world);
        mobEntity.hurt(fakePlayer.damageSources().playerAttack(fakePlayer), Float.MAX_VALUE);
    }

    private @Nullable Entity createEntity(@Nonnull FakeMob fakeMob, @Nonnull Level world, @Nonnull BlockPos pos) {

        ResourceLocation rl = fakeMob.getResourceLocation();
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(rl)) {
            Woot.setup.getLogger().debug("createEntity: {} not in entity list", rl);
            return null;
        }

        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", rl.toString());
        Entity entity = EntityType.loadEntityRecursive(nbt, world, (xc) -> {
            xc.moveTo(pos.getX(), pos.getY(), pos.getZ(), xc.yRotO, xc.xRotO);
            return xc;
        });

        return entity;
    }

    private static final int UNKNOWN_MOB_HEALTH = 100;
    public int getMobHealth(@Nonnull FakeMob fakeMob, @Nonnull Level level) {
        if (level.isClientSide)
            return UNKNOWN_MOB_HEALTH;

        if (!fakeMob.isValid())
            return UNKNOWN_MOB_HEALTH;

        if (!isCached(fakeMob)) {
            if (!updateCache(fakeMob,level))
                return UNKNOWN_MOB_HEALTH;
        }

        // Configuration value has priority
        if (Config.OVERRIDE.hasOverride(fakeMob, ConfigOverride.OverrideKey.HEALTH))
            return Config.OVERRIDE.getInteger(fakeMob, ConfigOverride.OverrideKey.HEALTH);

        return mobCacheEntryHashMap.get(fakeMob.toString()).health;
    }

    private static final int UNKNOWN_MOB_EXP = 1;
    public int getMobExperience(@Nonnull FakeMob fakeMob, @Nonnull Level level) {
        if (level.isClientSide)
            return UNKNOWN_MOB_EXP;

        if (!fakeMob.isValid())
            return UNKNOWN_MOB_EXP;

        if (!isCached(fakeMob)) {
            if (!updateCache(fakeMob, level))
                return UNKNOWN_MOB_EXP;
        }

        // Configuration value has priority
        if (Config.OVERRIDE.hasOverride(fakeMob, ConfigOverride.OverrideKey.XP))
            return Config.OVERRIDE.getInteger(fakeMob, ConfigOverride.OverrideKey.XP);

        return mobCacheEntryHashMap.get(fakeMob.toString()).xp;
    }

    public boolean isAnimal(@Nonnull FakeMob fakeMob, @Nonnull Level world) {

        if (!isCached(fakeMob)) {
            if (!updateCache(fakeMob, world))
                return false;
        }

        return mobCacheEntryHashMap.get(fakeMob.toString()).isAnimal;
    }

    private boolean isCached(@Nonnull FakeMob fakeMob) {
        return mobCacheEntryHashMap.containsKey(fakeMob.toString());
    }

    private boolean updateCache(@Nonnull FakeMob fakeMob, @Nonnull Level world) {
        // Cache miss, create the entity
        Entity entity = createEntity(fakeMob, world, new BlockPos(0, 0, 0));
        if (!(entity instanceof LivingEntity))
            return false;

        int health = (int)((LivingEntity) entity).getMaxHealth();

        FakePlayer fakePlayer = FakePlayerPool.getFakePlayer((ServerLevel)world, 0);

        int xp = 1;
        xp = ((LivingEntity) entity).getExperienceReward((ServerLevel) entity.level(), fakePlayer);


        Woot.setup.getLogger().debug("updateCache: caching mob:{} xp:{} health:{}", fakeMob, xp, health);
        addToCache(fakeMob, entity instanceof Animal, xp, health);
        return true;
    }


    public boolean isLivingEntity(FakeMob fakeMob, Level world) {
        Entity entity = createEntity(fakeMob, world, new BlockPos(0, 0, 0));
        return entity != null && entity instanceof Mob;
    }

    private static Map<String, MobCacheEntry> mobCacheEntryHashMap = new HashMap<>();
    private void addToCache(FakeMob fakeMob, boolean isAnimal, int xp, int health) {
        mobCacheEntryHashMap.put(fakeMob.toString(), new MobCacheEntry(isAnimal, xp, health));
    }

    class MobCacheEntry {
        public boolean isAnimal = false;
        public int xp = 1;
        public int health = 1;

        public MobCacheEntry(boolean isAnimal, int xp, int health) {
            this.isAnimal = isAnimal;
            this.xp = xp;
            this.health = health;
        }
    }
}
