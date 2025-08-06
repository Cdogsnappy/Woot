package ipsis.woot.modules.factory;

import ipsis.woot.Woot;
import ipsis.woot.config.Config;
import ipsis.woot.config.ConfigOverride;
import ipsis.woot.modules.factory.blocks.*;
import ipsis.woot.modules.factory.client.ClientFactorySetup;
import ipsis.woot.modules.factory.layout.Layout;
import ipsis.woot.modules.factory.layout.PatternBlock;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.simulator.spawning.SpawnController;
import ipsis.woot.util.ExtraWootCodecs;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.helper.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.*;

/**
 * Fully formed factory and its configuration
 * There will always be at least one mob which is in the primary slot
 */
public class FormedSetup {

    private Tier tier = Tier.TIER_1;
    private List<FakeMob> controllerMobs = new ArrayList<>();
    private HashMap<Perk.Group, Integer> perks = new HashMap<>();
    private HashMap<FakeMob, MobParam> mobParams = new HashMap<>();
    private Level world;
    private BlockPos importPos = BlockPos.ZERO;
    private BlockPos exportPos = BlockPos.ZERO;
    private BlockPos cellPos = BlockPos.ZERO;
    private int cellCapacity = 0;
    private int cellType = 0;
    private double shardDropChance = 0.0F;
    private int[] shardDropWeights = new int[]{ 0, 0, 0 };
    private int perkTierShardValue = 0;
    private Exotic exotic = Exotic.NONE;
    private Boolean perkCapped = false;

    private FormedSetup() {}
    private FormedSetup(Level world, Tier tier) {
        this.world = world;
        this.tier = tier;
    }

    public List<FakeMob> getAllMobs() { return Collections.unmodifiableList(controllerMobs); }
    public Map<FakeMob, MobParam> getAllMobParams() { return Collections.unmodifiableMap(mobParams); }
    public Map<Perk.Group, Integer> getAllPerks() { return Collections.unmodifiableMap(perks); }
    public Optional<IFluidHandler> getCellFluidHandler() {
        if (world != null) {
            BlockEntity te = world.getBlockEntity(cellPos);
            return te != null ? Optional.ofNullable(world.getCapability(Capabilities.FluidHandler.BLOCK, te.getBlockPos(), null)) : Optional.empty();
            //return te != null ? te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) : Optional.empty();
        }
        return Optional.empty();
    }

    public boolean isPerkCapped() { return this.perkCapped; }
    public Exotic getExotic() { return this.exotic; }
    public boolean hasFluidIngredientExotic() { return this.exotic == Exotic.EXOTIC_A; }
    public boolean hasItemIngredientExotic() { return this.exotic == Exotic.EXOTIC_B; }
    public boolean hasConatusExotic() { return this.exotic == Exotic.EXOTIC_C; }
    public boolean hasSpawnTimExotic() { return this.exotic == Exotic.EXOTIC_D; }
    public boolean hasMassExotic() { return this.exotic == Exotic.EXOTIC_E; }

    public double getShardDropChance() { return this.shardDropChance; }
    public int getBasicShardWeight() { return shardDropWeights[0]; }
    public int getAdvancedShardWeight() { return shardDropWeights[1]; }
    public int getEliteShardWeight() { return shardDropWeights[2]; }
    public int getPerkTierShardValue() { return perkTierShardValue; }

    public BlockPos getImportPos() { return this.importPos; }
    public BlockPos getExportPos() { return this.exportPos; }
    public Level getWorld() { return this.world; }

    public Tier getTier() { return this.tier; }
    public int getCellCapacity() { return this.cellCapacity; }
    public int getCellType() { return this.cellType; }
    public int getCellFluidAmount() {
        Optional<IFluidHandler> hdlr = getCellFluidHandler();
        if (hdlr.isPresent()) {
            IFluidHandler iFluidHandler = hdlr.orElseThrow(NullPointerException::new);
            return iFluidHandler.getFluidInTank(0).getAmount();
        }
        return 0;
    }
    public int getLootingLevel() { return MathHelper.clampLooting(perks.getOrDefault(Perk.Group.LOOTING, 0)); }

