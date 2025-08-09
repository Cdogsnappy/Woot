package ipsis.woot.modules.squeezer.blocks;

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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
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
    }

    public final ItemStackHandler inventory = new ItemStackHandler(1)
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
    private Optional<WootFluidTank> outputTank = Optional.of(createTank());
    private WootFluidTank createTank() {
        return new WootFluidTank(SqueezerConfiguration.ENCH_SQUEEZER_TANK_CAPACITY.get(), h -> h.is(FluidSetup.ENCHANT_FLUID.get())).setAccess(false, true);
    }
    public FluidStack getOutputTankFluid() { return outputTank.map(h -> h.getFluid()).orElse(FluidStack.EMPTY); }
    //endregion

    //-------------------------------------------------------------------------
    //region Energy
    private Optional<WootEnergyStorage> energyStorage = Optional.of(createEnergy());
    private WootEnergyStorage createEnergy() {
        return new WootEnergyStorage(SqueezerConfiguration.ENCH_SQUEEZER_MAX_ENERGY.get(), SqueezerConfiguration.ENCH_SQUEEZER_MAX_ENERGY_RX.get());
    }

    public int getEnergy() { return energyStorage.map(h -> h.getEnergyStored()).orElse(0); }
    public void setEnergy(int v) { energyStorage.ifPresent(h -> h.setEnergy(v)); }
    //endregion

    //-------------------------------------------------------------------------
    //region Inventory
    public static int INPUT_SLOT = 0;
    private final Optional<IItemHandler> inventoryGetter = Optional.of(inventory);
    public IItemHandler getInventory() { return inventory; }
    //endregion

    //-------------------------------------------------------------------------


    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        readfromNBT(tag, registries);
        super.loadAdditional(tag, registries);
    }

    public void readfromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains(ModNBT.INPUT_INVENTORY_TAG)){
            inventory.deserializeNBT(registries, tag.getCompound(ModNBT.INPUT_INVENTORY_TAG));
        }

        CompoundTag tankTag = tag.getCompound(ModNBT.OUTPUT_TANK_TAG);
        outputTank.ifPresent(h -> h.setFluid(FluidStack.parse(registries, tankTag).get()));

        CompoundTag energyTag = tag.getCompound(ModNBT.ENERGY_TAG);
        energyStorage.ifPresent(h -> h.deserializeNBT(registries, energyTag));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(ModNBT.INPUT_INVENTORY_TAG, inventory.serializeNBT(registries));

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
        return energyStorage.map(e -> e.getEnergyStored() > 0).orElse(false);
    }

    @Override
    protected int useEnergy() {
        return energyStorage.map(e -> e.extractEnergy(SqueezerConfiguration.ENCH_SQUEEZER_ENERGY_PER_TICK.get(), false)).orElse(0);
    }

    @Override
    protected int getRecipeEnergy() {
        ItemStack itemStack = inventory.getStackInSlot(INPUT_SLOT);
        if (itemStack.isEmpty())
            return 0;

        return getEnchantEnergy(itemStack);
    }

    @Override
    protected void clearRecipe() { }

    @Override
    protected void processFinish() {
        ItemStack itemStack = inventory.getStackInSlot(INPUT_SLOT);
        if (itemStack.isEmpty())
            return;

        inventory.extractItem(INPUT_SLOT, 1, false);

        int amount = getEnchantAmount(itemStack);
        outputTank.ifPresent(h -> {
            h.internalFill(new FluidStack(FluidSetup.ENCHANT_FLUID.get(), amount), IFluidHandler.FluidAction.EXECUTE);
        });

        setChanged();
    }

    @Override
    protected boolean canStart() {

        if (energyStorage.map(f -> f.getEnergyStored() <= 0).orElse((true)))
            return false;

        ItemStack itemStack = inventory.getStackInSlot(INPUT_SLOT);
        if (itemStack.isEmpty())
            return false;

        if (!EnchantmentHelper.isEnchanted(itemStack))
            return false;

        // Only start if we can hold the output
        if (outputTank.map(h -> {
            int amount = getEnchantAmount(itemStack);
            int filled = h.internalFill(new FluidStack(FluidSetup.ENCHANT_FLUID.get(), amount), IFluidHandler.FluidAction.SIMULATE);
            return amount == filled;
        }).orElse(false)) {
            // tank can hold the new output fluid
            return true;
        }

        return false;
    }

    @Override
    protected boolean hasValidInput() {
        ItemStack itemStack = inventory.getStackInSlot(INPUT_SLOT);
        if (itemStack.isEmpty() || !EnchantmentHelper.isEnchanted(itemStack))
                return false;

        return true;
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
        if (!itemStack.isEmpty() && EnchantmentHelper.isEnchanted(itemStack)) {
            ItemEnchantments enchants = itemStack.getTagEnchantments();
            enchants.keySet().forEach((e) -> {
                amount.addAndGet(SqueezerConfiguration.getEnchantFluidAmount(enchants.getLevel(e)));
            });
        }
        return capEnchantAmount(amount.get());
    }

    private int getEnchantEnergy(ItemStack itemStack) {
        AtomicInteger amount = new AtomicInteger();
        if (!itemStack.isEmpty() && EnchantmentHelper.isEnchanted(itemStack)) {
            ItemEnchantments enchants = itemStack.getTagEnchantments();
            enchants.keySet().forEach((e) -> {
                amount.addAndGet(SqueezerConfiguration.getEnchantEnergy(enchants.getLevel(e)));
            });
        }
        return amount.get();
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
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> EnchantSqueezerTileEntity");
        outputTank.ifPresent(h -> {
            debug.add("     p:" + h.getFluidAmount());
        });
        debug.add("      Settings " + settings);
        return debug;
    }

    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new EnchantSqueezerMenu(i, inventory, this);
    }
}
