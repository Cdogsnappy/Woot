package ipsis.woot.modules.debug.blocks;

import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;


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
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        if (!(level.getBlockEntity(pos) instanceof DebugTankBlockEntity))
            throw new IllegalStateException("Tile entity is missing");

        if (FluidUtil.getFluidHandler(stack).isPresent())
            return FluidUtil.interactWithFluidHandler(
                    player,
                    hand,
                    level,
                    pos,
                    null) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.FAIL;

        return ItemInteractionResult.SUCCESS;
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
