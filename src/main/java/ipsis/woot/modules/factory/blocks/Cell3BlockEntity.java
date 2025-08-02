package ipsis.woot.modules.factory.blocks;

import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.modules.factory.FactorySetup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class Cell3BlockEntity extends CellBlockEntityBase {

    public Cell3BlockEntity(BlockPos pos, BlockState state) {
        super(FactorySetup.CELL_3_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public int getCapacity() {
        return FactoryConfiguration.CELL_3_CAPACITY.get();
    }

    @Override
    public int getMaxTransfer() {
        return FactoryConfiguration.CELL_3_MAX_TRANSFER.get();
    }
}