    public List<Optional<IItemHandler>> getImportHandlers() {
        List<Optional<IItemHandler>> handlers = new ArrayList<>();
        for (Direction facing : Direction.values()) {
            if (!world.isLoaded(importPos.offset(facing.getNormal())))
                continue;
            BlockEntity te = world.getBlockEntity(importPos.offset(facing.getNormal()));
            if (te == null)
                continue;

            handlers.add(Optional.ofNullable(world.getCapability(Capabilities.ItemHandler.BLOCK, te.getBlockPos(), facing.getOpposite())));
        }
        return handlers;
    }

    public List<Optional<IFluidHandler>> getImportFluidHandlers() {
        List<Optional<IFluidHandler>> handlers = new ArrayList<>();
        for (Direction facing : Direction.values()) {
            if (!world.isLoaded(importPos.offset(facing.getNormal())))
                continue;
            BlockEntity te = world.getBlockEntity(importPos.offset(facing.getNormal()));
            if (te == null)
                continue;

            handlers.add(Optional.ofNullable(world.getCapability(Capabilities.FluidHandler.BLOCK, te.getBlockPos(), facing.getOpposite())));
        }
        return handlers;
    }

    public List<Optional<IFluidHandler>> getExportFluidHandlers() {
        List<Optional<IFluidHandler>> handlers = new ArrayList<>();
        for (Direction facing : Direction.values()) {
            if (!world.isLoaded(exportPos.offset(facing.getNormal())))
                continue;
            BlockEntity te = world.getBlockEntity(exportPos.offset(facing.getNormal()));
            if (te == null)
                continue;

            handlers.add(Optional.ofNullable(world.getCapability(Capabilities.FluidHandler.BLOCK, te.getBlockPos(), facing.getOpposite())));
        }
        return handlers;
    }

    public List<Optional<IItemHandler>> getExportHandlers() {
        List<Optional<IItemHandler>> handlers = new ArrayList<>();
        for (Direction facing : Direction.values()) {
            if (!world.isLoaded(importPos.offset(facing.getNormal())))
                continue;
            BlockEntity te = world.getBlockEntity(importPos.offset(facing.getNormal()));
            if (te == null)
                continue;

            handlers.add(Optional.ofNullable(world.getCapability(Capabilities.ItemHandler.BLOCK, te.getBlockPos(), facing.getOpposite())));
        }
        return handlers;
    }

    public int getMaxSpawnTime() {
        int max = 0;
        for (MobParam mobParam : mobParams.values()) {
            if (mobParam.baseSpawnTicks > max)
                max = mobParam.baseSpawnTicks;
        }
        return max;
    }

    public int getMinRateValue() {

        boolean hasPerk = false;
        int min = Integer.MAX_VALUE;
        for (MobParam mobParam : mobParams.values()) {
            if (mobParam.hasPerkRateValue() && mobParam.getPerkRateValue() < min) {
                min = mobParam.getPerkRateValue();
                hasPerk = true;
            }
        }

        // If there is no perk present then the rate reduction will be 0
        // Otherwise it is the smalled reduction across all the mobs
        return hasPerk ? min : 0;
    }

