package ipsis.woot.modules.factory.blocks;

import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.modules.factory.FactorySetup;

public class Cell2BlockEntity extends CellBlockEntityBase {

    public Cell2BlockEntity() {
        super(FactorySetup.CELL_2_BLOCK_TILE.get());
    }

    @Override
    public int getCapacity() {
        return FactoryConfiguration.CELL_2_CAPACITY.get();
    }

    @Override
    public int getMaxTransfer() {
        return FactoryConfiguration.CELL_2_MAX_TRANSFER.get();
    }
}
