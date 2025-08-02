package ipsis.woot.modules.layout.blocks;

import ipsis.woot.util.helper.WorldHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

public class LayoutBlock extends Block {

    public LayoutBlock() {
        super(Block.Properties.of().sound(SoundType.GLASS).strength(0.3F));
        registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LayoutBlockEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult blockRayTraceResult) {

        if (worldIn.isRemote || handIn == Hand.OFF_HAND)
            return super.onBlockActivated(state, worldIn, pos, player, handIn, blockRayTraceResult);

        if (!player.getHeldItemMainhand().isEmpty())
            return ActionResultType.FAIL;

        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof LayoutBlockEntity) {
            LayoutBlockEntity layout = (LayoutBlockEntity)te;
            if (player.isSneaking()) {
                layout.setNextLevel();
            } else {
                layout.setNextTier();
            }
            WorldHelper.updateClient(worldIn, pos);
        }

        return ActionResultType.SUCCESS;
    }
}
