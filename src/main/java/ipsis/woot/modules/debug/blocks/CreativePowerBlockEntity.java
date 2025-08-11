package ipsis.woot.modules.debug.blocks;

import ipsis.woot.Woot;
import ipsis.woot.modules.debug.DebugSetup;
import ipsis.woot.util.WootMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class CreativePowerBlockEntity extends WootMachineBlockEntity {

    public CreativePowerBlockEntity(BlockPos pos, BlockState state) {
        super(DebugSetup.CREATIVE_POWER_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public void tick(Level level) {
        if (level.isClientSide)
            return;

        // Fill adjacent every second
        for (Direction facing : Direction.values()) {
            BlockEntity te = level.getBlockEntity(getBlockPos().offset(facing.getNormal()));
            if (te != null) {
                IEnergyStorage e =  level.getCapability(Capabilities.EnergyStorage.BLOCK, te.getBlockPos(),te.getBlockState(), te, facing.getOpposite());
                if(e != null && e.canReceive()){
                    e.receiveEnergy(1000, false);
                    te.setChanged();
                }

            }
        }
    }

}
