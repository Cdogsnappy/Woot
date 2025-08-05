package ipsis.woot.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class WootEnergyStorage extends EnergyStorage implements IEnergyStorage {


    public WootEnergyStorage(int capacity) {
        super(capacity);
    }

    public WootEnergyStorage(int capacity, int maxReceive){
        super(capacity, maxReceive, capacity, capacity);
    }

    public WootEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }


    public void setEnergy(int energy){

    }
}