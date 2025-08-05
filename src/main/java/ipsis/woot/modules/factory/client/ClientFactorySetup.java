package ipsis.woot.modules.factory.client;

import io.netty.buffer.ByteBuf;
import ipsis.woot.modules.factory.Exotic;
import ipsis.woot.modules.factory.MobParam;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.NetworkHelper;
import ipsis.woot.util.oss.NetworkTools;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.checkerframework.checker.units.qual.K;

import java.rmi.registry.Registry;
import java.util.*;

public class ClientFactorySetup {

    public Tier tier = Tier.TIER_1;
    public List<FakeMob> controllerMobs = new ArrayList<>();
    public List<Perk> perks = new ArrayList<>();
    public HashMap<FakeMob, MobParam> mobParams = new HashMap<>();
    public HashMap<FakeMob, Mob> mobInfo = new HashMap<>();
    public Exotic exotic = Exotic.NONE;
    public int cellCapacity = 0;
    public int looting = 0;
    public int recipeTicks = 0;
    public int recipeFluid = 0;
    public boolean perkCapped = false;

    // shards
    public int shardRolls = 1;
    public double shardDropChance = 0.0F;
    public double[] shardDrops = new double[]{ 0.0F, 0.0F, 0.0F };

    public static record Mob (List<ItemStack> drops) {
        public Mob(){
            this(new ArrayList<>());
        }

        public static final StreamCodec<RegistryFriendlyByteBuf, Mob> STREAM_CODEC =
                StreamCodec.composite(ByteBufCodecs.collection(ArrayList::new,ItemStack.STREAM_CODEC), Mob::drops,
                        Mob::new);
    }


    public List<ItemStack> itemIng = new ArrayList<>();
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> ITEM_LIST_CODEC = ByteBufCodecs.collection(
            ArrayList::new, ItemStack.STREAM_CODEC);

    public List<FluidStack> fluidIng = new ArrayList<>();

    public static final StreamCodec<RegistryFriendlyByteBuf, List<FluidStack>> FLUID_LIST_CODEC = ByteBufCodecs.collection(
            ArrayList::new, FluidStack.STREAM_CODEC);

    private ClientFactorySetup() {}


    public static final StreamCodec<RegistryFriendlyByteBuf, HashMap<FakeMob, Mob>> MAP_CODEC =
            StreamCodec.of(
                    (buf, map) -> {
                        buf.writeVarInt(map.size());
                        map.forEach((key, value) -> {
                            FakeMob.STREAM_CODEC.encode(buf, key);
                            Mob.STREAM_CODEC.encode(buf, value);
                        });
                    },
                    (buf) -> {
                        int size = buf.readVarInt();
                        HashMap<FakeMob, Mob> map = new HashMap<>(size);
                        for (int i = 0; i < size; i++) {
                            FakeMob key = FakeMob.STREAM_CODEC.decode(buf);
                            Mob value = Mob.STREAM_CODEC.decode(buf);
                            map.put(key, value);
                        }
                        return map;
                    }
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientFactorySetup> STREAM_CODEC =
            StreamCodec.of(
                    (buf,data) ->{
                        buf.writeInt(data.tier.ordinal());
                        buf.writeInt(data.cellCapacity);
                        buf.writeInt(data.looting);
                        buf.writeInt(data.exotic.ordinal());
                        buf.writeInt(data.recipeTicks);
                        buf.writeInt(data.recipeFluid);
                        buf.writeInt(data.shardRolls);
                        buf.writeDouble(data.shardDropChance);
                        buf.writeArray(Arrays.stream(data.shardDrops).boxed().toArray(Double[]::new), ByteBufCodecs.DOUBLE);
                        buf.writeMap(data.mobParams, FakeMob.STREAM_CODEC, MobParam.STREAM_CODEC);
                        MAP_CODEC.encode(buf, data.mobInfo);
                        buf.writeBoolean(data.perkCapped);
                        buf.writeVarInt(data.perks.size());
                        data.perks.forEach((p) -> {
                            buf.writeVarInt(p.ordinal());
                        });

                        ITEM_LIST_CODEC.encode(buf, data.itemIng);
                        FLUID_LIST_CODEC.encode(buf, data.fluidIng);

                    },
                    (buf) -> {
                        ClientFactorySetup factorySetup = new ClientFactorySetup();
                        factorySetup.tier = Tier.byIndex(buf.readInt());
                        factorySetup.cellCapacity = buf.readInt();
                        buf.readInt(); /// fluid amount
                        factorySetup.looting = buf.readInt();
                        factorySetup.exotic = Exotic.getExotic(buf.readInt());

                        factorySetup.recipeTicks = buf.readInt();
                        factorySetup.recipeFluid = buf.readInt();
                        factorySetup.shardRolls = buf.readInt();
                        factorySetup.shardDropChance = buf.readDouble();
                        factorySetup.shardDrops[0] = buf.readDouble();
                        factorySetup.shardDrops[1] = buf.readDouble();
                        factorySetup.shardDrops[2] = buf.readDouble();
                        factorySetup.mobParams = (HashMap<FakeMob,MobParam>)(buf.readMap(FakeMob.STREAM_CODEC,MobParam.STREAM_CODEC));
                        factorySetup.mobInfo = MAP_CODEC.decode(buf);
                        factorySetup.controllerMobs = factorySetup.mobParams.keySet().stream().toList();
                        factorySetup.perkCapped = buf.readBoolean();
                        int size = buf.readVarInt();
                        for(int i = 0; i < size; ++i){
                            factorySetup.perks.add(Perk.byIndex(buf.readVarInt()));
                        }

                        factorySetup.itemIng = ITEM_LIST_CODEC.decode(buf);
                        factorySetup.fluidIng = FLUID_LIST_CODEC.decode(buf);


                        return factorySetup;

                    }
            );
}
