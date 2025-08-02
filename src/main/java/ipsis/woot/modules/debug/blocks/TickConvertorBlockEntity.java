package ipsis.woot.modules.debug.blocks;

import ipsis.woot.modules.debug.DebugSetup;
import ipsis.woot.modules.factory.blocks.CellBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This is the cheaty converter and allows you to keep the cells
 * full, essentially turning a factory into a free mob farm,
 * with the spawn time being the only limiting factor.
 */
public class TickConvertorBlockEntity extends BlockEntity {

    public TickConvertorBlockEntity(BlockPos pos, BlockState state) {
        super(DebugSetup.CREATIVE_CONATUS_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        for (Direction facing : Direction.values()) {
            BlockEntity te = level.getBlockEntity(getBlockPos().offset(facing.getNormal()));
            if (!(te instanceof CellBlockEntityBase))
                continue;

            ((CellBlockEntityBase) te).fillToCapacity();
        }
    }
}
