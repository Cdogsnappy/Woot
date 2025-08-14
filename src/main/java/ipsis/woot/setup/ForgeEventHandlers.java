package ipsis.woot.setup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ipsis.woot.Woot;
import ipsis.woot.commands.ModCommands;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.mod.ModFiles;
import ipsis.woot.modules.anvil.AnvilRecipes;
import ipsis.woot.modules.factory.multiblock.MultiBlockTracker;
import ipsis.woot.modules.fluidconvertor.FluidConvertorRecipes;
import ipsis.woot.modules.infuser.InfuserRecipes;
import ipsis.woot.simulator.CustomDropsLoader;
import ipsis.woot.simulator.MobSimulatorSetup;
import ipsis.woot.modules.squeezer.SqueezerRecipes;
import ipsis.woot.modules.factory.items.MobShardItem;
import ipsis.woot.simulator.spawning.FakePlayerPool;
import ipsis.woot.simulator.MobSimulator;
import ipsis.woot.simulator.tartarus.TartarusChunkGenerator;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.FakeMobKey;
import ipsis.woot.util.helper.ItemEntityHelper;
import ipsis.woot.util.helper.SerializationHelper;
import ipsis.woot.util.oss.WootFakePlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.DynamicFluidContainerModel;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid=Woot.MODID)
public class ForgeEventHandlers {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDropsEvent(LivingDropsEvent event) {

        /**
         * Entity->LivingEntity->Mob
         */

        if (!(event.getEntity() instanceof Mob))
            return;

        Mob mob = (Mob)event.getEntity();
        DamageSource damageSource = event.getSource();
        if (damageSource == null)
            return;

        if (!FakePlayerPool.isFakePlayer(damageSource.getDirectEntity()))
            return;
        FakePlayer fakePlayer  = (FakePlayer)damageSource.getDirectEntity();

        ItemStack stack = fakePlayer.getItemInHand(InteractionHand.MAIN_HAND);
        // Cancel our fake spawns
        event.setCanceled(true);

        Registry<Enchantment> enchantmentRegistry = damageSource.getEntity().level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> lootingHolder = enchantmentRegistry.getHolderOrThrow(Enchantments.LOOTING);

        int level = stack.getEnchantmentLevel(lootingHolder);

        List<ItemStack> drops = ItemEntityHelper.convertToItemStacks(event.getDrops());
        FakeMobKey fakeMobKey = new FakeMobKey(new FakeMob(mob), fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).getEnchantmentLevel(lootingHolder));
        if (fakeMobKey.getMob().isValid()) {
            ipsis.woot.simulator.MobSimulator.getInstance().learnSimulatedDrops(fakeMobKey, drops);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDeathEvent(LivingDeathEvent event) {

        // Only player kills
        if (!(event.getSource().getEntity() instanceof Player))
            return;

        Player killer = (Player)event.getSource().getDirectEntity();
        LivingEntity victim = event.getEntity();

        if (ignoreDeathEvent(event.getEntity())) {
            Woot.setup.getLogger().debug("onLivingDeathEvent: duplicate {} {}",
                    event.getEntity(), event.getEntity().getStringUUID());
            return;
        }

        // Ignore fakeplayer
        if (killer instanceof FakePlayer)
            return;

        if (!(victim instanceof Mob))
            return;

        FakeMob fakeMob = new FakeMob((Mob)victim);
        if (!fakeMob.isValid())
            return;

        MobShardItem.handleKill(killer, fakeMob);
    }

    private static final long MULTI_BLOCK_TRACKER_DELAY = 20;
    private static class TickTrack {
        public Level world;
        public long lastWorldTick;
        public TickTrack(Level world) {
            this.world = world;
            this.lastWorldTick = 0;
        }

        public boolean tick(long gameTime) {
            if (gameTime > lastWorldTick + MULTI_BLOCK_TRACKER_DELAY) {
                lastWorldTick = gameTime;
                return true;
            }
            return false;
        }
    }
    private static List<TickTrack> tickTracks = new ArrayList<>();
    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post event) {
        if(event.getLevel().isClientSide){
            return;
        }

        TickTrack currTick = null;
        for (TickTrack tickTrack : tickTracks) {
            if (tickTrack.world == event.getLevel()) {
                currTick = tickTrack;
                break;
            }
        }

        if (currTick == null) {
            currTick = new TickTrack(event.getLevel());
            tickTracks.add(currTick);
        }

        if (event.getLevel().dimension().equals(MobSimulatorSetup.TARTARUS_LEVEL_KEY)) {
            MobSimulator.getInstance().tick(event.getLevel());
        } else {
            if (currTick.tick(event.getLevel().getGameTime()))
                MultiBlockTracker.get().run(event.getLevel());
        }
    }


