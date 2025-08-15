package ipsis.woot.modules.squeezer.blocks;

import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.squeezer.SqueezerConfiguration;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.WootEnergyStorage;
import ipsis.woot.util.WootFluidTank;
import ipsis.woot.util.WootMachineBlockEntity;
import ipsis.woot.util.helper.EnchantmentHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class EnchantSqueezerBlockEntity extends WootMachineBlockEntity implements WootDebug, MenuProvider {

    public EnchantSqueezerBlockEntity(BlockPos pos, BlockState blockState) {
        super(SqueezerSetup.ENCHANT_SQUEEZER_BLOCK_TILE.get(), pos, blockState);
        stackInputHandler = new ItemStackHandler(1)
        {
            @Override
            protected void onContentsChanged(int slot) {
                EnchantSqueezerBlockEntity.this.onContentsChanged(slot);
                setChanged();
            }

            public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
                return EnchantmentHelper.isEnchanted(stack);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValidForSlot(slot, stack))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
        fluidOutputHandler = new FluidTank(10000);
        
    }

    

    @Override
    public void onLoad() {
        for (Direction direction : Direction.values())
            settings.put(direction, Mode.OUTPUT);
    }

    @Override
    public void tick(Level level) {
        super.tick(level);

        if (level.isClientSide)
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
    //region Tanks


    public FluidStack getOutputTankFluid() { return fluidOutputHandler.getFluid(); }
    //endregion

    //-------------------------------------------------------------------------
    //region Energy


    public int getEnergy() { return energyStorage.getEnergyStored(); }
    //endregion

    //-------------------------------------------------------------------------
    //region Inventory
    public static int INPUT_SLOT = 0;
    public IItemHandler getInventory() { return stackInputHandler; }
    //endregion

    //-------------------------------------------------------------------------



    //endregion

    //-------------------------------------------------------------------------
    //region WootDebug
    
    //endregion

    //-------------------------------------------------------------------------
    //region Container
    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.woot.enchsqueezer.name");
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

    @Override
    protected boolean hasEnergy() {
        return energyStorage.getEnergyStored() > 0;
    }

    @Override
    protected int useEnergy() {
        return energyStorage.extractEnergy(SqueezerConfiguration.ENCH_SQUEEZER_ENERGY_PER_TICK.get(), false);
    }

    @Override
    protected int getRecipeEnergy() {
        ItemStack itemStack = stackInputHandler.getStackInSlot(INPUT_SLOT);
        if (itemStack.isEmpty())
            return 0;

        return getEnchantEnergy(itemStack);
    }

    @Override
    protected void clearRecipe() { }

    @Override
    protected void processFinish() {
        ItemStack itemStack = stackInputHandler.getStackInSlot(INPUT_SLOT);
        if (itemStack.isEmpty())
            return;

        stackInputHandler.extractItem(INPUT_SLOT, 1, false);

        int amount = getEnchantAmount(itemStack);
        fluidOutputHandler.fill(new FluidStack(FluidSetup.ENCHANT_FLUID.get(), amount), IFluidHandler.FluidAction.EXECUTE);

        setChanged();
    }

    @Override
    protected boolean canStart() {

        if (energyStorage.getEnergyStored() <= 0)
            return false;

        ItemStack itemStack = stackInputHandler.getStackInSlot(INPUT_SLOT);
        if (itemStack.isEmpty())
            return false;

        if (!EnchantmentHelper.isEnchanted(itemStack) && !itemStack.is(Items.ENCHANTED_BOOK))
            return false;

        // Only start if we can hold the output

        int amount = getEnchantAmount(itemStack);
        int filled = fluidOutputHandler.fill(new FluidStack(FluidSetup.ENCHANT_FLUID.get(), amount), IFluidHandler.FluidAction.SIMULATE);
        return amount == filled;

    }

    @Override
    protected boolean hasValidInput() {
        return level.getRecipeManager().getRecipeFor(WootRecipes.ENCHANT_SQUEEZER_TYPE.get(),
                new SingleRecipeInput(stackInputHandler.getStackInSlot(INPUT_SLOT)), level).isPresent();

    }

    @Override
    protected boolean isDisabled() {
        return false;
    }
    //endregion



    private int capEnchantAmount(int amount) {

        int max = SqueezerConfiguration.getEnchantFluidAmount(5);
        max *= 4;
        max = Math.clamp(max, 0, SqueezerConfiguration.ENCH_SQUEEZER_TANK_CAPACITY.get());
        return Math.clamp(amount, 0, max);
    }

    private int getEnchantAmount(ItemStack itemStack) {
        AtomicInteger amount = new AtomicInteger();
        if (!itemStack.isEmpty() && (EnchantmentHelper.isEnchanted(itemStack) || itemStack.is(Items.ENCHANTED_BOOK))) {
            ItemEnchantments data = itemStack.get(DataComponents.STORED_ENCHANTMENTS);
            data.keySet().forEach((e) -> {
                amount.addAndGet(SqueezerConfiguration.getEnchantEnergy(data.getLevel(e)));
            });
        }
        return capEnchantAmount(amount.get());
    }

    private int getEnchantEnergy(ItemStack itemStack) {
        AtomicInteger amount = new AtomicInteger();
        if (!itemStack.isEmpty() && (EnchantmentHelper.isEnchanted(itemStack) || itemStack.is(Items.ENCHANTED_BOOK))) {
            ItemEnchantments data = itemStack.get(DataComponents.STORED_ENCHANTMENTS);
            data.keySet().forEach((e) -> {
                amount.addAndGet(SqueezerConfiguration.getEnchantEnergy(data.getLevel(e)));
            });
        }
        return amount.get();
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
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> EnchantSqueezerTileEntity");

        debug.add("     p:" + fluidOutputHandler.getFluidAmount());
        debug.add("      Settings " + settings);
        return debug;
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory stackInputHandler, Player player) {
        return new EnchantSqueezerMenu(i, stackInputHandler, this);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SqueezerSetup.ENCHANT_SQUEEZER_BLOCK_TILE.get(),
                (be, side) -> be.energyStorage);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                SqueezerSetup.ENCHANT_SQUEEZER_BLOCK_TILE.get(),
                (be, side) -> be.stackInputHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                SqueezerSetup.ENCHANT_SQUEEZER_BLOCK_TILE.get(),
                (be, side) -> be.fluidOutputHandler);
    }
}
