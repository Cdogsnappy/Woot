package ipsis.woot.modules.debug.blocks;

import ipsis.woot.modules.debug.DebugSetup;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.WootFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class DebugTankBlockEntity extends BlockEntity implements WootDebug {

    public DebugTankBlockEntity(BlockPos pos, BlockState state) {
        super(DebugSetup.DEBUG_TANK_BLOCK_TILE.get(), pos, state);
    }

    private Optional<WootFluidTank> inputTank = Optional.of(createTank());
    private WootFluidTank createTank() {
        return new WootFluidTank(Integer.MAX_VALUE);
    }


    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> DebugTankTileEntity");
        inputTank.ifPresent(h-> {
            if (h.getFluid().isEmpty())
                debug.add("    Empty");
            else
                debug.add("    " + h.getFluid().getDescriptionId() + "/" + h.getFluidAmount());
        });
        return debug;
    }

}
