package ipsis.woot.modules.squeezer.blocks;

import ipsis.woot.crafting.WootRecipes;
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
import ipsis.woot.util.WootMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class DyeSqueezerBlockEntity extends WootMachineBlockEntity implements WootDebug, MenuProvider {

    private int red = 0;
    private int yellow = 0;
    private int blue = 0;
    private int white = 0;



    public DyeSqueezerBlockEntity(BlockPos pos, BlockState state) {
        super(SqueezerSetup.SQUEEZER_BLOCK_TILE.get(), pos, state);
        this.stackInputHandler = new ItemStackHandler(1)
        {
            @Override
            protected void onContentsChanged(int slot) {
            DyeSqueezerBlockEntity.this.onContentsChanged(slot);
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
    }

    @Override
    public void onLoad() {
        for (Direction direction : Direction.values())
            settings.put(direction, Mode.OUTPUT);
    }

    //-------------------------------------------------------------------------
    //region Tanks
    private WootFluidTank outputTank = this.createTank();
    private WootFluidTank createTank() {
        return new WootFluidTank(SqueezerConfiguration.DYE_SQUEEZER_TANK_CAPACITY.get(), h -> h.is(new FluidStack(FluidSetup.PUREDYE_FLUID.get(), 1).getFluidType())).setAccess(false, true);
    }
    public FluidStack getOutputTankFluid() { return outputTank.getFluid(); }
    //endregion

    //-------------------------------------------------------------------------
    //region Energy



    public int getEnergy() { return energyStorage.getEnergyStored(); }
    //endregion

    //-------------------------------------------------------------------------
    //region Inventory
    //endregion

    //-------------------------------------------------------------------------
    //region NBT
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        readFromNBT(tag, registries);
        super.loadAdditional(tag, registries);
    }



    private void readFromNBT(CompoundTag tag, HolderLookup.Provider registries) {


        CompoundTag tankTag = tag.getCompound(ModNBT.OUTPUT_TANK_TAG);
        FluidStack fluidStack = FluidStack.parseOptional(registries, tankTag);
        outputTank.setFluid(fluidStack);

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

        if(!outputTank.getFluid().isEmpty()) {
            Tag tankTag = outputTank.getFluid().save(registries);
            tag.put(ModNBT.OUTPUT_TANK_TAG, tankTag);
        }




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

    //endregion

    //-------------------------------------------------------------------------
    //region Container
    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.woot.squeezer.name");
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
    public void tick(Level level) {
        super.tick(level);

        if (level.isClientSide)
            return;

        if (level.getGameTime() % 20 == 0)
            generatePureFluid();

        if (outputTank.isEmpty())
            return;


        for (Direction direction : Direction.values()) {
            BlockEntity te = level.getBlockEntity(getBlockPos().offset(direction.getNormal()));
            if (te == null)
                continue;

            Optional<IFluidHandler> lazyOptional = Optional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK,te.getBlockPos(), direction.getOpposite()));
            if (lazyOptional.isPresent()) {
                IFluidHandler iFluidHandler = lazyOptional.orElseThrow(NullPointerException::new);
                FluidStack fluidStack = outputTank.getFluid();
                if (!fluidStack.isEmpty()) {
                    int filled = iFluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                    outputTank.internalDrain(filled, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                }
            }
        }
    }

    @Override
    protected boolean hasEnergy() {
        return energyStorage.getEnergyStored() > 0;
    }

    @Override
    protected int useEnergy() {
        return energyStorage.extractEnergy(SqueezerConfiguration.DYE_SQUEEZER_ENERGY_PER_TICK.get(), false);
    }

    @Override
    protected int getRecipeEnergy() {
        return currRecipe != null ? currRecipe.energy() : 0;
    }

    @Override
    protected void clearRecipe() {
        currRecipe = null;
    }

    private void generatePureFluid() {
            while (canCreateOutput() && canStoreOutput()) {
                outputTank.internalFill(new FluidStack(FluidSetup.PUREDYE_FLUID.get(), DyeMakeup.LCM * 4), IFluidHandler.FluidAction.EXECUTE);
                red -= DyeMakeup.LCM;
                yellow -= DyeMakeup.LCM;
                blue -= DyeMakeup.LCM;
                white -= DyeMakeup.LCM;
                setChanged();
            }
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

        red = Math.clamp(red, 0, SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get());
        yellow = Math.clamp(yellow, 0, SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get());
        blue = Math.clamp(blue, 0, SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get());
        white = Math.clamp(white, 0, SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get());

        stackInputHandler.extractItem(0, 1, false);
        generatePureFluid();
        setChanged();
    }

    @Override
    protected boolean canStart() {

        if (energyStorage.getEnergyStored() <= 0)
            return false;

        if (stackInputHandler.getStackInSlot(0).isEmpty())
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

        if (stackInputHandler.getStackInSlot(0).isEmpty())
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
        currRecipe = level.getRecipeManager().getRecipeFor(WootRecipes.DYE_SQUEEZER_TYPE.get(),
                new SingleRecipeInput(stackInputHandler.getStackInSlot(0)),
                level).get().value();
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
    private boolean canStoreOutput() { return outputTank.internalFill(new FluidStack(FluidSetup.PUREDYE_FLUID.get(), DyeMakeup.LCM * 4), IFluidHandler.FluidAction.SIMULATE ) == DyeMakeup.LCM * 4; }



    public void dropContents(Level level, BlockPos pos) {

        List<ItemStack> drops = new ArrayList<>();
        ItemStack itemStack = stackInputHandler.getStackInSlot(0).copy();
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            stackInputHandler.insertItem(0, ItemStack.EMPTY, false);
        }
        super.dropContents(drops);
    }

    public TankPacket getOutputTankPacket() {
        return new TankPacket(outputTank.getFluid(), 0);
    }

    private boolean dumpExcess = false;
    public void toggleDumpExcess() {
        dumpExcess = !dumpExcess;
    }

    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> SqueezerTileEntity");
        debug.add("      r:" + red + " y:" + yellow + " b:" + blue + " w:" + white);
        return debug;
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new DyeSqueezerContainer(i, inventory, this);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SqueezerSetup.SQUEEZER_BLOCK_TILE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                SqueezerSetup.SQUEEZER_BLOCK_TILE.get(),
                (be, side) -> be.stackInputHandler);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                SqueezerSetup.SQUEEZER_BLOCK_TILE.get(),
                (be, side) -> be.fluidOutputHandler);
    }
}
