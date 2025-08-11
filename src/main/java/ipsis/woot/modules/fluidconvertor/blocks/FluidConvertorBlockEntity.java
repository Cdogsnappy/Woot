package ipsis.woot.modules.fluidconvertor.blocks;

import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.fluidconvertor.ConvertorRecipeInput;
import ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipe;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.fluidconvertor.FluidConvertorConfiguration;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.WootFluidTank;
import ipsis.woot.util.WootMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class FluidConvertorBlockEntity extends WootMachineBlockEntity implements WootDebug, MenuProvider {

    static final int INPUT_SLOT = 0;
    public FluidConvertorBlockEntity(BlockPos pos, BlockState state) {
        super(FluidConvertorSetup.FLUID_CONVERTOR_BLOCK_TILE.get(), pos, state);
        stackInputHandler = new ItemStackHandler(1)
        {
            @Override
            protected void onContentsChanged(int slot) {
                FluidConvertorBlockEntity.this.onContentsChanged(slot);
                setChanged();
            }

            public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
                if (slot == INPUT_SLOT)
                    return FluidConvertorRecipe.isValidCatalyst(stack);
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValidForSlot(slot, stack))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }



    public void configureSides() {
        Direction direction = level.getBlockState(getBlockPos()).getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (direction == Direction.NORTH) {
            settings.put(Direction.UP, Mode.INPUT);
            settings.put(Direction.DOWN, Mode.OUTPUT);

            settings.put(Direction.WEST, Mode.OUTPUT);
            settings.put(Direction.SOUTH, Mode.OUTPUT);
            settings.put(Direction.EAST, Mode.INPUT);
            settings.put(Direction.NORTH, Mode.INPUT);
        } else if (direction == Direction.SOUTH) {
            settings.put(Direction.UP, Mode.INPUT);
            settings.put(Direction.DOWN, Mode.OUTPUT);

            settings.put(Direction.WEST, Mode.INPUT);
            settings.put(Direction.SOUTH, Mode.INPUT);
            settings.put(Direction.EAST, Mode.OUTPUT);
            settings.put(Direction.NORTH, Mode.OUTPUT);
        } else if (direction == Direction.WEST) {
            settings.put(Direction.UP, Mode.INPUT);
            settings.put(Direction.DOWN, Mode.OUTPUT);

            settings.put(Direction.WEST, Mode.INPUT);
            settings.put(Direction.SOUTH, Mode.OUTPUT);
            settings.put(Direction.EAST, Mode.OUTPUT);
            settings.put(Direction.NORTH, Mode.INPUT);
        } else if (direction == Direction.EAST) {
            settings.put(Direction.UP, Mode.INPUT);
            settings.put(Direction.DOWN, Mode.OUTPUT);

            settings.put(Direction.WEST, Mode.OUTPUT);
            settings.put(Direction.SOUTH, Mode.INPUT);
            settings.put(Direction.EAST, Mode.INPUT);
            settings.put(Direction.NORTH, Mode.OUTPUT);
        }
    }


    private boolean firstTick = true;
    @Override
    public void tick(Level level) {

        if (firstTick && level != null) {
            // Configure sides needs to access the block state so cannot do onLoad
            configureSides();
            firstTick = false;
        }

        super.tick(level);

        if (!level.isClientSide)
            return;

        if (fluidOutputHandler.isEmpty())
            return;

        for (Direction direction : Direction.values()) {

            if (settings.get(direction) != Mode.OUTPUT)
                continue;

            BlockEntity te = level.getBlockEntity(getBlockPos().offset(direction.getNormal()));
            if (te == null)
                continue;

            Optional<IFluidHandler> lazyOptional = Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, te.getBlockPos(), direction.getOpposite()));
            if (lazyOptional.isPresent()) {
                IFluidHandler iFluidHandler = lazyOptional.orElseThrow(NullPointerException::new);
                FluidStack fluidStack = fluidOutputHandler.getFluid();
                if (!fluidStack.isEmpty()) {
                    int filled = iFluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    fluidOutputHandler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                }
            }
        }
    }

    //-------------------------------------------------------------------------


    public FluidStack getInputTankFluid() { return fluidInputHandler.getFluid(); }
    public FluidStack getOutputTankFluid() { return fluidOutputHandler.getFluid(); }
    //endregion

    //-------------------------------------------------------------------------

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }
    //endregion

    //-------------------------------------------------------------------------

    //endregion

    //-------------------------------------------------------------------------
    
    //endregion

    //-------------------------------------------------------------------------
    //region WootDebug
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> " + this.getClass().toString());
        debug.add("      Input Tank " + getInputTankFluid().getDescriptionId() + " " + getInputTankFluid().getAmount());
        debug.add("      Output Tank " + getOutputTankFluid().getDescriptionId() + " " + getOutputTankFluid().getAmount());
        debug.add("      Energy " + getEnergy());
        if (currRecipe != null)
            debug.add("      Energy " + currRecipe);
        debug.add("      Settings " + settings);
        return debug;
    }
    //endregion

    //-------------------------------------------------------------------------
    //region Container
    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.woot.fluidconvertor.name");
    }

    //endregion

    public int getProgress() { return calculateProgress(); }

    //-------------------------------------------------------------------------
    //region Machine Process
    private FluidConvertorRecipe currRecipe = null;

    @Override
    protected boolean hasEnergy() {
        return energyStorage.getEnergyStored() > 0;
    }

    @Override
    protected int useEnergy() {
        return energyStorage.extractEnergy(FluidConvertorConfiguration.FLUID_CONV_ENERYG_PER_TICK.get(), false);
    }

    @Override
    protected void clearRecipe() { currRecipe = null; }

    @Override
    protected int getRecipeEnergy() { return currRecipe != null ? currRecipe.getEnergy() : 0; }

    @Override
    protected void processFinish() {

        if (currRecipe == null)
            getRecipe();

        if (currRecipe == null) {
            processOff();;
            return;
        }

        FluidConvertorRecipe finishedRecipe = currRecipe;

        stackInputHandler.extractItem(0, finishedRecipe.getCatalystCount(), false);
        fluidInputHandler.drain(finishedRecipe.getInputFluid().getAmount(),
                IFluidHandler.FluidAction.EXECUTE);

        fluidOutputHandler.fill(finishedRecipe.getOutput().copy(), IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    @Override
    protected boolean canStart() {

        if (energyStorage.getEnergyStored() <= 0)
            return false;

        if (fluidInputHandler.isEmpty())
            return false;

        getRecipe();

        if (currRecipe == null)
            return false;

        // Only start if we have enough of the catalyst
        if (stackInputHandler.getStackInSlot(INPUT_SLOT).getCount() < currRecipe.getCatalystCount())
            return false;

        // Only start if we have enough input fluid
        FluidStack inFluid = fluidInputHandler.getFluid();
        if (inFluid.isEmpty())
            return false;

        if (inFluid.getAmount() < currRecipe.getInputFluid().getAmount())
            return false;

        // Only start if we can hold the output
        int amount = currRecipe.getOutput().getAmount();
        int filled = fluidOutputHandler.fill(new FluidStack(currRecipe.outputFluid().getFluidHolder(), amount), IFluidHandler.FluidAction.SIMULATE);
        return amount == filled;

    }

    @Override
    protected boolean hasValidInput() {

        if (currRecipe == null)
            getRecipe();

        if (currRecipe == null)
            return false;

        // Only valid if we have enough of the catalyst
        if (stackInputHandler.getStackInSlot(INPUT_SLOT).getCount() < currRecipe.getCatalystCount())
            return false;

        // Only valid if we have enough input fluid
        FluidStack inFluid = fluidInputHandler.getFluid();
        if (inFluid.isEmpty())
            return false;

        if (inFluid.getAmount() < currRecipe.getInputFluid().getAmount())
            return false;

        return true;
    }

    @Override
    protected boolean isDisabled() {
        return false;
    }
    //endregion

    /**
     * Get the maching recipe for the input item and the input fluid
     */
    private void getRecipe() {
        clearRecipe();

        FluidStack inFluid = fluidInputHandler.getFluid();
        if (inFluid.isEmpty()) {
            return;
        }

        ItemStack catalyst = stackInputHandler.getStackInSlot(INPUT_SLOT);
        if (catalyst.isEmpty()) {
            return;
        }

        // Get a list of recipes with matching catalyst
        RecipeHolder<FluidConvertorRecipe> recipe = level.getRecipeManager().getRecipeFor(
                WootRecipes.FLUID_CONVERTOR_TYPE.get(),
                new ConvertorRecipeInput(catalyst, inFluid),level).get();

        currRecipe = recipe == null ? null : recipe.value();

    }


    public void dropContents(Level world, BlockPos pos) {

        List<ItemStack> drops = new ArrayList<>();
        ItemStack itemStack = stackInputHandler.getStackInSlot(INPUT_SLOT);
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            stackInputHandler.insertItem(INPUT_SLOT, ItemStack.EMPTY, false);
        }
        super.dropContents(drops);
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FluidConvertorMenu(i, inventory, this);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                FluidConvertorSetup.FLUID_CONVERTOR_BLOCK_TILE.get(),
                (be, direction) -> be.energyStorage);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FluidConvertorSetup.FLUID_CONVERTOR_BLOCK_TILE.get(),
                (be, direction) -> {
                    if(direction == be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise() || direction == Direction.DOWN){
                        return be.fluidOutputHandler;
                    }
                    return be.fluidInputHandler;
                });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                FluidConvertorSetup.FLUID_CONVERTOR_BLOCK_TILE.get(),
                (be, direction) -> be.stackInputHandler);
    }

}
