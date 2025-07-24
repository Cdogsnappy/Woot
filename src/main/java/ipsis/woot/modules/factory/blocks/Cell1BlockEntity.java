package ipsis.woot.modules.factory.blocks;

import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.modules.factory.FactorySetup;

public class Cell1BlockEntity extends CellBlockEntityBase {

    public Cell1BlockEntity() {
        super(FactorySetup.CELL_1_BLOCK_TILE.get());
    }

    @Override
    public int getCapacity() {
        return FactoryConfiguration.CELL_1_CAPACITY.get();
    }

    @Override
    public int getMaxTransfer() {
        return FactoryConfiguration.CELL_1_MAX_TRANSFER.get();
    }
}
