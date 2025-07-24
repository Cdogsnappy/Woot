package ipsis.woot.modules.factory.multiblock;

import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;

public class MultiBlockBlockEntity extends BlockEntity implements MultiBlockGlueProvider, WootDebug {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String REGNAME = "multiblock";

    protected MultiBlockGlue glue;

    public MultiBlockBlockEntity(BlockEntityType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        glue = new Glue(this, this);
    }

    public MultiBlockBlockEntity() {
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


    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        return List.of();
    }
}
