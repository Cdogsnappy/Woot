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
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
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
        stackInputHandler = new ItemStackHandler(2) {
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
        stackOutputHandler = new ItemStackHandler();
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

    public void setTankFluid(FluidStack fluidStack) {
        fluidInputHandler.setFluid(fluidStack);
    }

    public FluidStack getTankFluid() {
        return fluidInputHandler.getFluid();
    }
    //endregion

    //-------------------------------------------------------------------------
    //region Energy

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }
    //endregion

    //-------------------------------------------------------------------------
    //region Inventory
    public static final int INPUT_SLOT = 0;
    public static final int AUGMENT_SLOT = 1;
    public static final int OUTPUT_SLOT = 0; // two different inventories, one for input, one for output
    //endregion

    //-------------------------------------------------------------------------
    //region NBT

    
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
        return energyStorage.getEnergyStored() > 0;
    }

    @Override
    protected int useEnergy() {
        return energyStorage.extractEnergy(InfuserConfiguration.INFUSER_ENERGY_PER_TICK.get(), false);
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

        stackInputHandler.extractItem(INPUT_SLOT, inputSize, false);
        if (finishedRecipe.hasAugment())
            stackInputHandler.extractItem(AUGMENT_SLOT, augmentSize, false);

        ItemStack itemStack = finishedRecipe.getOutput();
        if (itemStack.getItem() == Items.ENCHANTED_BOOK) {
            // stack size determines the enchant level, so save it off and reset to single item generated
            int level = itemStack.getCount();
            itemStack = new ItemStack(Items.BOOK, 1);
            itemStack = EnchantingHelper.addRandomBookEnchant(itemStack, level, getLevel().registryAccess());
        }

        stackOutputHandler.insertItem(OUTPUT_SLOT, itemStack.copy(), false);
        fluidInputHandler.drain(finishedRecipe.getFluidInput().getAmount(), IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    @Override
    protected boolean canStart() {

        if (energyStorage.getEnergyStored() <= 0)
            return false;

        if (stackInputHandler.getStackInSlot(INPUT_SLOT).isEmpty())
            return false;

        if (fluidInputHandler.isEmpty())
            return false;

        getRecipe();
        if (currRecipe == null)
            return false;

        if (currRecipe.hasAugment() && currRecipe.getAugmentCount() > stackInputHandler.getStackInSlot(AUGMENT_SLOT).getCount())
            return false;

        FluidStack fluidStack = fluidInputHandler.getFluid();
        if (!fluidStack.is(currRecipe.getFluidInput().getFluidType()))
            return false;

        if (stackOutputHandler.getStackInSlot(OUTPUT_SLOT).isEmpty())
            return true;

        ItemStack outStack = stackOutputHandler.getStackInSlot(OUTPUT_SLOT);
        if (outStack.getCount() == outStack.getMaxStackSize())
            return false;

        if (ItemStack.isSameItemSameComponents(currRecipe.getOutput(), stackOutputHandler.getStackInSlot(OUTPUT_SLOT)))
            return true;

        return false;
    }

    @Override
    protected boolean hasValidInput() {

        if (currRecipe == null)
            getRecipe();

        if (currRecipe == null)
            return false;

        if (stackInputHandler.getStackInSlot(INPUT_SLOT).isEmpty())
            return false;

        if (currRecipe.hasAugment() && currRecipe.getAugmentCount() > stackInputHandler.getStackInSlot(AUGMENT_SLOT).getCount())
            return false;

        FluidStack fluidStack = fluidInputHandler.getFluid();
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
        if (fluidInputHandler.isEmpty()) {
            clearRecipe();
            return;
        }

        List<RecipeHolder<InfuserRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(
                WootRecipes.INFUSER_TYPE.get());

        if (!recipes.isEmpty()) {
            // Already checked for empty so this should always be !empty
            FluidStack fluidStack = fluidInputHandler.getFluid();
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
        ItemStack itemStack = stackInputHandler.getStackInSlot(INPUT_SLOT);
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            stackInputHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        }

        itemStack = stackInputHandler.getStackInSlot(AUGMENT_SLOT);
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            stackInputHandler.setStackInSlot(AUGMENT_SLOT, ItemStack.EMPTY);
        }

        itemStack = stackOutputHandler.getStackInSlot(OUTPUT_SLOT);
        if (!itemStack.isEmpty()) {
            drops.add(itemStack);
            stackOutputHandler.setStackInSlot(OUTPUT_SLOT, ItemStack.EMPTY);
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
    
    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                InfuserSetup.INFUSER_BLOCK_TILE.get(),
                (be, direction) -> be.energyStorage);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                InfuserSetup.INFUSER_BLOCK_TILE.get(),
                (be, direction) -> {
            if(direction == be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise() || direction == Direction.DOWN){
                return be.fluidOutputHandler;
            }
            return be.fluidInputHandler;
                });

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                InfuserSetup.INFUSER_BLOCK_TILE.get(),
                (be, direction) -> {
                    if (direction == be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise() || direction == Direction.DOWN) {
                        return be.stackOutputHandler;
                    }
                    return be.stackInputHandler;
                });
    }



}
