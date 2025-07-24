package ipsis.woot.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class WootEnergyStorage extends EnergyStrage implements IEnergyStorage {


    public WootEnergyStorage(int capacity) {
        super(capacity);
    }
}