    private void setupMobParams() {
        for (FakeMob fakeMob : controllerMobs) {
            MobParam param = new MobParam();
            param.baseSpawnTicks = Config.OVERRIDE.getIntegerOrDefault(fakeMob, ConfigOverride.OverrideKey.SPAWN_TICKS);
            param.baseMassCount = Config.OVERRIDE.getIntegerOrDefault(fakeMob, ConfigOverride.OverrideKey.MASS_COUNT);

            if (Config.OVERRIDE.hasOverride(fakeMob, ConfigOverride.OverrideKey.FIXED_COST)) {
                param.baseFluidCost = Config.OVERRIDE.getInteger(fakeMob, ConfigOverride.OverrideKey.FIXED_COST);
            } else {
                int healthPoints = SpawnController.get().getMobHealth(fakeMob, world);
                int unitsPerHealthPoint = Config.OVERRIDE.getIntegerOrDefault(fakeMob, ConfigOverride.OverrideKey.UNITS_PER_HEALTH);
                param.baseFluidCost = unitsPerHealthPoint * healthPoints;
            }

            // Efficiency
            if (perks.containsKey(Perk.Group.EFFICIENCY)) {
                int perkLevel = perks.getOrDefault(Perk.Group.EFFICIENCY, 0);
                if (perkLevel > 0)
                    param.setPerkEfficiencyValue(Config.OVERRIDE.getIntegerOrDefault(fakeMob,
                            Config.OVERRIDE.getKeyByPerk(Perk.Group.EFFICIENCY, perkLevel)));
            }

            // Mass
            if (perks.containsKey(Perk.Group.MASS)) {
                int perkLevel = perks.getOrDefault(Perk.Group.MASS, 0);
                if (perkLevel > 0)
                    param.setPerkMassValue(Config.OVERRIDE.getIntegerOrDefault(fakeMob,
                            Config.OVERRIDE.getKeyByPerk(Perk.Group.MASS, perkLevel)));
            }

            // Rate
            if (perks.containsKey(Perk.Group.RATE)) {
                int perkLevel = perks.getOrDefault(Perk.Group.RATE, 0);
                if (perkLevel > 0)
                    param.setPerkRateValue(Config.OVERRIDE.getIntegerOrDefault(fakeMob,
                            Config.OVERRIDE.getKeyByPerk(Perk.Group.RATE, perkLevel)));
            }

            // Xp
            if (perks.containsKey(Perk.Group.XP)) {
                int perkLevel = perks.getOrDefault(Perk.Group.XP, 0);
                if (perkLevel > 0)
                    param.setPerkXpValue(Config.OVERRIDE.getIntegerOrDefault(fakeMob,
                            Config.OVERRIDE.getKeyByPerk(Perk.Group.XP, perkLevel)));
            }

            // Headless
            if (perks.containsKey(Perk.Group.HEADLESS)) {
                int perkLevel = perks.getOrDefault(Perk.Group.HEADLESS, 0);
                if (perkLevel > 0)
                    param.setPerkHeadlessValue(Config.OVERRIDE.getIntegerOrDefault(fakeMob,
                            Config.OVERRIDE.getKeyByPerk(Perk.Group.HEADLESS, perkLevel)));
            }

            mobParams.put(fakeMob, param);
        }
    }

    @Override
    public String toString() {
        return "FormedSetup{" +
                "tier=" + tier +
                ", controllerMobs=" + controllerMobs +
                ", perks=" + perks +
                ", world=" + world +
                ", importPos=" + importPos +
                ", exportPos=" + exportPos +
                ", cellPos=" + cellPos +
                ", cellCapacity=" + cellCapacity +
                ", exotic=" + exotic +
                '}';
    }

