package ipsis.woot.modules.oracle.blocks;

import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.oracle.OracleSetup;
import ipsis.woot.modules.oracle.network.SimulatedMobDropsSummaryReply;
import ipsis.woot.modules.oracle.network.SimulatedMobsReply;
import ipsis.woot.setup.NetworkChannel;
import ipsis.woot.setup.ServerDataRequest;
import ipsis.woot.simulator.SimulatedMobDropSummary;
import ipsis.woot.util.FakeMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OracleContainer extends AbstractContainerMenu {

    public BlockEntity tileEntity;

    public OracleContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        super(OracleSetup.ORACLE_BLOCK_CONTAINER.get(), windowId);
        tileEntity = world.getBlockEntity(pos);

        /**
         * There is no player inventory as it is display only
         */
    }

    public BlockPos getPos() { return tileEntity.getBlockPos(); }

    /**
     * Server data sync
     */
    public List<FakeMob> simulatedMobs = new ArrayList<>();
    public List<SimulatedMobDropSummary> simulatedDrops = new ArrayList<>();
    public void refreshMobs() {
        PacketDistributor.sendToServer(new ServerDataRequest("", getPos(), ServerDataRequest.Type.DROP_REGISTRY_STATUS.ordinal()));
        simulatedMobs.clear();
        simulatedDrops.clear();
    }

    public void refreshDrops(int index) {
        if (simulatedMobs.size() > index) {
            PacketDistributor.sendToServer(new ServerDataRequest(simulatedMobs.get(index).getName(),
                    getPos(), ServerDataRequest.Type.SIMULATED_MOB_DROPS.ordinal()));
        }
        simulatedDrops.clear();
    }


    public void handleSimulatedMobsReply(SimulatedMobsReply msg) {
        simulatedMobs.clear();
        simulatedMobs.addAll(msg.simulatedMobs());
        if (!simulatedMobs.isEmpty())
            refreshDrops(0);
    }

    public void handleSimulatedMobDropsSummaryReply(SimulatedMobDropsSummaryReply msg) {
        simulatedDrops.clear();
        simulatedDrops.addAll(msg.drops());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), tileEntity.getBlockPos()), player, OracleSetup.ORACLE_BLOCK.get());
    }
}
