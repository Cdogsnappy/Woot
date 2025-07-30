package ipsis.woot.modules.factory.blocks;

import ipsis.woot.fluilds.network.TankPacket;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.client.ClientFactorySetup;
import ipsis.woot.setup.NetworkChannel;
import ipsis.woot.util.TankPacketHandler;
import ipsis.woot.util.WootContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;


public class HeartMenu extends WootContainer implements TankPacketHandler  {

    private HeartBlockEntity tileEntity;

    public HeartMenu(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        super(FactorySetup.HEART_BLOCK_CONTAINER.get(), windowId);
        tileEntity = (HeartBlockEntity)world.getBlockEntity(pos);
        addListeners();

        /**
         * There is no player inventory as it is display only
         */
    }

    public BlockPos getPos() {
        return tileEntity.getPos();
    }


    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getPos()),
                playerIn, FactorySetup.HEART_BLOCK.get());
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!inputFluid.is(tileEntity.getTankFluid().getFluid())) {
            inputFluid = tileEntity.getTankFluid().copy();
            TankPacket tankPacket = new TankPacket(inputFluid, 0);
            for (ContainerListener l : this.) {
                if (l instanceof ServerPlayer) {
                    PacketDistributor.sendToPlayer(((ServerPlayer) l), tankPacket);
                }
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
