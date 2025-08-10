package ipsis.woot.modules.factory.client;

import ipsis.woot.modules.factory.Exotic;
import ipsis.woot.modules.factory.MobParam;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.util.ExtraWootCodecs;
import ipsis.woot.util.FakeMob;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

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


    public List<FluidStack> fluidIng = new ArrayList<>();


    public ClientFactorySetup() {}



    public static final StreamCodec<RegistryFriendlyByteBuf, HashMap<FakeMob, ClientFactorySetup.Mob>> MAP_CODEC =
            ExtraWootCodecs.mapStreamCodec(FakeMob.STREAM_CODEC, ClientFactorySetup.Mob.STREAM_CODEC, HashMap::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, HashMap<FakeMob, MobParam>> PARAM_MAP_CODEC =
            ExtraWootCodecs.mapStreamCodec(FakeMob.STREAM_CODEC, MobParam.STREAM_CODEC, HashMap::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, List<FakeMob>> MOB_LIST_CODEC =
            ExtraWootCodecs.listStreamCodec(FakeMob.STREAM_CODEC);


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
                        PARAM_MAP_CODEC.encode(buf, data.mobParams);
                        MAP_CODEC.encode(buf, data.mobInfo);
                        buf.writeBoolean(data.perkCapped);
                        buf.writeVarInt(data.perks.size());
                        data.perks.forEach((p) -> {
                            buf.writeVarInt(p.ordinal());
                        });

                        ExtraWootCodecs.ITEM_LIST_CODEC.encode(buf, data.itemIng);
                        ExtraWootCodecs.FLUID_LIST_CODEC.encode(buf, data.fluidIng);

                    },
                    (buf) -> {
                        ClientFactorySetup factorySetup = new ClientFactorySetup();
                        factorySetup.tier = Tier.byIndex(buf.readInt());
                        factorySetup.cellCapacity = buf.readInt();
                        factorySetup.looting = buf.readInt();
                        factorySetup.exotic = Exotic.getExotic(buf.readInt());

                        factorySetup.recipeTicks = buf.readInt();
                        factorySetup.recipeFluid = buf.readInt();
                        factorySetup.shardRolls = buf.readInt();
                        factorySetup.shardDropChance = buf.readDouble();
                        factorySetup.shardDrops = Arrays.stream(buf.readArray(Double[]::new, ByteBufCodecs.DOUBLE)).mapToDouble(Double::doubleValue).toArray();
                        factorySetup.mobParams = PARAM_MAP_CODEC.decode(buf);
                        factorySetup.mobInfo = MAP_CODEC.decode(buf);
                        factorySetup.controllerMobs = factorySetup.mobParams.keySet().stream().toList();

                        factorySetup.perkCapped = buf.readBoolean();
                        int size = buf.readVarInt();
                        for(int i = 0; i < size; ++i){
                            factorySetup.perks.add(Perk.byIndex(buf.readVarInt()));
                        }

                        factorySetup.itemIng = ExtraWootCodecs.ITEM_LIST_CODEC.decode(buf);
                        factorySetup.fluidIng = ExtraWootCodecs.FLUID_LIST_CODEC.decode(buf);


                        return factorySetup;

                    }
            );
}
