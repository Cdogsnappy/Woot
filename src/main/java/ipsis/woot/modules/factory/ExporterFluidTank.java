package ipsis.woot.modules.factory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class ExporterFluidTank implements IFluidHandler, IFluidTank
{
    public List<FluidTank> tanks = new ArrayList<>();
    int numTanks;
    int capacity;
    int cycle = 0;

    public ExporterFluidTank(int tankNum, int capacity){

        if(tankNum < 1 || capacity < 1){
            throw new IllegalArgumentException("Invalid tank configuration");
        }
        numTanks = tankNum;
        for(int i = 0; i < tankNum; ++i){
            tanks.add(new FluidTank(capacity));
        }
        this.capacity = capacity*numTanks;
    }
    @Override
    public FluidStack getFluid() {
        return tanks.getFirst().getFluid();
    }

    @Override
    public int getFluidAmount() {
        return tanks.getFirst().getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack fluidStack) {
        for(FluidTank ft : tanks){
            if(ft.isFluidValid(fluidStack)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getTanks() {
        return numTanks;
    }

    @Override
    public FluidStack getFluidInTank(int i) {
        if(i > numTanks){
            return null;
        }
        return tanks.get(i).getFluid();
    }

    @Override
    public int getTankCapacity(int i) {
        return tanks.getFirst().getCapacity();
    }

    @Override
    public boolean isFluidValid(int i, FluidStack fluidStack) {
        return tanks.get(i).isEmpty() || tanks.get(i).isFluidValid(fluidStack);
    }

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        int total = fluidStack.getAmount();
        int filled = 0;
        for(int i = 0; i < numTanks; ++i){
            if(isFluidValid(i, fluidStack)){
                filled+=tanks.get(i).fill(fluidStack, fluidAction);
                fluidStack.shrink(filled);
                if(filled == total){
                    return filled;
                }
            }
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        int drained = 0;
        for(int i = 0; i < numTanks; ++i){
            if(FluidStack.isSameFluidSameComponents(fluidStack, tanks.get(i).getFluid())){
                int justDrained = tanks.get(i).drain(fluidStack, fluidAction).getAmount();
                drained += justDrained;
                fluidStack.shrink(justDrained);
            }
        }
        return new FluidStack(fluidStack.getFluid(), drained);
    }

    @Override
    public FluidStack drain(int max, FluidAction fluidAction) {
        for(int i = 0; i < numTanks; ++i){
            if(!tanks.get(i).isEmpty()){
                return tanks.get(i).drain(max, fluidAction);
            }
        }
        return FluidStack.EMPTY;
    }


    public ExporterFluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        numTanks = nbt.getInt("num");
        capacity = nbt.getInt("cap");
        tanks = new ArrayList<>();
        ListTag tanksTag = nbt.getList("tanks", 9);
        for(int i = 0; i < numTanks; ++i){
            FluidTank toAdd = new FluidTank(capacity/numTanks);
            toAdd.readFromNBT(lookupProvider, tanksTag.getCompound(i));
            tanks.add(toAdd);
        }
        return this;
    }


    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        nbt.putInt("num", numTanks);
        nbt.putInt("cap", capacity);
        ListTag tanksTag = new ListTag();
        for(int i = 0; i < numTanks; ++i){
            if(!tanks.get(i).isEmpty()) {
                tanksTag.add(tanks.get(i).writeToNBT(lookupProvider, new CompoundTag()));
            }
        }
        nbt.put("tanks", tanksTag);

        return nbt;
    }
}
