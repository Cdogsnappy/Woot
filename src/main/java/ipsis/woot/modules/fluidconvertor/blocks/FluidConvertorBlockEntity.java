package ipsis.woot.modules.fluidconvertor.blocks;

import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.fluidconvertor.ConvertorRecipeInput;
import ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipe;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.fluidconvertor.FluidConvertorConfiguration;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
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

import static ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipe.FLUID_CONV_TYPE;

public class FluidConvertorBlockEntity extends WootMachineBlockEntity implements WootDebug, MenuProvider {

    public FluidConvertorBlockEntity(BlockPos pos, BlockState state) {
        super(FluidConvertorSetup.FLUID_CONVERTOR_BLOCK_TILE.get(), pos, state);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(1)
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

        if (outputTank.map(WootFluidTank::isEmpty).orElse(true))
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
                FluidStack fluidStack = outputTank.map(WootFluidTank::getFluid).orElse(FluidStack.EMPTY);
                if (!fluidStack.isEmpty()) {
                    int filled = iFluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    outputTank.ifPresent(f -> f.internalDrain(filled, IFluidHandler.FluidAction.EXECUTE));
                    setChanged();
                }
            }
        }
    }

    //-------------------------------------------------------------------------
    //region Tanks
    private Optional<FluidTank> inputTank = Optional.of(this.createInputTank());
    private Optional<WootFluidTank> outputTank = Optional.of(this.createOutputTank());
    private FluidTank createInputTank() {
        return new FluidTank(FluidConvertorConfiguration.FLUID_CONV_INPUT_TANK_CAPACITY.get());
    }
    private WootFluidTank createOutputTank() {
        return new WootFluidTank(FluidConvertorConfiguration.FLUID_CONV_OUTPUT_TANK_CAPACITY.get()).setAccess(false, true);
    }

    public FluidStack getInputTankFluid() { return inputTank.map(h -> h.getFluid()).orElse(FluidStack.EMPTY); }
    public FluidStack getOutputTankFluid() { return outputTank.map(h -> h.getFluid()).orElse(FluidStack.EMPTY); }
    //endregion

    //-------------------------------------------------------------------------
    //region Energy
    private Optional<EnergyStorage> energyStorage = Optional.of(this.createEnergy());
    private EnergyStorage createEnergy() {
        return new EnergyStorage(FluidConvertorConfiguration.FLUID_CONV_MAX_ENERGY.get(), FluidConvertorConfiguration.FLUID_CONV_MAX_ENERGY_RX.get());
    }

    public int getEnergy() {
        return energyStorage.map(h -> h.getEnergyStored()).orElse(0);
    }
    //endregion

    //-------------------------------------------------------------------------
    //region Inventory
    public static final int INPUT_SLOT = 0;
    private final Optional<IItemHandler> inventoryGetter = Optional.of(inventory);
    public IItemHandler getInventory() { return inventory; }
    //endregion

    //-------------------------------------------------------------------------


    @Override
    public void loadAdditional( CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(ModNBT.INPUT_INVENTORY_TAG))
            inventory.deserializeNBT(registries, tag.getCompound(ModNBT.INPUT_INVENTORY_TAG));

        CompoundTag inputTankTag = tag.getCompound(ModNBT.INPUT_TANK_TAG);
        inputTank.ifPresent(h -> h.readFromNBT(registries, inputTankTag));

        CompoundTag outputTankTag = tag.getCompound(ModNBT.OUTPUT_TANK_TAG);
        outputTank.ifPresent(h -> h.setFluid(FluidStack.parse(registries, outputTankTag).get()));

        CompoundTag energyTag = tag.getCompound(ModNBT.ENERGY_TAG);
        energyStorage.ifPresent(h -> h.deserializeNBT(registries, energyTag));
        super.loadAdditional(tag, registries);
    }



    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {

        tag.put(ModNBT.INPUT_INVENTORY_TAG, inventory.serializeNBT(registries));

        inputTank.ifPresent(h -> {
            CompoundTag tankTag = h.writeToNBT(registries, new CompoundTag());
            tag.put(ModNBT.INPUT_TANK_TAG, tankTag);
        });

        outputTank.ifPresent(h -> {
            CompoundTag tankTag = (CompoundTag)h.getFluid().save(registries);
            tag.put(ModNBT.OUTPUT_TANK_TAG, tankTag);
        });

        energyStorage.ifPresent(h -> {
            CompoundTag energyTag = (CompoundTag)h.serializeNBT(registries);
            tag.put(ModNBT.ENERGY_TAG, energyTag);
        });

        super.saveAdditional(tag, registries);
    }
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
        return energyStorage.map(e -> e.getEnergyStored() > 0).orElse(false);
    }

    @Override
    protected int useEnergy() {
        return energyStorage.map(e -> e.extractEnergy(FluidConvertorConfiguration.FLUID_CONV_ENERYG_PER_TICK.get(), false)).orElse(0);
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

        inventory.extractItem(INPUT_SLOT, finishedRecipe.getCatalystCount(), false);
        inputTank.ifPresent(f -> f.drain(finishedRecipe.getInputFluid().getAmount(),
                IFluidHandler.FluidAction.EXECUTE));

        outputTank.ifPresent(f -> f.internalFill(finishedRecipe.getOutput().copy(), IFluidHandler.FluidAction.EXECUTE));
        setChanged();
    }

    @Override
    protected boolean canStart() {

        if (energyStorage.map(f -> f.getEnergyStored() <= 0).orElse(true))
            return false;

        if (inputTank.map(f -> f.isEmpty()).orElse(true))
            return false;

        getRecipe();

        if (currRecipe == null)
            return false;

        // Only start if we have enough of the catalyst
        if (inventory.getStackInSlot(INPUT_SLOT).getCount() < currRecipe.getCatalystCount())
            return false;

        // Only start if we have enough input fluid
        FluidStack inFluid = inputTank.map(h -> h.getFluid()).orElse(FluidStack.EMPTY);
        if (inFluid.isEmpty())
            return false;

        if (inFluid.getAmount() < currRecipe.getInputFluid().getAmount())
            return false;

        // Only start if we can hold the output
        if (outputTank.map(h -> {
            int amount = currRecipe.getOutput().getAmount();
            int filled = h.internalFill(new FluidStack(currRecipe.outputFluid().getFluidHolder(), amount), IFluidHandler.FluidAction.SIMULATE);
            return amount == filled;
        }).orElse(false)) {
            // tank can hold the new output fluid
            return true;
        }

        return false;
    }

    @Override
    protected boolean hasValidInput() {

        if (currRecipe == null)
            getRecipe();

        if (currRecipe == null)
            return false;

        // Only valid if we have enough of the catalyst
        if (inventory.getStackInSlot(INPUT_SLOT).getCount() < currRecipe.getCatalystCount())
            return false;

        // Only valid if we have enough input fluid
        FluidStack inFluid = inputTank.map(h -> h.getFluid()).orElse(FluidStack.EMPTY);
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

        FluidStack inFluid = inputTank.map(h -> h.getFluid()).orElse(FluidStack.EMPTY);
        if (inFluid.isEmpty()) {
            return;
        }

        ItemStack catalyst = inventory.getStackInSlot(INPUT_SLOT);
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
        ItemStack itemStack = inventory.getStackInSlot(INPUT_SLOT);
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            inventory.insertItem(INPUT_SLOT, ItemStack.EMPTY, false);
        }
        super.dropContents(drops);
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FluidConvertorMenu(i, inventory, player, this);
    }

}
