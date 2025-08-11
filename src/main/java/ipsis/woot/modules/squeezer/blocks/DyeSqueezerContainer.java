package ipsis.woot.modules.squeezer.blocks;

import ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipe;
import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.modules.infuser.InfuserSetup;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import ipsis.woot.setup.NetworkChannel;
import ipsis.woot.util.TankPacketHandler;
import ipsis.woot.util.WootContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class DyeSqueezerContainer extends WootContainer implements TankPacketHandler {

    public DyeSqueezerBlockEntity tileEntity;
    public Player player;
    public ContainerData containerData;

    public DyeSqueezerContainer(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public DyeSqueezerContainer(int windowId, Inventory playerInventory, BlockEntity entity) {
        super(SqueezerSetup.SQUEEZER_BLOCK_CONTAINER.get(), windowId);
        tileEntity = (DyeSqueezerBlockEntity) entity;
        addOwnSlots(tileEntity.stackInputHandler);
        addPlayerSlots(playerInventory);
        addListeners();
        this.player = playerInventory.player;
    }

    private void addOwnSlots(IItemHandler inv) {
        this.addSlot(new SlotItemHandler(inv, 0, 39, 40));
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
        return stillValid(ContainerLevelAccess.create(playerIn.level(),tileEntity.getBlockPos()), playerIn, SqueezerSetup.SQUEEZER_BLOCK.get());
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
            if (DyeSqueezerRecipe.isValidInput(stack)) {
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

    private int red = 0;
    private int yellow = 0;
    private int blue = 0;
    private int white = 0;
    private int progress = 0;
    private int energy = 0;
    private FluidStack pureDye = FluidStack.EMPTY;
    private boolean dumpExcess = false;

    @OnlyIn(Dist.CLIENT)
    public int getRedDyeAmount() { return this.red; }
    @OnlyIn(Dist.CLIENT)
    public int getYellowDyeAmount() { return this.yellow; }
    @OnlyIn(Dist.CLIENT)
    public int getBlueDyeAmount() { return this.blue; }
    @OnlyIn(Dist.CLIENT)
    public int getWhiteDyeAmount() { return this.white; }
    @OnlyIn(Dist.CLIENT)
    public int getProgress() { return this.progress; }
    @OnlyIn(Dist.CLIENT)
    public FluidStack getPureDye() { return this.pureDye; }
    @OnlyIn(Dist.CLIENT)
    public int getEnergy() { return energy; }
    @OnlyIn(Dist.CLIENT)
    public boolean getDumpExcess() { return this.dumpExcess; }

    public void addListeners() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() { return tileEntity.getRed(); }

            @Override
            public void set(int i) { red = i; }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() { return tileEntity.getBlue(); }

            @Override
            public void set(int i) { blue = i; }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() { return tileEntity.getWhite(); }

            @Override
            public void set(int i) { white = i; }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() { return tileEntity.getYellow(); }

            @Override
            public void set(int i) { yellow = i; }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() { return tileEntity.getEnergy(); }

            @Override
            public void set(int i){
                energy = i;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() { return tileEntity.getProgress(); }

            @Override
            public void set(int i) { progress = i; }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() { return tileEntity.getDumpExcess() ? 1 : 0; }

            @Override
            public void set(int i) { dumpExcess = i == 1; }
        });
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!FluidStack.isSameFluidSameComponents(pureDye,tileEntity.getOutputTankFluid())) {
            pureDye = tileEntity.getOutputTankFluid().copy();

            PacketDistributor.sendToPlayer((ServerPlayer)player, tileEntity.getOutputTankPacket());


        }
    }

    @Override
    public void handlePacket(TankPacket packet) {
        if (packet.tankId() == 0)
            pureDye = packet.fluidStack();
    }
}
