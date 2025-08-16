package ipsis.woot.modules.factory.blocks;

import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.factory.ExporterFluidTank;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.multiblock.MultiBlockBlockEntity;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.util.helper.StorageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExporterBlockEntity extends MultiBlockBlockEntity {

    static int LAZY_TICK = 40;
    static int FLUID_ROTATION_TICK = 200;

    int tick=0;
    int cycleTick = 0;

    ExporterFluidTank fluidOutputHandler;
    ItemStackHandler stackOutputHandler;


    public ExporterBlockEntity(BlockPos pos, BlockState state) {
        super(FactorySetup.EXPORTER_BLOCK_ENTITY.get(), pos, state);
        stackOutputHandler = new ItemStackHandler(27);
        fluidOutputHandler = new ExporterFluidTank(4,10000);
    }


    public void tick() {
        tick++;
        cycleTick++;
        if(cycleTick > FLUID_ROTATION_TICK){
            fluidOutputHandler.tanks.add(fluidOutputHandler.tanks.removeFirst());
            cycleTick = 0;
        }
        if(tick < LAZY_TICK){
            return;
        }

        tick = 0;
        List<Optional<IItemHandler>> handlers = new ArrayList<>();
        for (Direction facing : Direction.values()) {
            if (!level.isLoaded(getBlockPos().offset(facing.getNormal())))
                continue;
            BlockEntity te = level.getBlockEntity(getBlockPos().offset(facing.getNormal()));
            if (te == null || te instanceof MultiBlockBlockEntity)
                continue;

            handlers.add(Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, te.getBlockPos(), facing.getOpposite())));
        }
        StorageHelper.insertItems(stackOutputHandler, handlers);
    }

    public List<Optional<IItemHandler>> getExportHandlers() {
        List<Optional<IItemHandler>> handlers = new ArrayList<>();
        for (Direction facing : Direction.values()) {
            if (!level.isLoaded(getBlockPos().offset(facing.getNormal())))
                continue;
            BlockEntity te = level.getBlockEntity(getBlockPos().offset(facing.getNormal()));
            if (te == null)
                continue;

            handlers.add(Optional.ofNullable(level.getCapability(Capabilities.ItemHandler.BLOCK, te.getBlockPos(), facing.getOpposite())));
        }
        return handlers;
    }


    public void insertStacks(List<ItemStack> stacks){
        for (ItemStack stack : stacks) {
            ItemStack currStack = stack.copy();
            for (int i = 0; i < stackOutputHandler.getSlots(); ++i) {
                if(stackOutputHandler.isItemValid(i, stack)){
                    currStack = stackOutputHandler.insertItem(i, stack, false);
                    if(currStack.getCount() == 0){
                        break;
                    }
                }
            }
        }

    }
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(ModNBT.OUTPUT_INVENTORY_TAG, stackOutputHandler.serializeNBT(registries));
        tag.put(ModNBT.OUTPUT_TANK_TAG, fluidOutputHandler.writeToNBT(registries, new CompoundTag()));
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if(tag.contains(ModNBT.OUTPUT_INVENTORY_TAG)) {
            stackOutputHandler.deserializeNBT(registries, tag.getCompound(ModNBT.OUTPUT_INVENTORY_TAG));
        }
        if(tag.contains(ModNBT.OUTPUT_TANK_TAG)){
            fluidOutputHandler.readFromNBT(registries, tag.getCompound(ModNBT.OUTPUT_TANK_TAG));
        }
        super.loadAdditional(tag, registries);
    }

    public void insertFluids(List<FluidStack> fluids){
        for(FluidStack stack : fluids){
            fluidOutputHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }


    public static void registerCapabilities(RegisterCapabilitiesEvent event){

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                FactorySetup.EXPORTER_BLOCK_ENTITY.get(),
                (be, direction) -> be.stackOutputHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FactorySetup.EXPORTER_BLOCK_ENTITY.get(),
                (be, direction) -> be.fluidOutputHandler);
    }


}
