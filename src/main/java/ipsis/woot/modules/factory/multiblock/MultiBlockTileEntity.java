package ipsis.woot.modules.factory.multiblock;

import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.util.WootDebug;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

public class MultiBlockTileEntity extends BlockEntity implements MultiBlockGlueProvider, WootDebug {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String REGNAME = "multiblock";

    protected MultiBlockGlue glue;

    public MultiBlockTileEntity(BlockEntityType type) {
        super(type);
        glue = new Glue(this, this);
    }

    public MultiBlockTileEntity() {
        this(FactorySetup.MULTIBLOCK_BLOCK_TILE.get());
    }

    @Override
    public void validate() {
        super.validate();
        if (!level.isClientSide) {
            //LOGGER.debug("validate");
            MultiBlockTracker.get().addEntry(world, pos);
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (!level.isClientSide) {
            //LOGGER.debug("remove");
            glue.onGoodbye();
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (!level.isClientSide) {
            //LOGGER.debug("onChunkUnloaded");
            glue.onGoodbye();
        }
    }

    @Nonnull
    @Override
    public MultiBlockGlue getGlue() {
        return glue;
    }


}
