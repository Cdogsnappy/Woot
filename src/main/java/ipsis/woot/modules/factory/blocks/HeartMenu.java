package ipsis.woot.modules.factory.blocks;

import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.client.ClientFactorySetup;
import ipsis.woot.setup.NetworkChannel;
import ipsis.woot.util.TankPacketHandler;
import ipsis.woot.util.WootContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;


public class HeartMenu extends WootContainer implements TankPacketHandler  {

    private HeartBlockEntity tileEntity;
    private Inventory pInventory;

    public HeartMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public HeartMenu(int windowId, Inventory playerInventory, BlockEntity entity) {
        super(FactorySetup.HEART_BLOCK_CONTAINER.get(), windowId);
        tileEntity = (HeartBlockEntity)entity;
        this.pInventory = playerInventory;
        addListeners();

        /**
         * There is no player inventory as it is display only
         */
    }

    public BlockPos getPos() {
        return tileEntity.getBlockPos();
    }


    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos()),
                playerIn, FactorySetup.HEART_BLOCK.get());
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!inputFluid.is(tileEntity.getTankFluid().getFluid())) {
            inputFluid = tileEntity.getTankFluid().copy();
            TankPacket tankPacket = new TankPacket(inputFluid, 0);
            if(pInventory.player instanceof ServerPlayer pl) {
                PacketDistributor.sendToPlayer(pl, tankPacket);
            }
        }
    }

    private int progress = 0;
    private FluidStack inputFluid = FluidStack.EMPTY;
    private int cellType = 0;

    @OnlyIn(Dist.CLIENT)
    public FluidStack getInputFluid() { return inputFluid; }
    @OnlyIn(Dist.CLIENT)
    public int getProgress() { return this.progress; }

    @OnlyIn(Dist.CLIENT)
    public int getCellType() { return this.cellType; }


    private void addListeners() {
        addShortListener(new DataSlot() {
            @Override
            public int get() { return tileEntity.getProgress(); }

            @Override
            public void set(int i) { progress = i; }
        });
        addIntegerListener(new DataSlot() {
            @Override
            public int get() {
                return tileEntity.getCellType();
            }

            @Override
            public void set(int i) { cellType = i; }
        });
    }

    public HeartBlockEntity getTileEntity() { return tileEntity; }

    /**
     * Client sync
     */
    public void handleStaticDataReply(ClientFactorySetup clientFactorySetup) {
        tileEntity.setClientFactorySetup(clientFactorySetup);
    }

    @Override
    public void handlePacket(TankPacket packet) {
        if (packet.tankId() == 0)
            inputFluid = packet.fluidStack();
    }
}
