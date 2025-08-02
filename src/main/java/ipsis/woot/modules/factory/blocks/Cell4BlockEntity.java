package ipsis.woot.modules.factory.blocks;

import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.modules.factory.FactorySetup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Cell4BlockEntity extends CellBlockEntityBase {

    public Cell4BlockEntity(BlockPos pos, BlockState state) {
        super(FactorySetup.CELL_4_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public int getCapacity() {
        return FactoryConfiguration.CELL_4_CAPACITY.get();
    }

    @Override
    public int getMaxTransfer() {
        return FactoryConfiguration.CELL_4_MAX_TRANSFER.get();
    }
}
