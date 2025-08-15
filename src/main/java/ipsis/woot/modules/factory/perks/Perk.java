package ipsis.woot.modules.factory.perks;



import ipsis.woot.util.ExtraWootCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.*;

public enum Perk implements StringRepresentable {

    empty(null),
    efficiency_1(Group.efficiency),
    efficiency_2(Group.efficiency),
    efficiency_3(Group.efficiency),
    looting_1(Group.looting),
   looting_2(Group.looting),
    looting_3(Group.looting),
    mass_1(Group.mass),
    mass_2(Group.mass),
    mass_3(Group.mass),
    rate_1(Group.rate),
    rate_2(Group.rate),
    rate_3(Group.rate),
    tier_shard_1(Group.tier_shard),
    tier_shard_2(Group.tier_shard),
    tier_shard_3(Group.tier_shard),
    xp_1(Group.xp),
    xp_2(Group.xp),
    xp_3(Group.xp),
    headless_1(Group.headless),
    headless_2(Group.headless),
    headless_3(Group.headless),
    slaughter_1(Group.slaughter),
    slaughter_2(Group.slaughter),
    slaughter_3(Group.slaughter),
    crusher_1(Group.crusher),
    crusher_2(Group.crusher),
    crusher_3(Group.crusher),
    laser_1(Group.laser),
    laser_2(Group.laser),
    laser_3(Group.laser),
    flayed_1(Group.flayed),
    flayed_2(Group.flayed),
    flayed_3(Group.flayed)
    ;

    private Group group;
    Perk(Group group) {
        this.group = group;
    }

    public static Perk[] VALUES = values();
    public String getLowerCaseName() { return name().toLowerCase(Locale.ROOT); }
    public String getString() { return getLowerCaseName(); }

    public static Perk byIndex(int index) {
        index = Math.clamp(index, 0, VALUES.length - 1);
        return VALUES[index];
    }

    public static final EnumSet<Perk> LEVEL_1_PERKS = EnumSet.of(efficiency_1, looting_1, mass_1, rate_1, tier_shard_1, xp_1, headless_1, slaughter_1, crusher_1, laser_1, flayed_1);
    public static final EnumSet<Perk> LEVEL_2_PERKS = EnumSet.of(efficiency_2, looting_2, mass_2, rate_2, tier_shard_2, xp_2, headless_2, slaughter_2, crusher_2, laser_2, flayed_2);
    public static final EnumSet<Perk> LEVEL_3_PERKS = EnumSet.of(efficiency_3, looting_3, mass_3, rate_3, tier_shard_3, xp_3, headless_3, slaughter_3, crusher_3, laser_3, flayed_3);

    public static final EnumSet<Perk> EFFICIENCY_PERKS = EnumSet.of(efficiency_1, efficiency_2, efficiency_3);
    public static final EnumSet<Perk> LOOTING_PERKS = EnumSet.of(looting_1, looting_2, looting_3);
    public static final EnumSet<Perk> MASS_PERKS = EnumSet.of(mass_1, mass_2, mass_3);
    public static final EnumSet<Perk> RATE_PERKS = EnumSet.of(rate_1, rate_2, rate_3);
    public static final EnumSet<Perk> XP_PERKS = EnumSet.of(xp_1, xp_2, xp_3);
    public static final EnumSet<Perk> TIER_SHARD_PERKS = EnumSet.of(tier_shard_1, tier_shard_2, tier_shard_3);
    public static final EnumSet<Perk> HEADLESS_PERKS = EnumSet.of(headless_1, headless_2, headless_3);
    public static final EnumSet<Perk> SLAUGHTER_PERKS = EnumSet.of(slaughter_1, slaughter_2, slaughter_3);
    public static final EnumSet<Perk> CRUSHER_PERKS = EnumSet.of(crusher_1, crusher_2, crusher_3);
    public static final EnumSet<Perk> LASER_PERKS = EnumSet.of(laser_1, laser_2, laser_3);
    public static final EnumSet<Perk> FLAYED_PERKS = EnumSet.of(flayed_1, flayed_2, flayed_3);

    private static final Map<Group, EnumSet<Perk>> perkMap = new HashMap<>();
    static {
        perkMap.put(Group.efficiency, EFFICIENCY_PERKS);
        perkMap.put(Group.looting, LOOTING_PERKS);
        perkMap.put(Group.mass, MASS_PERKS);
        perkMap.put(Group.rate, RATE_PERKS);
        perkMap.put(Group.xp, XP_PERKS);
        perkMap.put(Group.tier_shard, TIER_SHARD_PERKS);
        perkMap.put(Group.headless, HEADLESS_PERKS);
        perkMap.put(Group.slaughter, SLAUGHTER_PERKS);
        perkMap.put(Group.crusher, CRUSHER_PERKS);
        perkMap.put(Group.laser, LASER_PERKS);
        perkMap.put(Group.flayed, FLAYED_PERKS);
    }

    public static EnumSet<Perk> getPerksByGroup(Group group) {
        EnumSet<Perk> perks = perkMap.get(group);
        // Have we forgotten to add a new map entry for a new group
        if (perks == null)
            throw new IllegalArgumentException("No map entry for perk group");
        return perkMap.get(group);
    }

    public static Group getGroup(Perk perk) {
        return perk.group;
    }

    public static int getLevel(Perk perk) {
        int level = 3;
        if (LEVEL_1_PERKS.contains(perk))
            level = 1;
        else if (LEVEL_2_PERKS.contains(perk))
            level = 2;
        return level;
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }

    public enum Group {
        efficiency,
        looting,
        mass,
        rate,
        xp,
        tier_shard,
        headless,

        // Industrial Foregoing
        slaughter, crusher, laser,

        // Blood Magic
        flayed
        ;

        private static final Group[] VALUES = Group.values();
        public static Group byIndex(int index) {
            index = Math.clamp(index, 0, VALUES.length - 1);
            return VALUES[index];
        }

        public String getLowerCaseName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Group> STREAM_CODEC = ExtraWootCodecs.enumStreamCodec(Group.class);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, Perk> STREAM_CODEC = ExtraWootCodecs.enumStreamCodec(Perk.class);

}
