package ipsis.woot.modules.fluidconvertor.blocks;

import ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipe;
import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.setup.NetworkChannel;
import ipsis.woot.util.TankPacketHandler;
import ipsis.woot.util.WootContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;


public class FluidConvertorMenu extends WootContainer implements TankPacketHandler {

    public FluidConvertorBlockEntity tileEntity;
    Player player;

    public FluidConvertorMenu(int windowId, Inventory playerInventory, Player playerEntity, FluidConvertorBlockEntity entity) {
        super(FluidConvertorSetup.FLUID_CONVERTOR_BLOCK_CONTATAINER.get(), windowId);
        tileEntity = entity;
        this.player = playerEntity;

        addOwnSlots(tileEntity.inventory);
        addPlayerSlots(playerInventory);
        addListeners();
    }

    private void addOwnSlots(ItemStackHandler inv) {
        this.addSlot(new SlotItemHandler(inv, 0, 100, 22));
    }

    private void addPlayerSlots(Inventory playerInventory) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 10 + col * 18;
                int y = row * 18 + 95;
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        for (int row = 0; row < 9; ++row) {
            int x = 10 + row * 18;
            this.addSlot(new Slot(playerInventory, row, x, 153));
        }
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(playerIn.level(),tileEntity.getBlockPos()), playerIn, InfuserSetup.INFUSER_BLOCK.get());
    }

    private FluidStack inputFluid = FluidStack.EMPTY;
    private FluidStack outputFluid = FluidStack.EMPTY;
    private int progress = 0;
    private int energy = 0;

    @OnlyIn(Dist.CLIENT)
    public int getProgress() { return this.progress; }
    @OnlyIn(Dist.CLIENT)
    public int getEnergy() { return this.energy; }
    @OnlyIn(Dist.CLIENT)
    public FluidStack getInputFluid() { return inputFluid; }
    @OnlyIn(Dist.CLIENT)
    public FluidStack getOutputFluid() { return outputFluid; }


    public void addListeners() {
        addIntegerListener(new DataSlot() {
            @Override
            public int get() { return tileEntity.getEnergy(); }

            @Override
            public void set(int i) { energy = i; }
        });

        addShortListener(new DataSlot() {
            @Override
            public int get() { return tileEntity.getProgress(); }

            @Override
            public void set(int i) { progress = i; }
        });
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!FluidStack.isSameFluidSameComponents(inputFluid, tileEntity.getInputTankFluid())) {
            inputFluid = tileEntity.getInputTankFluid().copy();
            TankPacket tankPacket = new TankPacket(inputFluid, 0);
            PacketDistributor.sendToPlayer((ServerPlayer)player, tankPacket);

        }

        if (!FluidStack.isSameFluidSameComponents(outputFluid, tileEntity.getOutputTankFluid())) {
            outputFluid = tileEntity.getOutputTankFluid().copy();
            TankPacket tankPacket = new TankPacket(outputFluid, 1);
            PacketDistributor.sendToPlayer((ServerPlayer)player, tankPacket);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {

        // Based off Gigaherz Elements Of Power code
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack stackCopy = stack.copy();

        int startIndex;
        int endIndex;

        final int MACHINE_INV_SIZE = 1;
        final int PLAYER_INV_SIZE = 27;
        final int TOOLBAR_INV_SIZE = 9;

        if (index >= MACHINE_INV_SIZE) {
            // player slot
            if (FluidConvertorRecipe.isValidCatalyst(stack)) {
                // -> machine
                startIndex = 0;
                endIndex = MACHINE_INV_SIZE;
            } else if (index < PLAYER_INV_SIZE + MACHINE_INV_SIZE) {
                // -> toolbar
                startIndex = PLAYER_INV_SIZE + MACHINE_INV_SIZE;
                endIndex = startIndex + TOOLBAR_INV_SIZE;
            } else if (index >= PLAYER_INV_SIZE + MACHINE_INV_SIZE) {
                // -> player
                startIndex = MACHINE_INV_SIZE;
                endIndex = startIndex + PLAYER_INV_SIZE;
            } else {
                return ItemStack.EMPTY;
            }
        } else {
            // machine slot
            startIndex = MACHINE_INV_SIZE;
            endIndex = startIndex + PLAYER_INV_SIZE + TOOLBAR_INV_SIZE;
        }

        if (!this.moveItemStackTo(stack, startIndex, endIndex, false))
            return ItemStack.EMPTY;

        if (stack.getCount() == 0)
            slot.set(ItemStack.EMPTY);
        else
            slot.setChanged();

        if (stack.getCount() == stackCopy.getCount())
            return ItemStack.EMPTY;

        slot.onTake(playerIn, stack);
        return stackCopy;
    }

    @Override
    public void handlePacket(TankPacket packet) {
        if (packet.tankId() == 0)
            inputFluid = packet.fluidStack();
        else if (packet.tankId() == 1)
            outputFluid = packet.fluidStack();
    }
}
