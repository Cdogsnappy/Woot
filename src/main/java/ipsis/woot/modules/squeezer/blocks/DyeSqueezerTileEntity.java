package ipsis.woot.modules.squeezer.blocks;

import ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipe;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.squeezer.DyeMakeup;
import ipsis.woot.modules.squeezer.SqueezerConfiguration;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.WootEnergyStorage;
import ipsis.woot.util.WootFluidTank;
import ipsis.woot.util.WootMachineTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipe.DYE_SQUEEZER_TYPE;

public class DyeSqueezerTileEntity extends WootMachineTileEntity implements WootDebug, MenuProvider {

    private int red = 0;
    private int yellow = 0;
    private int blue = 0;
    private int white = 0;

    public DyeSqueezerTileEntity(BlockPos pos, BlockState state) {
        super(SqueezerSetup.SQUEEZER_BLOCK_TILE.get(), pos, state);
    }

    public final IItemHandler inventory = new ItemStackHandler(1)
    {
        @Override
        protected void onContentsChanged(int slot) {
            DyeSqueezerTileEntity.this.onContentsChanged(slot);
            setChanged();
        }

        public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
            return slot == 0 ? DyeSqueezerRecipe.isValidInput(stack) : false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!isItemValidForSlot(slot, stack))
                return stack;
            return super.insertItem(slot, stack, simulate);
        }
    };

    @Override
    public void onLoad() {
        for (Direction direction : Direction.values())
            settings.put(direction, Mode.OUTPUT);
    }

    //-------------------------------------------------------------------------
    //region Tanks
    private Optional<WootFluidTank> outputTank = Optional.of(this.createTank());
    private WootFluidTank createTank() {
        return new WootFluidTank(SqueezerConfiguration.DYE_SQUEEZER_TANK_CAPACITY.get(), h -> h.is(new FluidStack(FluidSetup.PUREDYE_FLUID.get(), 1).getFluidType())).setAccess(false, true);
    }
    public FluidStack getOutputTankFluid() { return outputTank.map(h -> h.getFluid()).orElse(FluidStack.EMPTY); }
    //endregion

    //-------------------------------------------------------------------------
    //region Energy
    private Optional<WootEnergyStorage> energyStorage = Optional.of(this.createEnergy());
    private WootEnergyStorage createEnergy() {
        return new WootEnergyStorage(SqueezerConfiguration.DYE_SQUEEZER_MAX_ENERGY.get());
    }

    public int getEnergy() { return energyStorage.map(EnergyStorage::getEnergyStored).orElse(0); }
    //endregion

    //-------------------------------------------------------------------------
    //region Inventory
    public static int INPUT_SLOT = 0;
    private final Optional<IItemHandler> inventoryGetter = Optional.of(() -> inventory);
    public IItemHandler getInventory() { return inventory; }
    //endregion

    //-------------------------------------------------------------------------
    //region NBT
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        readFromNBT(tag);
        super.loadAdditional(tag, registries);
    }



    private void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {

        if (tag.contains(ModNBT.INPUT_INVENTORY_TAG, Constants.NBT.TAG_LIST))
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(
                    inventory, null, tag.getList(ModNBT.INPUT_INVENTORY_TAG, Constants.NBT.TAG_COMPOUND));

        CompoundTag tankTag = tag.getCompound(ModNBT.OUTPUT_TANK_TAG);
        FluidStack fluidStack = FluidStack.parseOptional(registries, tankTag);
        outputTank.get().setFluid(fluidStack);

        CompoundTag energyTag = tag.getCompound(ModNBT.ENERGY_TAG);
        int cap = SqueezerConfiguration.DYE_SQUEEZER_MAX_ENERGY.get();
        energyStorage = Optional.of(new WootEnergyStorage(cap, cap, cap, energyTag.getInt("energy")));

        if (tag.contains(ModNBT.DyeSqueezer.INTERNAL_DYE_TANKS_TAG)) {
            CompoundTag dyeTag = tag.getCompound(ModNBT.DyeSqueezer.INTERNAL_DYE_TANKS_TAG);
            red = dyeTag.getInt(ModNBT.DyeSqueezer.RED_TAG);
            yellow = dyeTag.getInt(ModNBT.DyeSqueezer.YELLOW_TAG);
            blue = dyeTag.getInt(ModNBT.DyeSqueezer.BLUE_TAG);
            white = dyeTag.getInt(ModNBT.DyeSqueezer.WHITE_TAG);
        }

        if (tag.contains(ModNBT.DyeSqueezer.EXCESS_TAG)) {
            dumpExcess = tag.getBoolean(ModNBT.DyeSqueezer.EXCESS_TAG);
        }
    }



    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(ModNBT.INPUT_INVENTORY_TAG,
                Objects.requireNonNull(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null)));

        outputTank.ifPresent(h -> {
            CompoundTag tankTag = h.writeToNBT(new CompoundTag());
            tag.put(ModNBT.OUTPUT_TANK_TAG, tankTag);
        });

        energyStorage.ifPresent(h -> {
            CompoundTag energyTag = new CompoundTag();
            energyTag.putInt("energy", h.getEnergyStored());
            tag.put(ModNBT.ENERGY_TAG, energyTag);
        });

        CompoundTag dyeTag = new CompoundTag();
        dyeTag.putInt(ModNBT.DyeSqueezer.RED_TAG, red);
        dyeTag.putInt(ModNBT.DyeSqueezer.YELLOW_TAG, yellow);
        dyeTag.putInt(ModNBT.DyeSqueezer.BLUE_TAG, blue);
        dyeTag.putInt(ModNBT.DyeSqueezer.WHITE_TAG, white);
        tag.put(ModNBT.DyeSqueezer.INTERNAL_DYE_TANKS_TAG, dyeTag);
        tag.putBoolean(ModNBT.DyeSqueezer.EXCESS_TAG, dumpExcess);
        super.saveAdditional(tag, registries);
    }
    //endregion

    //-------------------------------------------------------------------------
    //region WootDebug
    @Override
    public List<String> getDebugText(List<String> debug, ItemUseContext itemUseContext) {
        debug.add("====> SqueezerTileEntity");
        debug.add("      r:" + red + " y:" + yellow + " b:" + blue + " w:" + white);
        outputTank.ifPresent(h -> debug.add("     p:" + h.getFluidAmount()));
        return debug;
    }
    //endregion

    //-------------------------------------------------------------------------
    //region Container
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.woot.squeezer.name");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new DyeSqueezerContainer(i, world, pos, playerInventory, playerEntity);
    }
    //endregion

    public int getRed() { return this.red; }
    public int getYellow() { return this.yellow; }
    public int getBlue() { return this.blue; }
    public int getWhite() { return this.white; }
    public boolean getDumpExcess() { return this.dumpExcess; }
    public int getProgress() { return calculateProgress(); }

    //-------------------------------------------------------------------------
    //region Machine Process
    private DyeSqueezerRecipe currRecipe = null;

    @Override
    public void tick() {
        super.tick();

        if (world.isRemote)
            return;

        if (world.getGameTime() % 20 == 0)
            generatePureFluid();

        if (outputTank.map(WootFluidTank::isEmpty).orElse(true))
            return;


        for (Direction direction : Direction.values()) {
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

    @Override
    protected boolean hasEnergy() {
        return energyStorage.map(e -> e.getEnergyStored() > 0).orElse(false);
    }

    @Override
    protected int useEnergy() {
        return energyStorage.map(e -> e.extractEnergy(SqueezerConfiguration.DYE_SQUEEZER_ENERGY_PER_TICK.get(), false)).orElse(0);
    }

    @Override
    protected int getRecipeEnergy() {
        return currRecipe != null ? currRecipe.getEnergy() : 0;
    }

    @Override
    protected void clearRecipe() {
        currRecipe = null;
    }

    private void generatePureFluid() {
        outputTank.ifPresent(f -> {
            while (canCreateOutput() && canStoreOutput()) {
                f.internalFill(new FluidStack(FluidSetup.PUREDYE_FLUID.get(), DyeMakeup.LCM * 4), IFluidHandler.FluidAction.EXECUTE);
                red -= DyeMakeup.LCM;
                yellow -= DyeMakeup.LCM;
                blue -= DyeMakeup.LCM;
                white -= DyeMakeup.LCM;
                markDirty();
            }
        });
    }

    @Override
    protected void processFinish() {
        if (currRecipe == null)
            getRecipe();
        if (currRecipe == null) {
            processOff();
            return;
        }

        DyeSqueezerRecipe finishedRecipe = currRecipe;

        red += finishedRecipe.getRed();
        yellow += finishedRecipe.getYellow();
        blue += finishedRecipe.getBlue();
        white += finishedRecipe.getWhite();

        red = MathHelper.clamp(red, 0, SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get());
        yellow = MathHelper.clamp(yellow, 0, SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get());
        blue = MathHelper.clamp(blue, 0, SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get());
        white = MathHelper.clamp(white, 0, SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get());

        inventory.extractItem(INPUT_SLOT, 1, false);
        generatePureFluid();
        markDirty();
    }

    @Override
    protected boolean canStart() {

        if (energyStorage.map(f -> f.getEnergyStored() <= 0).orElse(true))
            return false;

        if (inventory.getStackInSlot(INPUT_SLOT).isEmpty())
            return false;

        getRecipe();
        if (currRecipe == null)
            return false;

        if (!canStoreInternal(currRecipe))
            return false;

        return true;
    }

    @Override
    protected boolean hasValidInput() {
        if (currRecipe == null)
            getRecipe();

        if (currRecipe == null)
            return false;

        if (inventory.getStackInSlot(INPUT_SLOT).isEmpty())
            return false;

        // stack count is always 1
        return true;
    }

    @Override
    protected boolean isDisabled() {
        return false;
    }
    //endregion

    private void getRecipe() {
        currRecipe = world.getRecipeManager().getRecipe(DYE_SQUEEZER_TYPE,
                new Inventory(inventory.getStackInSlot(INPUT_SLOT)),
                world).orElse(null);
    }

    private boolean canStoreInternal(DyeSqueezerRecipe recipe) {

        boolean redHasSpace = recipe.getRed() + red <= SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get();
        boolean yellowHasSpace = recipe.getYellow() + yellow <= SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get();
        boolean blueHasSpace = recipe.getBlue() + blue <= SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get();
        boolean whiteHasSpace = recipe.getWhite() + white <= SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get();

        if (dumpExcess) {
            // Must be space for at least on recipe output
            return recipe.getRed() > 0 && redHasSpace ||
                   recipe.getYellow() > 0 && yellowHasSpace ||
                   recipe.getBlue() > 0 && blueHasSpace ||
                   recipe.getWhite() > 0 && whiteHasSpace;
        }

        return redHasSpace && yellowHasSpace && blueHasSpace && whiteHasSpace;
    }

    private boolean canCreateOutput() { return red >= DyeMakeup.LCM && yellow >= DyeMakeup.LCM && blue >= DyeMakeup.LCM && white >= DyeMakeup.LCM; }
    private boolean canStoreOutput() { return outputTank.map(h -> h.internalFill(new FluidStack(FluidSetup.PUREDYE_FLUID.get(), DyeMakeup.LCM * 4), IFluidHandler.FluidAction.SIMULATE ) == DyeMakeup.LCM * 4).orElse(false); }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryGetter.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return outputTank.cast();
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    public void dropContents(Level level, BlockPos pos) {

        List<ItemStack> drops = new ArrayList<>();
        ItemStack itemStack = inventory.getStackInSlot(INPUT_SLOT).copy();
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            inventory.insertItem(INPUT_SLOT, ItemStack.EMPTY, false);
        }
        super.dropContents(drops);
    }

    public TankPacket getOutputTankPacket() {
        return new TankPacket(0, outputTank.map(f -> f.getFluid()).orElse(FluidStack.EMPTY));
    }

    private boolean dumpExcess = false;
    public void toggleDumpExcess() {
        dumpExcess = !dumpExcess;
    }

    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        return List.of();
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }
}
