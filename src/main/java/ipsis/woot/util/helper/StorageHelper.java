package ipsis.woot.util.helper;

import ipsis.woot.Woot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class StorageHelper {

    public static void insertItems(List<ItemStack> items, List<Optional<IItemHandler>> hdlrs) {

        if (items.isEmpty() || hdlrs.isEmpty())
            return;

        for (Optional<IItemHandler> hdlr : hdlrs) {
            hdlr.ifPresent(h -> {
                for (int i = 0; i < items.size(); i++) {
                    ItemStack itemStack = items.get(i);
                    if (itemStack.isEmpty())
                        continue;

                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(h, itemStack.copy(), false);
                    items.set(i, remainder);
                }
            });
        }
    }

    public static void insertFluids(List<FluidStack> fluids, List<Optional<IFluidHandler>> hdlrs) {

        if (fluids.isEmpty() || hdlrs.isEmpty())
            return;

        // Try to fill non-empty tanks
        for (Optional<IFluidHandler> hdlr : hdlrs) {
            hdlr.ifPresent(h -> {
                int tanks = h.getTanks();
                if (tanks > 0) {
                    for (int tank = 0; tank < tanks; tank++) {
                        FluidStack fluidStack = h.getFluidInTank(tank);
                        if (fluidStack != null && !fluidStack.isEmpty()) {
                            for (FluidStack f : fluids) {
                                if (!f.isEmpty() && fluidStack.is(f.getFluid())) {
                                    int filled = h.fill(f, IFluidHandler.FluidAction.EXECUTE);
                                    Woot.setup.getLogger().debug("insertFluids: {} ----> topup {} {}", tank, f.getDescriptionId(), filled);
                                    if (filled > 0)
                                        f.setAmount(f.getAmount() - filled);
                                }
                            }
                        }
                    }
                }
            });
        }

        boolean haveFluids = false;
        for (FluidStack f : fluids) {
            if (!f.isEmpty())
                haveFluids = true;
        }

        if (haveFluids) {
            // Try to fill empty tanks
            for (Optional<IFluidHandler> hdlr : hdlrs) {
                hdlr.ifPresent(h -> {
                    int tanks = h.getTanks();
                    if (tanks > 0) {
                        for (int tank = 0; tank < tanks; tank++) {
                            FluidStack fluidStack = h.getFluidInTank(tank);
                            if (fluidStack.isEmpty()) {
                                for (FluidStack f : fluids) {
                                    if (!f.isEmpty()) {
                                        int filled = h.fill(f, IFluidHandler.FluidAction.EXECUTE);
                                        Woot.setup.getLogger().debug("insertFluids: {} ----> new fill {} {}", tank, f.getDescriptionId(), filled);
                                        if (filled > 0)
                                            f.setAmount(f.getAmount() - filled);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * Flattens the list and returns a list of unique items
     * The item counts may break the max stack size
     */
    public static List<ItemStack> flattenItemStackList(List<ItemStack> items) {
        List<ItemStack> flattened = new ArrayList<>();

        for (ItemStack itemStack : items) {
            for (ItemStack flatStack : flattened) {
                if (itemStack.isEmpty())
                    break;

                if (ItemStack.isSameItemSameComponents(flatStack, itemStack)) {
                    flatStack.grow(itemStack.getCount());
                    itemStack.setCount(0);
                }
            }

            if (!itemStack.isEmpty())
                flattened.add(itemStack.copy());
        }

        return flattened;
    }

    public static int getCount(ItemStack itemStack, List<Optional<IItemHandler>> hdlrs) {

        AtomicInteger count = new AtomicInteger();
        for (Optional<IItemHandler> hdlr : hdlrs) {
            hdlr.ifPresent(h -> {
                for (int slot = 0; slot < h.getSlots(); slot++) {
                    ItemStack slotStack = h.getStackInSlot(slot);
                    if (slotStack.isEmpty())
                        continue;
                    if (ItemStack.isSameItem(itemStack, slotStack))
                        count.getAndAdd(slotStack.getCount());
                }
            });
        }

        return count.get();
    }

    public static int getAmount(FluidStack fluidStack, List<Optional<IFluidHandler>> hdlrs) {

        AtomicInteger count = new AtomicInteger();
        for (Optional<IFluidHandler> hdlr : hdlrs) {
            hdlr.ifPresent(h -> {

                for (int slot = 0; slot < h.getTanks(); slot++) {
                    FluidStack slotStack = h.getFluidInTank(slot);
                    if (slotStack.isEmpty())
                        continue;

                    if (FluidStack.isSameFluid(fluidStack, slotStack))
                        count.getAndAdd(slotStack.getAmount());
                }
            });
        }

        return count.get();

    }
}
