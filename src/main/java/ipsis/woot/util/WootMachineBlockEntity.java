package ipsis.woot.util;

import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.util.helper.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Implements a ticking machine with recipes that uses energy
 * This requires that enough power is available per tick to progress the recipe.
 * If you lose power then the progress is reset
 *
 * This is HEAVILY based off the CoFH's TileMachineBase processing algorithm
 *
 */
public class WootMachineBlockEntity extends BlockEntity {

    protected enum Mode {
        NONE, INPUT, OUTPUT
    }
    protected HashMap<Direction, Mode> settings = new HashMap<>();

    protected static final Logger LOGGER = LogManager.getLogger();

    public IEnergyStorage energyStorage = new WootEnergyStorage(10000);

    public ItemStackHandler stackInputHandler = new ItemStackHandler();

    public ItemStackHandler stackOutputHandler = new ItemStackHandler();

    public FluidTank fluidInputHandler = new FluidTank(5000);

    public FluidTank fluidOutputHandler = new FluidTank(5000);

    public WootMachineBlockEntity(BlockPos pos, BlockState state) {
        super(FactorySetup.WOOT_MACHINE_ENTITY.get(), pos, state);
        for (Direction direction : Direction.values())
            settings.put(direction, Mode.NONE);
    }

    public WootMachineBlockEntity(BlockEntityType<?> entityType, BlockPos pos, BlockState state) {
        super(entityType, pos, state);
        for (Direction direction : Direction.values())
            settings.put(direction, Mode.NONE);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(ModNBT.ENERGY_TAG, energyStorage.getEnergyStored());
        tag.put(ModNBT.INPUT_INVENTORY_TAG, stackInputHandler.serializeNBT(registries));
        tag.put(ModNBT.OUTPUT_INVENTORY_TAG, stackOutputHandler.serializeNBT(registries));
        tag.put(ModNBT.INPUT_TANK_TAG, fluidInputHandler.writeToNBT(registries, new CompoundTag()));
        tag.put(ModNBT.OUTPUT_TANK_TAG, fluidOutputHandler.writeToNBT(registries, new CompoundTag()));
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if(tag.contains(ModNBT.ENERGY_TAG)) {
            energyStorage = new WootEnergyStorage(10000, 1000, 1000, tag.getInt(ModNBT.ENERGY_TAG));
        }
        if(tag.contains(ModNBT.INPUT_INVENTORY_TAG)){
            stackInputHandler.deserializeNBT(registries, tag.getCompound(ModNBT.INPUT_INVENTORY_TAG));
        }
        if(tag.contains(ModNBT.OUTPUT_INVENTORY_TAG)){
            stackOutputHandler.deserializeNBT(registries, tag.getCompound(ModNBT.OUTPUT_INVENTORY_TAG));
        }
        if(tag.contains(ModNBT.INPUT_TANK_TAG)){
            fluidInputHandler.readFromNBT(registries, tag.getCompound(ModNBT.INPUT_TANK_TAG));
        }
        if(tag.contains(ModNBT.OUTPUT_TANK_TAG)){
            fluidOutputHandler.readFromNBT(registries, tag.getCompound(ModNBT.OUTPUT_TANK_TAG));
        }
        super.loadAdditional(tag, registries);
    }



    public void tick(Level level) {
        if (level.isClientSide)
            return;

        machineTick();
    }

    private boolean isActive = false;
    private int processMax = 0; // total energy needed
    private int processRem = 0; // energy still to use
    private void machineTick() {

        if (isActive) {
            // Not physically disabled and we have a valid set of input items
            //LOGGER.info("machineTick: running");
            processTick(); // progress by one tick, consume energy
            if (canFinish()) {
                // energy required for recipe all used, input items still valid
                //LOGGER.info("machineTick: finished");
                processFinish(); //
                if (isDisabled() || !canStart()) {
                    //  disabled via redstone or dont have a valid set of input items and enough energy
                    processOff();
                    isActive = false;
                    //LOGGER.info("machineTick: redstone disabled or cannot start -> turn off");
                } else {
                    processStart(); // set processMax and processRem
                }
            } else if (!hasEnergy()) {
                //LOGGER.info("machineTick: no energy");
                processOff();;
            }
        } else if (!isDisabled()) {
            if (level.getGameTime() % 10 == 0 && canStart()) {
                // have a valid set of input items and enough energy
                processStart(); // set processMax and processRem
                processTick(); // use energy and update processRem
                isActive = true;
                //LOGGER.info("machineTick: turn on");
            }
        }
    }

    /**
     * All inputs must be present to form a recipe
     * Energy cell must not be empty
     * Must be space for all outputs
     */
    protected boolean canStart(){return false;}

    /**
     * Remove the inputs and generate the outputs
     */
    protected void processFinish(){}

    /**
     * All inputs must be present to form a recipe
     * Energy and output space are not valid
     */
    protected boolean hasValidInput(){return false;}

    protected boolean hasEnergy(){return false;}
    protected int useEnergy(){return 0;}
    protected void clearRecipe(){}
    protected int getRecipeEnergy(){return 0;}
    protected boolean isDisabled(){return false;}

    /**
     *
     * If energy still needs to be consumed then do so
     * Returns the amount of energy used
     */
    private int processTick() {
        //LOGGER.info("processTick: processRem {}", processRem);
        if (processRem <= 0)
            return 0;

        int energy = useEnergy();
        //LOGGER.info("processTick: energy used {}", energy);
        processRem -= energy;
        return energy;
    }

    /**
     * Can finish if all the energy has been consumed and the inputs still give a valid recipe
     */
    private boolean canFinish() {
        return processRem <= 0 && hasValidInput();
    }

    protected int calculateProgress() {
        return processMax == 0 ? 0 : 100 - (int)((100.0F / processMax) * processRem);
    }

    protected void processOff() {
        //LOGGER.info("processOff: clearing remainder and recipe");
        isActive = false;
        processRem = 0;
        processMax = 0;
        clearRecipe();
    }

    /**
     * Setup the required energy for the recipe
     */
    private void processStart() {
        processMax = getRecipeEnergy();
        processRem = getRecipeEnergy();
    }

    public void onContentsChanged(int slot) {
       if (!isActive)
           return;

       if (!hasValidInput())
           processOff();
    }

    public void dropContents(List<ItemStack> items) {
        for (ItemStack itemStack : items) {
            if (itemStack.isEmpty())
                continue;
            Containers.dropItemStack(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), itemStack);
        }
        setChanged();
        WorldHelper.updateClient(level, getBlockPos());
    }




}
