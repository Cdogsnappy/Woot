package ipsis.woot.modules.debug.blocks;

import ipsis.woot.modules.debug.DebugSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;

public class CreativePowerBlockEntity extends BlockEntity {

    public CreativePowerBlockEntity(BlockPos pos, BlockState state) {
        super(DebugSetup.CREATIVE_POWER_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if (level.isClientSide)
            return;

        // Fill adjacent every second
        for (Direction facing : Direction.values()) {
            BlockEntity te = level.getBlockEntity(getBlockPos().offset(facing.getNormal()));
            if (te != null) {
                EnergyStorage e = (EnergyStorage) level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos(), facing.getOpposite());
                if(e != null && e.canReceive()){
                    e.receiveEnergy(1000, false);
                }

            }
        }
    }

}
