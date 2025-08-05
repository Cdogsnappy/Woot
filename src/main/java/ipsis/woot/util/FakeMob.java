package ipsis.woot.util;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Slime;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FakeMob {

    private static final String INVALID_ENTITY_KEY = "INVALID";
    private static final String EMPTY_TAG = "";

    private String entityKey;
    private String tag;
    private String name;

    public static final StreamCodec<FriendlyByteBuf, FakeMob> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, FakeMob::getEntityKey,
            ByteBufCodecs.STRING_UTF8, FakeMob::getTag,
            FakeMob::new
    );


    public static final Codec<FakeMob> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(inst ->
        inst.group(ExtraCodecs.NON_EMPTY_STRING.fieldOf("entityKey").forGetter(FakeMob::getEntityKey),
                ExtraCodecs.NON_EMPTY_STRING.fieldOf("tag").forGetter(FakeMob::getTag)).apply(inst, FakeMob::new)
    ));


    public FakeMob() {
        this(INVALID_ENTITY_KEY, EMPTY_TAG);
    }

    // This handles "minecraft:slime" and "minecraft:slime,small" style strings
    public FakeMob(String s) {
        this();
        String[] parts = s.split(Pattern.quote(","));
        if (parts.length == 1)
            setInfo(s, EMPTY_TAG);
        else if (parts.length == 2)
            setInfo(parts[0], parts[1]);
    }

    private FakeMob(String entityKey, String tag) {
        setInfo(entityKey, tag);
    }

    public FakeMob(FakeMob fakeMob) {
        this(fakeMob.getEntityKey(), fakeMob.getTag());
    }

    public FakeMob(Mob mobEntity) {
        this();

        if (mobEntity.toString() == null) {
            setInfo(INVALID_ENTITY_KEY, EMPTY_TAG);
        } else {
            if (isSlime(mobEntity)) {
                if (((Slime) mobEntity).getSize() == 1)
                    setInfo(mobEntity.toString(), SMALL_TAG);
                else
                    setInfo(mobEntity.toString(), LARGE_TAG);
            } else if (isMagmaCube(mobEntity)) {
                if (((MagmaCube) mobEntity).getSize() == 1)
                    setInfo(mobEntity.toString(), SMALL_TAG);
                else
                    setInfo(mobEntity.toString(), LARGE_TAG);
            } else if (isChargedCreeper(mobEntity)) {
                setInfo(mobEntity.toString(), CHARGED_TAG);
            } else {
                setInfo(mobEntity.toString(), EMPTY_TAG);
            }
        }
    }

    private void setInfo(String entityKey, String tag) {
        this.entityKey = entityKey;
        this.tag = tag;
        this.name = entityKey;
        if (!this.tag.equalsIgnoreCase(EMPTY_TAG))
            this.name += "," + this.tag;
    }

    @Override
    public String toString() {
        return this.name;
    }

    private String getEntityKey() { return entityKey; }
    public String getTag() { return tag; }
    public String getName() { return name; }
    public boolean hasTag() { return !this.tag.equalsIgnoreCase(EMPTY_TAG); }

    public @Nonnull
    ResourceLocation getResourceLocation() { return  ResourceLocation.read(entityKey).getOrThrow(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FakeMob fakeMob = (FakeMob) o;
        return entityKey.equalsIgnoreCase(fakeMob.entityKey) &&
                tag.equalsIgnoreCase(fakeMob.tag) &&
                name.equalsIgnoreCase(fakeMob.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityKey, tag, name);
    }

    /**
     * Does not guarantee that an entity can be created from this entry
     */
    public boolean isValid() { return !this.entityKey.equalsIgnoreCase(INVALID_ENTITY_KEY); }

    /**
     * Checks to see if the key represents a valid entity in the entity list.
     * NB the entity list contains mobs and other world entities such as lightning bolts
     * @return true if the key is in the EntityList
     */
    public static boolean isInEntityList(FakeMob fakeMob) {
        return BuiltInRegistries.ENTITY_TYPE.containsKey(fakeMob.getResourceLocation());
    }

    private static final String KEY_ENTITY = "keyEntity";
    private static final String KEY_TAG = "keyTag";
    public static void writeToNBT(@Nonnull FakeMob fakeMob, CompoundTag nbtTagCompound) {
        if (fakeMob.isValid()) {
            if (nbtTagCompound == null)
                nbtTagCompound = new CompoundTag();

            nbtTagCompound.putString(KEY_ENTITY, fakeMob.entityKey);
            nbtTagCompound.putString(KEY_TAG, fakeMob.tag);
        }
    }

    public FakeMob(CompoundTag nbtTagCompound) {
        this();
        if (nbtTagCompound != null && nbtTagCompound.contains(KEY_ENTITY) && nbtTagCompound.contains(KEY_TAG))
            setInfo(nbtTagCompound.getString(KEY_ENTITY), nbtTagCompound.getString(KEY_TAG));
    }

    /**
     * Custom tags for mobs
     */
    private static final String CHARGED_TAG = "charged";
    private static final String SMALL_TAG = "small";
    private static final String LARGE_TAG = "large";
    private static final String CREEPER = "minecraft:creeper";
    private static final String SLIME = "minecraft:slime";
    private static final String MAGMA_CUBE = "minecraft:magma_cube";
    private static final String ENDER_DRAGON = "minecraft:ender_dragon";
    private static final String WITHER = "minecraft:wither";
    private static final String SHEEP = "minecraft:sheep";
    private static final String SKELETON = "minecraft:skeleton";
    private static final String WITHER_SKELETON = "minecraft:wither_skeleton";
    private static final String ZOMBIE = "minecraft:zombie";

    private boolean isChargedCreeper(Mob mobEntity) {
        // shouldRenderOverlay reports the POWERED state of the entity
        return mobEntity instanceof Creeper && ((Creeper)mobEntity).isPowered();
    }

    private boolean isSlime(Mob mobEntity) { return mobEntity instanceof Slime; }

    private boolean isMagmaCube(Mob mobEntity) { return mobEntity instanceof MagmaCube; }

    public boolean isChargedCreeper() {
        return isCreeper() && tag.equalsIgnoreCase(CHARGED_TAG);
    }

    public boolean isSmallSlime() {
        return getEntityKey().equalsIgnoreCase(SLIME) && tag.equalsIgnoreCase(SMALL_TAG);
    }

    public boolean isSmallMagmaCube() {
        return getEntityKey().equalsIgnoreCase(MAGMA_CUBE) && tag.equalsIgnoreCase(SMALL_TAG);
    }

    public boolean isSheep() {
        return getEntityKey().equalsIgnoreCase(SHEEP);
    }

    public boolean isSkeleton() {
        return getEntityKey().equalsIgnoreCase(SKELETON);
    }

    public boolean isWitherSkeleton() {
        return getEntityKey().equalsIgnoreCase(WITHER_SKELETON);
    }

    public boolean isWither() {
        return getEntityKey().equalsIgnoreCase(WITHER);
    }

    public boolean isZombie() {
        return getEntityKey().equalsIgnoreCase(ZOMBIE);
    }

    public boolean isCreeper() {
        return getEntityKey().equalsIgnoreCase(CREEPER);
    }

    public static FakeMob getEnderDragon() { return new FakeMob(ENDER_DRAGON); }
    public static FakeMob getWither() { return new FakeMob(WITHER); }

}
