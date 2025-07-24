package ipsis.woot.modules.fluidconvertor.blocks;

import ipsis.woot.crafting.DyeSqueezerRecipe;
import ipsis.woot.crafting.FluidConvertorRecipe;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.fluidconvertor.FluidConvertorConfiguration;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.squeezer.blocks.DyeSqueezerTileEntity;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.WootEnergyStorage;
import ipsis.woot.util.WootFluidTank;
import ipsis.woot.util.WootMachineTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ipsis.woot.crafting.FluidConvertorRecipe.FLUID_CONV_TYPE;

public class FluidConvertorTileEntity extends WootMachineTileEntity implements WootDebug, MenuProvider {

    public FluidConvertorTileEntity() {
        super(FluidConvertorSetup.FLUID_CONVERTOR_BLOCK_TILE.get());
    }

    public final IItemHandler inventory = new ItemStackHandler(1)
    {
        @Override
        protected void onContentsChanged(int slot) {
            FluidConvertorTileEntity.this.onContentsChanged(slot);
            markDirty();
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
        Direction direction = world.getBlockState(getPos()).get(BlockStateProperties.HORIZONTAL_FACING);
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
    public void tick() {

        if (firstTick && world != null) {
            // Configure sides needs to access the block state so cannot do onLoad
            configureSides();
            firstTick = false;
        }

        super.tick();

        if (world.isRemote)
            return;

        if (outputTank.map(WootFluidTank::isEmpty).orElse(true))
            return;

        for (Direction direction : Direction.values()) {

            if (settings.get(direction) != Mode.OUTPUT)
                continue;

            TileEntity te = world.getTileEntity(getPos().offset(direction));
            if (!(te instanceof TileEntity))
                continue;

            LazyOptional<IFluidHandler> lazyOptional = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
            if (lazyOptional.isPresent()) {
                IFluidHandler iFluidHandler = lazyOptional.orElseThrow(NullPointerException::new);
                FluidStack fluidStack = outputTank.map(WootFluidTank::getFluid).orElse(FluidStack.EMPTY);
                if (!fluidStack.isEmpty()) {
                    int filled = iFluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    outputTank.ifPresent(f -> f.internalDrain(filled, IFluidHandler.FluidAction.EXECUTE));
                    markDirty();
                }
            }
        }
    }

    //-------------------------------------------------------------------------
    //region Tanks
    private Optional<FluidTank> inputTank = Optional.of(this::createInputTank);
    private Optional<WootFluidTank> outputTank = Optional.of(this::createOutputTank);
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
    private Optional<EnergyStorage> energyStorage = Optional.of(this::createEnergy);
    private EnergyStorage createEnergy() {
        return new EnergyStorage(FluidConvertorConfiguration.FLUID_CONV_MAX_ENERGY.get(), FluidConvertorConfiguration.FLUID_CONV_MAX_ENERGY_RX.get());
    }

    public int getEnergy() {
        return energyStorage.map(h -> h.getEnergyStored()).orElse(0);
    }
    public void setEnergy(int v) { energyStorage.ifPresent(h -> h.setEnergy(v)); }
    //endregion

    //-------------------------------------------------------------------------
    //region Inventory
    public static final int INPUT_SLOT = 0;
    private final LazyOptional<IItemHandler> inventoryGetter = LazyOptional.of(() -> inventory);
    public IItemHandler getInventory() { return inventory; }
    //endregion

    //-------------------------------------------------------------------------
    //region NBT
    @Override
    public void deserializeNBT(CompoundTag compoundNBT) {
        readFromNBT(compoundNBT);
        super.deserializeNBT(compoundNBT);
    }

    @Override
    public void read(BlockState blockState, CompoundTag compoundNBT) {
        readFromNBT(compoundNBT);
        super.read(blockState, compoundNBT);
    }

    private void readFromNBT(CompoundTag compoundNBT) {
        if (compoundNBT.contains(ModNBT.INPUT_INVENTORY_TAG, Constants.NBT.TAG_LIST))
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(
                    inventory, null, compoundNBT.getList(ModNBT.INPUT_INVENTORY_TAG, Constants.NBT.TAG_COMPOUND));

        CompoundTag inputTankTag = compoundNBT.getCompound(ModNBT.INPUT_TANK_TAG);
        inputTank.ifPresent(h -> h.readFromNBT(inputTankTag));

        CompoundTag outputTankTag = compoundNBT.getCompound(ModNBT.OUTPUT_TANK_TAG);
        outputTank.ifPresent(h -> h.readFromNBT(outputTankTag));

        CompoundTag energyTag = compoundNBT.getCompound(ModNBT.ENERGY_TAG);
        energyStorage.ifPresent(h -> ((INBTSerializable<Tag>)h).deserializeNBT(energyTag));
    }

    @Override
    public CompoundTag write(CompoundTag compoundNBT) {

        compoundNBT.put(ModNBT.INPUT_INVENTORY_TAG,
                Objects.requireNonNull(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null)));

        inputTank.ifPresent(h -> {
            CompoundTag tankTag = h.writeToNBT(new CompoundTag());
            compoundNBT.put(ModNBT.INPUT_TANK_TAG, tankTag);
        });

        outputTank.ifPresent(h -> {
            CompoundTag tankTag = h.writeToNBT(new CompoundTag());
            compoundNBT.put(ModNBT.OUTPUT_TANK_TAG, tankTag);
        });

        energyStorage.ifPresent(h -> {
            CompoundTag energyTag = ((INBTSerializable<CompoundTag>)h).serializeNBT();
            compoundNBT.put(ModNBT.ENERGY_TAG, energyTag);
        });

        return super.write(compoundNBT);
    }
    //endregion

    //-------------------------------------------------------------------------
    //region WootDebug
    @Override
    public List<String> getDebugText(List<String> debug, ItemUseContext itemUseContext) {
        debug.add("====> " + this.getClass().toString());
        debug.add("      Input Tank " + getInputTankFluid().getTranslationKey() + " " + getInputTankFluid().getAmount());
        debug.add("      Output Tank " + getOutputTankFluid().getTranslationKey() + " " + getOutputTankFluid().getAmount());
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
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.woot.fluidconvertor.name");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new FluidConvertorContainer(i, world, pos, playerInventory, playerEntity);
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

        outputTank.ifPresent(f -> f.internalFill(new FluidStack(finishedRecipe.getOutput(),
                finishedRecipe.getOutput().getAmount()), IFluidHandler.FluidAction.EXECUTE));
        markDirty();
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
            int filled = h.internalFill(new FluidStack(currRecipe.getOutput(), amount), IFluidHandler.FluidAction.SIMULATE);
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
            clearRecipe();
            return;
        }

        ItemStack catalyst = inventory.getStackInSlot(INPUT_SLOT);
        if (catalyst.isEmpty()) {
            clearRecipe();
            return;
        }

        // Get a list of recipes with matching catalyst
        List<FluidConvertorRecipe> recipes = world.getRecipeManager().getRecipes(
                FLUID_CONV_TYPE,
                new Inventory(inventory.getStackInSlot(INPUT_SLOT)), world);

        for (FluidConvertorRecipe recipe : recipes) {
            if (recipe.getInputFluid().isFluidEqual(inFluid)) {
                currRecipe = recipe;
                return;
            }
        }
    }

    @Nonnull
    @Override
    public <T> Optional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryGetter.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (side == null) {
                return inputTank.cast();
            } else if (settings.get(side) == Mode.INPUT) {
                return inputTank.cast();
            } else if (settings.get(side) == Mode.OUTPUT) {
                return outputTank.cast();
            } else {
                return inputTank.cast();
            }
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energyStorage.cast();
        }
        return super.getCapability(cap, side);
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
        return null;
    }
}
