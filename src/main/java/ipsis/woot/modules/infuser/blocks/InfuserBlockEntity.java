package ipsis.woot.modules.infuser.blocks;

import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.infuser.InfuserRecipe;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.infuser.InfuserConfiguration;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.util.EnchantingHelper;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.WootEnergyStorage;
import ipsis.woot.util.WootMachineBlockEntity;
import ipsis.woot.util.oss.OutputOnlyItemStackHandler;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class InfuserBlockEntity extends WootMachineBlockEntity implements WootDebug, MenuProvider {

    public InfuserBlockEntity(BlockPos pos, BlockState state) {
        super(InfuserSetup.INFUSER_BLOCK_TILE.get(), pos, state);
        inputSlots = new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                InfuserBlockEntity.this.onContentsChanged(slot);
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == INPUT_SLOT)
                    return InfuserRecipe.isValidInput(stack);
                else if (slot == AUGMENT_SLOT)
                    return InfuserRecipe.isValidAugment(stack);
                return false;
            }
        };
        outputSlot = new ItemStackHandler();
        outputWrappedSlot= new OutputOnlyItemStackHandler(outputSlot);
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
    }

    //-------------------------------------------------------------------------
    //region Tanks
    private Optional<FluidTank> inputTank =Optional.of(createTank());
    private FluidTank createTank() {
        return new FluidTank(InfuserConfiguration.INFUSER_TANK_CAPACITY.get(), h -> InfuserRecipe.isValidFluid(h));
    }

    public void setTankFluid(FluidStack fluidStack) {
        inputTank.ifPresent(h -> h.setFluid(fluidStack));
    }

    public FluidStack getTankFluid() {
        return inputTank.map(h -> h.getFluid()).orElse(FluidStack.EMPTY);
    }
    //endregion

    //-------------------------------------------------------------------------
    //region Energy
    private Optional<WootEnergyStorage> energyStorage = Optional.of(createEnergy());
    private WootEnergyStorage createEnergy() {
        return new WootEnergyStorage(InfuserConfiguration.INFUSER_MAX_ENERGY.get(), InfuserConfiguration.INFUSER_MAX_ENERGY_RX.get());
    }

    public int getEnergy() {
        return energyStorage.map(h -> h.getEnergyStored()).orElse(0);
    }
    public void setEnergy(int v) { energyStorage.ifPresent(h -> h.setEnergy(v)); }
    //endregion

    //-------------------------------------------------------------------------
    //region Inventory
    public static final int INPUT_SLOT = 0;
    public static final int AUGMENT_SLOT = 1;
    public static final int OUTPUT_SLOT = 0; // two different inventories, one for input, one for output
    private ItemStackHandler inputSlots;
    private ItemStackHandler outputSlot;
    private ItemStackHandler outputWrappedSlot;
    private final Optional<IItemHandler> inputSlotHandler = Optional.of(inputSlots);
    private final Optional<IItemHandler> outputWrappedSlotHandler = Optional.of(outputWrappedSlot);
    private final Optional<IItemHandler> allSlotHandler = Optional.of(new CombinedInvWrapper(inputSlots, outputSlot));
    //endregion

    //-------------------------------------------------------------------------
    //region NBT

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(ModNBT.INPUT_INVENTORY_TAG))
            inputSlots.deserializeNBT(registries, tag.getCompound(ModNBT.INPUT_INVENTORY_TAG));

        if (tag.contains(ModNBT.OUTPUT_INVENTORY_TAG))
            outputSlot.deserializeNBT(registries, tag.getCompound(ModNBT.OUTPUT_INVENTORY_TAG));

        CompoundTag tankTag = tag.getCompound(ModNBT.INPUT_TANK_TAG);
        inputTank.ifPresent(h -> h.readFromNBT(registries, tankTag));

        CompoundTag energyTag = tag.getCompound(ModNBT.ENERGY_TAG);
        energyStorage.ifPresent(h -> h.deserializeNBT(registries, energyTag));
        super.loadAdditional(tag, registries);
    }

    @Override
    public void saveAdditional(CompoundTag compoundNBT, HolderLookup.Provider registries) {
        if(inputSlots != null) {
            compoundNBT.put(ModNBT.INPUT_INVENTORY_TAG, inputSlots.serializeNBT(registries));
        }
        if(outputSlot != null) {
            compoundNBT.put(ModNBT.OUTPUT_INVENTORY_TAG,
                    outputSlot.serializeNBT(registries));
        }

        inputTank.ifPresent(h -> {
            CompoundTag tankTag = h.writeToNBT(registries, new CompoundTag());
            compoundNBT.put(ModNBT.INPUT_TANK_TAG, tankTag);
        });

        energyStorage.ifPresent(h -> {
            Tag energyTag = h.serializeNBT(registries);
            compoundNBT.put(ModNBT.ENERGY_TAG, energyTag);
        });

        super.saveAdditional(compoundNBT, registries);
    }
    //endregion

    //-------------------------------------------------------------------------
    //region IWootDebug
    //endregion

    //-------------------------------------------------------------------------
    //region Container
    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.woot.infuser.name");
    }


    //endregion


    //-------------------------------------------------------------------------
    //region Client sync
    public int getProgress() {
        return calculateProgress();
    }
    //endregion

    //-------------------------------------------------------------------------
    //region Machine Process
    private InfuserRecipe currRecipe = null;

    @Override
    protected boolean hasEnergy() {
        return energyStorage.map(e -> e.getEnergyStored() > 0).orElse(false);
    }

    @Override
    protected int useEnergy() {
        return energyStorage.map(e -> e.extractEnergy(InfuserConfiguration.INFUSER_ENERGY_PER_TICK.get(), false)).orElse(0);
    }

    @Override
    protected void clearRecipe() {
        currRecipe = null;
    }

    @Override
    protected int getRecipeEnergy() {
        return currRecipe != null ? currRecipe.getEnergy() : 0;
    }

    @Override
    protected void processFinish() {
        if (currRecipe == null)
            getRecipe();
        if (currRecipe == null) {
            processOff();
            return;
        }

        InfuserRecipe finishedRecipe = currRecipe;
        final int inputSize = finishedRecipe.getIngredient().getItems()[0].getCount();
        final int augmentSize = finishedRecipe.hasAugment() ? finishedRecipe.getAugment().getItems()[0].getCount() : 1;

        inputSlots.extractItem(INPUT_SLOT, inputSize, false);
        if (finishedRecipe.hasAugment())
            inputSlots.extractItem(AUGMENT_SLOT, augmentSize, false);

        ItemStack itemStack = finishedRecipe.getOutput();
        if (itemStack.getItem() == Items.ENCHANTED_BOOK) {
            // stack size determines the enchant level, so save it off and reset to single item generated
            int level = itemStack.getCount();
            itemStack = new ItemStack(Items.BOOK, 1);
            itemStack = EnchantingHelper.addRandomBookEnchant(itemStack, level, getLevel().registryAccess());
        }

        outputSlot.insertItem(OUTPUT_SLOT, itemStack, false);
        inputTank.ifPresent(f -> f.drain(finishedRecipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE));
        setChanged();
    }

    @Override
    protected boolean canStart() {

        if (energyStorage.map(f -> f.getEnergyStored() <= 0).orElse(true))
            return false;

        if (inputSlots.getStackInSlot(INPUT_SLOT).isEmpty())
            return false;

        if (inputTank.map(f -> f.isEmpty()).orElse(true))
            return false;

        getRecipe();
        if (currRecipe == null)
            return false;

        if (currRecipe.hasAugment() && currRecipe.getAugmentCount() > inputSlots.getStackInSlot(AUGMENT_SLOT).getCount())
            return false;

        FluidStack fluidStack = inputTank.map(h ->h.getFluid()).orElse(FluidStack.EMPTY);
        if (!fluidStack.is(currRecipe.getFluidInput().getFluidType()))
            return false;

        if (outputSlot.getStackInSlot(OUTPUT_SLOT).isEmpty())
            return true;

        ItemStack outStack = outputSlot.getStackInSlot(OUTPUT_SLOT);
        if (outStack.getCount() == outStack.getMaxStackSize())
            return false;

        if (ItemStack.isSameItemSameComponents(currRecipe.getOutput(), outputSlot.getStackInSlot(OUTPUT_SLOT)))
            return true;

        return false;
    }

    @Override
    protected boolean hasValidInput() {

        if (currRecipe == null)
            getRecipe();

        if (currRecipe == null)
            return false;

        if (inputSlots.getStackInSlot(INPUT_SLOT).isEmpty())
            return false;

        if (currRecipe.hasAugment() && currRecipe.getAugmentCount() > inputSlots.getStackInSlot(AUGMENT_SLOT).getCount())
            return false;

        FluidStack fluidStack = inputTank.map(h ->h.getFluid()).orElse(FluidStack.EMPTY);
        if (!fluidStack.is(currRecipe.getFluidInput().getFluidType()))
            return false;

        return true;
    }

    @Override
    protected boolean isDisabled() {
        return false;
    }
    //endregion

    private void getRecipe() {
        if (inputTank.map(h -> h.isEmpty()).orElse(false)) {
            clearRecipe();
            return;
        }

        List<RecipeHolder<InfuserRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(
                WootRecipes.INFUSER_TYPE.get());

        if (!recipes.isEmpty()) {
            // Already checked for empty so this should always be !empty
            FluidStack fluidStack = inputTank.map(h ->h.getFluid()).orElse(FluidStack.EMPTY);
            for (RecipeHolder<InfuserRecipe> r : recipes) {
                if (r.value().getFluidInput().is(fluidStack.getFluid())) {
                        currRecipe = r.value();
                        return;
                }
            }
        }

        clearRecipe();
    }

    public void dropContents(Level level, BlockPos pos) {

        List<ItemStack> drops = new ArrayList<>();
        ItemStack itemStack = inputSlots.getStackInSlot(INPUT_SLOT);
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            inputSlots.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        }

        itemStack = inputSlots.getStackInSlot(AUGMENT_SLOT);
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            inputSlots.setStackInSlot(AUGMENT_SLOT, ItemStack.EMPTY);
        }

        itemStack = outputSlot.getStackInSlot(OUTPUT_SLOT);
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            outputSlot.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
        }
        super.dropContents(drops);
    }

    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> InfuserTileEntity");
        debug.add("      Tank " + getTankFluid().getDescriptionId() + " " + getTankFluid().getAmount());
        debug.add("      Energy " + getEnergy());
        return debug;
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new InfuserMenu(i, inventory, this);
    }
}
