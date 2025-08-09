package ipsis.woot.modules.factory.blocks;

import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.factory.multiblock.MultiBlockBlockEntity;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;


import java.util.List;
import java.util.Optional;

public abstract class CellBlockEntityBase extends MultiBlockBlockEntity implements WootDebug {

    protected FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME);
    private final Optional<IFluidHandler> holder = Optional.of(tank);

    public CellBlockEntityBase(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(pos, state);
        tank.setCapacity(getCapacity());
        tank.setValidator(e -> e.getFluid() == FluidSetup.CONATUS_FLUID.get().getSource());
    }


    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        readFromNBT(tag);
    }

    private void readFromNBT(CompoundTag compound) {
        if (compound.contains(ModNBT.TANK_TAG))
            tank.readFromNBT(this.level.registryAccess(), compound.getCompound(ModNBT.TANK_TAG));
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        CompoundTag tankNBT = new CompoundTag();
        tank.writeToNBT(registries, tankNBT);
        compound.put(ModNBT.TANK_TAG, tankNBT);
    }

    /**
     * For testing and the tick converter
     */
    public void fillToCapacity() {
        tank.fill(new FluidStack(FluidSetup.CONATUS_FLUID.get(),
                getCapacity()), IFluidHandler.FluidAction.EXECUTE);
    }

    public abstract int getCapacity();
    public abstract int getMaxTransfer();

    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> CellTileEntity");
        debug.add("      hasMaster: " + glue.hasMaster());
        debug.add("      capacity: " + tank.getCapacity());
        debug.add("      transfer: " + getMaxTransfer());
        debug.add("      contains: " + tank.getFluid().getDescriptionId());
        debug.add("      contains: " + tank.getFluid().getAmount());
        return debug;
    }
}
