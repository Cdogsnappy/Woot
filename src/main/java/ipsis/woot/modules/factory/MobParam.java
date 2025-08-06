package ipsis.woot.modules.factory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Mob specific values
 */
public class MobParam {

    public int baseSpawnTicks;
    public int baseMassCount;
    public int baseFluidCost;
    public int[] perkValues;


    public MobParam(int baseSpawnTicks, int baseMassCount, int baseFluidCost, int[] perkValues){
        this.baseSpawnTicks = baseSpawnTicks;
        this.baseMassCount = baseMassCount;
        this.baseFluidCost = baseFluidCost;
        this.perkValues = perkValues;
    }

    public MobParam(){
        this(1,1,1,
                new int[]{MOB_PARAM_UNDEFINED,MOB_PARAM_UNDEFINED,MOB_PARAM_UNDEFINED,MOB_PARAM_UNDEFINED,MOB_PARAM_UNDEFINED});
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, MobParam> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MobParam::getBaseSpawnTicks,
            ByteBufCodecs.VAR_INT, MobParam::getBaseMassCount,
            ByteBufCodecs.VAR_INT, MobParam::getBaseFluidCost,
            StreamCodec.of(
                    (buf, intArray) -> {
                        // Assuming fixed size of 4
                        for (int i = 0; i < 5; i++) {
                            buf.writeVarInt(intArray[i]);
                        }
                    },
                    (buf) -> {
                        int[] array = new int[5];
                        for (int i = 0; i < 5; i++) {
                            array[i] = buf.readVarInt();
                        }
                        return array;
                    }
            ), MobParam::getPerkValues,
            MobParam::new
    );

    // int perkEfficiencyValue, int perkMassValue, int perkRateValue,int perkXpValue, int perkHeadlessValue

    private static final int MOB_PARAM_UNDEFINED = -1;




    // Mass has a value regardless of perk
    public int getMobCount(boolean hasMassPerk, boolean hasMassExotic) {
        if (hasMassExotic)
            return FactoryConfiguration.EXOTIC_E.get();

        return hasMassPerk ? perkValues[1] : baseMassCount;
    }

    public boolean hasPerkEfficiencyValue() { return perkValues[0] != MOB_PARAM_UNDEFINED; }
    public boolean hasPerkRateValue() { return perkValues[2] != MOB_PARAM_UNDEFINED; }
    public boolean hasPerkXpValue() { return perkValues[3] != MOB_PARAM_UNDEFINED; }
    public boolean hasPerkHeadlessValue() { return perkValues[4] != MOB_PARAM_UNDEFINED; }

    public int getBaseSpawnTicks() {return baseSpawnTicks;}
    public int getBaseMassCount() { return baseMassCount;}
    public int getBaseFluidCost() { return baseFluidCost;}
    public int[] getPerkValues() { return perkValues;}
    public int getPerkEfficiencyValue() { return perkValues[0]; }
    public int getPerkMassValue(){return perkValues[1];}
    public int getPerkRateValue() { return perkValues[2]; }
    public int getPerkXpValue() { return perkValues[3]; }
    public int getPerkHeadlessValue() { return perkValues[4]; }

    public void setPerkEfficiencyValue(int v) { perkValues[0] = v;}
    public void setPerkMassValue(int v) { perkValues[1] = v;}
    public void setPerkRateValue(int v) { perkValues[2] = v;}
    public void setPerkXpValue(int v) { perkValues[3] = v;}
    public void setPerkHeadlessValue(int v) { perkValues[4] = v; }
    public void setBaseSpawnTicks(int v) { baseSpawnTicks = v;}

    @Override
    public String toString() {
        return "MobParam{" +
                "baseSpawnTicks=" + baseSpawnTicks +
                ", baseMassCount=" + baseMassCount +
                ", baseFluidCost=" + baseFluidCost +
                ", perkEfficiencyValue=" +  perkValues[0] +
                ", perkMassValue=" +  perkValues[1] +
                ", perkRateValue=" +  perkValues[2] +
                ", perkXpValue=" +  perkValues[3] +
                ", perkHeadlessValue=" +  perkValues[4] +
                '}';
    }
}
