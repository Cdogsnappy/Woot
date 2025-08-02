package ipsis.woot.modules.debug.blocks;

import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


import javax.annotation.Nullable;
import java.util.List;

public class DebugTankBlock extends Block implements WootDebug, EntityBlock {

    public DebugTankBlock() {
        super(Properties.of().sound(SoundType.METAL));
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DebugTankBlockEntity(pos, state);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote)
            return ActionResultType.SUCCESS;

        if (!(worldIn.getTileEntity(pos) instanceof DebugTankBlockEntity))
            throw new IllegalStateException("Tile entity is missing");

        ItemStack heldItem = player.getHeldItem(handIn);
        if (FluidUtil.getFluidHandler(heldItem).isPresent())
            return FluidUtil.interactWithFluidHandler(
                    player,
                    handIn,
                    worldIn,
                    pos,
                    null) ? ActionResultType.SUCCESS : ActionResultType.FAIL;

        return ActionResultType.SUCCESS;
    }

    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> DebugTank");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }
}