    public static FormedSetup createFromValidLayout(Level world, Layout layout) {
        FormedSetup formedSetup = new FormedSetup(world, layout.getAbsolutePattern().getTier());

        // Mobs are already validated
        for (FakeMob fakeMob : layout.getAbsolutePattern().getMobs())
            formedSetup.controllerMobs.add(new FakeMob(fakeMob));

        for (PatternBlock pb : layout.getAbsolutePattern().getBlocks()) {
            if (pb.getFactoryComponent() == FactoryComponent.FACTORY_UPGRADE) {
                BlockEntity te = world.getBlockEntity(pb.getBlockPos());
                if (te instanceof UpgradeBlockEntity) {
                    Perk perk = ((UpgradeBlockEntity) te).getUpgrade(world.getBlockState(pb.getBlockPos()));
                    if (perk != Perk.EMPTY) {
                        Perk.Group group = Perk.getGroup(perk);
                        int perkLevel = Perk.getLevel(perk);
                        /**
                         * Tier 1,2 - level 1 upgrades only
                         * Tier 3 - level 1,2 upgrades only
                         * Tier 4+ - all upgrades
                         * You can still apply them but they don't get the same level if not a high enough tier
                         */
                        if ((formedSetup.tier == Tier.TIER_1 || formedSetup.tier == Tier.TIER_2) && perkLevel > 1) {
                            perkLevel = 1;
                            formedSetup.perkCapped = true;
                        } else if (formedSetup.tier == Tier.TIER_3 && perkLevel > 2) {
                            perkLevel = 2;
                            formedSetup.perkCapped = true;
                        }

                        Woot.setup.getLogger().debug("createFromValidLayout: adding perk {}/{}", group, perkLevel);
                        formedSetup.perks.put(group, perkLevel);

                        if (group == Perk.Group.TIER_SHARD) {
                            if (perkLevel == 1)
                                formedSetup.perkTierShardValue = FactoryConfiguration.TIER_SHARD_1.get();
                            else if (perkLevel == 2)
                                formedSetup.perkTierShardValue = FactoryConfiguration.TIER_SHARD_2.get();
                            else if (perkLevel == 3)
                                formedSetup.perkTierShardValue = FactoryConfiguration.TIER_SHARD_3.get();

                            if (formedSetup.getTier() == Tier.TIER_1) {
                                formedSetup.shardDropChance = FactoryConfiguration.T1_FARM_DROP_CHANCE.get();
                                formedSetup.shardDropWeights[0] = FactoryConfiguration.T1_FARM_DROP_SHARD_WEIGHTS.get().get(0);
                                formedSetup.shardDropWeights[1] = FactoryConfiguration.T1_FARM_DROP_SHARD_WEIGHTS.get().get(1);
                                formedSetup.shardDropWeights[2] = FactoryConfiguration.T1_FARM_DROP_SHARD_WEIGHTS.get().get(2);
                            } else if (formedSetup.getTier() == Tier.TIER_2) {
                                formedSetup.shardDropChance = FactoryConfiguration.T2_FARM_DROP_CHANCE.get();
                                formedSetup.shardDropWeights[0] = FactoryConfiguration.T2_FARM_DROP_SHARD_WEIGHTS.get().get(0);
                                formedSetup.shardDropWeights[1] = FactoryConfiguration.T2_FARM_DROP_SHARD_WEIGHTS.get().get(1);
                                formedSetup.shardDropWeights[2] = FactoryConfiguration.T2_FARM_DROP_SHARD_WEIGHTS.get().get(2);
                            } else if (formedSetup.getTier() == Tier.TIER_3) {
                                formedSetup.shardDropChance = FactoryConfiguration.T3_FARM_DROP_CHANCE.get();
                                formedSetup.shardDropWeights[0] = FactoryConfiguration.T3_FARM_DROP_SHARD_WEIGHTS.get().get(0);
                                formedSetup.shardDropWeights[1] = FactoryConfiguration.T3_FARM_DROP_SHARD_WEIGHTS.get().get(1);
                                formedSetup.shardDropWeights[2] = FactoryConfiguration.T3_FARM_DROP_SHARD_WEIGHTS.get().get(2);
                            } else if (formedSetup.getTier() == Tier.TIER_4) {
                                formedSetup.shardDropChance = FactoryConfiguration.T4_FARM_DROP_CHANCE.get();
                                formedSetup.shardDropWeights[0] = FactoryConfiguration.T4_FARM_DROP_SHARD_WEIGHTS.get().get(0);
                                formedSetup.shardDropWeights[1] = FactoryConfiguration.T4_FARM_DROP_SHARD_WEIGHTS.get().get(1);
                                formedSetup.shardDropWeights[2] = FactoryConfiguration.T4_FARM_DROP_SHARD_WEIGHTS.get().get(2);
                            } else if (formedSetup.getTier() == Tier.TIER_5) {
                                formedSetup.shardDropChance = FactoryConfiguration.T5_FARM_DROP_CHANCE.get();
                                formedSetup.shardDropWeights[0] = FactoryConfiguration.T5_FARM_DROP_SHARD_WEIGHTS.get().get(0);
                                formedSetup.shardDropWeights[1] = FactoryConfiguration.T5_FARM_DROP_SHARD_WEIGHTS.get().get(1);
                                formedSetup.shardDropWeights[2] = FactoryConfiguration.T5_FARM_DROP_SHARD_WEIGHTS.get().get(2);
                            }

                        }
                    }
                }
            } else if (pb.getFactoryComponent() == FactoryComponent.CELL) {
                formedSetup.cellPos = new BlockPos(pb.getBlockPos());
                BlockEntity te = world.getBlockEntity(pb.getBlockPos());
                if (te instanceof CellBlockEntityBase) {
                    formedSetup.cellCapacity = ((CellBlockEntityBase) te).getCapacity();
                    if (te instanceof Cell1BlockEntity)
                        formedSetup.cellType = 0;
                    else if (te instanceof Cell2BlockEntity)
                        formedSetup.cellType = 1;
                    else if (te instanceof Cell3BlockEntity)
                        formedSetup.cellType = 2;
                    else if (te instanceof Cell4BlockEntity)
                        formedSetup.cellType = 3;
                }
            } else if (pb.getFactoryComponent() == FactoryComponent.IMPORT) {
                formedSetup.importPos = new BlockPos(pb.getBlockPos());
            } else if (pb.getFactoryComponent() == FactoryComponent.EXPORT) {
                formedSetup.exportPos = new BlockPos(pb.getBlockPos());
            }
        }

        formedSetup.exotic = layout.getAbsolutePattern().getExotic();
        formedSetup.setupMobParams();
        return formedSetup;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, HashMap<FakeMob, MobParam>> PARAM_MAP_CODEC =
            ExtraWootCodecs.mapStreamCodec(FakeMob.STREAM_CODEC, MobParam.STREAM_CODEC, HashMap::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, HashMap<Perk.Group, Integer>> GROUP_MAP_CODEC =
            ExtraWootCodecs.mapStreamCodec(Perk.Group.STREAM_CODEC,ByteBufCodecs.VAR_INT, HashMap::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, List<FakeMob>> MOB_LIST_CODEC =
            ExtraWootCodecs.listStreamCodec(FakeMob.STREAM_CODEC);



    public static final StreamCodec<RegistryFriendlyByteBuf, FormedSetup> STREAM_CODEC =
            StreamCodec.of(
                    (buf,data) ->{
                        Tier.STREAM_CODEC.encode(buf,data.tier);
                        buf.writeInt(data.cellCapacity);
                        MOB_LIST_CODEC.encode(buf, data.controllerMobs);
                        GROUP_MAP_CODEC.encode(buf, data.perks);
                        PARAM_MAP_CODEC.encode(buf, data.mobParams);
                        buf.writeInt(data.exotic.ordinal());
                        buf.writeDouble(data.shardDropChance);
                        buf.writeVarIntArray(data.shardDropWeights);
                        buf.writeBoolean(data.perkCapped);
                        buf.writeVarInt(data.perks.size());

                    },
                    (buf) -> {
                        FormedSetup factorySetup = new FormedSetup();
                        factorySetup.tier = Tier.byIndex(buf.readInt());
                        factorySetup.cellCapacity = buf.readInt();
                        factorySetup.controllerMobs = MOB_LIST_CODEC.decode(buf);
                        factorySetup.perks = GROUP_MAP_CODEC.decode(buf);
                        factorySetup.mobParams = PARAM_MAP_CODEC.decode(buf);
                        factorySetup.exotic = Exotic.getExotic(buf.readInt());
                        factorySetup.shardDropChance = buf.readDouble();
                        factorySetup.shardDropWeights = buf.readVarIntArray();
                        factorySetup.perkCapped = buf.readBoolean();
                        return factorySetup;

                    }
            );
}
