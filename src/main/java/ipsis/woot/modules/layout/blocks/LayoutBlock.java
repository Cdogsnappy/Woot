package ipsis.woot.modules.layout.blocks;

import ipsis.woot.util.WootBaseEntityBlock;
import ipsis.woot.util.helper.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class LayoutBlock extends WootBaseEntityBlock {

    public LayoutBlock() {
        super(Block.Properties.of().sound(SoundType.GLASS).strength(0.3F));
        registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }


    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!level.isClientSide || hand == InteractionHand.OFF_HAND)
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);

        if(stack.isEmpty()){
            return ItemInteractionResult.FAIL;
        }
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof LayoutBlockEntity) {
            LayoutBlockEntity layout = (LayoutBlockEntity)te;
            if (player.isCrouching()) {
                layout.setNextLevel();
            } else {
                layout.setNextTier();
            }
            WorldHelper.updateClient(level, pos);
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new LayoutBlockEntity(blockPos, blockState);
    }
}