    @SubscribeEvent
    public static void onServerStarting(final ServerStartingEvent event) {
        Woot.setup.getLogger().info("onServerStarting");
        SqueezerRecipes.load(event.getServer().getRecipeManager());
        AnvilRecipes.load(event.getServer().getRecipeManager());
        InfuserRecipes.load(event.getServer().getRecipeManager());
        FluidConvertorRecipes.load(event.getServer().getRecipeManager());
        CustomDropsLoader.load(event.getServer().getRecipeManager());

        for (ServerLevel world : event.getServer().getAllLevels())
            Woot.setup.getLogger().debug("onServerStarting: world {}", world.dimension());

        ServerLevel serverWorld = event.getServer().getLevel(MobSimulatorSetup.TARTARUS_LEVEL_KEY);
        if (serverWorld == null) {
            Woot.setup.getLogger().error("onServerStarting: tartarus not found");
        } else {
            Woot.setup.getLogger().info("onServerStarting: force load Tartarus Cells");
            serverWorld.setChunkForced(TartarusChunkGenerator.WORK_CHUNK_X, TartarusChunkGenerator.WORK_CHUNK_Z, true);
        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(final LivingExperienceDropEvent event) {
        if (event.getAttackingPlayer() instanceof WootFakePlayer) {
            // This is not a real kill, so dont spawn the xp
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRecipesUpdatedEvent(final RecipesUpdatedEvent event) {
        Woot.setup.getLogger().info("onRecipesUpdatedEvent");
        SqueezerRecipes.load(event.getRecipeManager());
        AnvilRecipes.load(event.getRecipeManager());
        InfuserRecipes.load(event.getRecipeManager());
        FluidConvertorRecipes.load(event.getRecipeManager());
    }

    @SubscribeEvent
    public static void onRegisterCommandsEvent(final RegisterCommandsEvent event) {
        Woot.setup.getLogger().info("onRegisterCommandsEvent");
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStop(final ServerStoppingEvent event) {
        Woot.setup.getLogger().info("onServerStop");
        JsonObject jsonObject = MobSimulator.getInstance().toJson();
        File dropFile = ModFiles.INSTANCE.getLootFile();
        Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        SerializationHelper.writeJsonFile(dropFile, GSON.toJson(jsonObject));
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return FluidSetup.ENCHANT_STILL;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FluidSetup.ENCHANT_FLOWING;
            }



            @Override
            public int getTintColor() {
                return 0xFFFFFFFF;
            }
        }, FluidSetup.ENCHANT_FLUID_TYPE.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return FluidSetup.MOB_ESSENCE_STILL;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FluidSetup.MOB_ESSENCE_FLOWING;
            }



            @Override
            public int getTintColor() {
                return 0xFFFFFFFF;
            }
        }, FluidSetup.MOB_ESSENCE_FLUID_TYPE.get());

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return FluidSetup.CONATUS_STILL;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FluidSetup.CONATUS_FLOWING;
            }



            @Override
            public int getTintColor() {
                return 0xFFFFFFFF;
            }
        }, FluidSetup.CONATUS_FLUID_TYPE.get());


        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public @NotNull ResourceLocation getStillTexture() {
                return FluidSetup.PUREDYE_STILL;
            }

            @Override
            public @NotNull ResourceLocation getFlowingTexture() {
                return FluidSetup.PUREDYE_FLOWING;
            }



            @Override
            public int getTintColor() {
                return 0xFFFFFFFF;
            }
        }, FluidSetup.PUREDYE_FLUID_TYPE.get());
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(new DynamicFluidContainerModel.Colors(), FluidSetup.CONATUS_FLUID_BUCKET.get());
        event.register(new DynamicFluidContainerModel.Colors(), FluidSetup.PUREDYE_FLUID_BUCKET.get());
        event.register(new DynamicFluidContainerModel.Colors(), FluidSetup.MOB_ESSENCE_FLUID_BUCKET.get());
        event.register(new DynamicFluidContainerModel.Colors(), FluidSetup.ENCHANT_FLUID_BUCKET.get());

    }

    /**
     * Death cache
     * Some entities like the EnderDragon generate multiple death events
     * so we cache the last X here and ignore any duplicates
     */
    private static final int MAX_UUID_CACHE_SIZE = 10;
    private static List<String> uuidList = new ArrayList<>();
    private static boolean ignoreDeathEvent(Entity entity) {
        String uuid = entity.getStringUUID();
        if (uuidList.contains(uuid))
            return true;

        uuidList.add(uuid);
        if (uuidList.size() > MAX_UUID_CACHE_SIZE)
            uuidList.remove(0);

        return false;
    }
}
