package ipsis.woot.modules.anvil.blocks;

import ipsis.woot.crafting.anvil.AnvilRecipe;
import ipsis.woot.modules.anvil.AnvilConfiguration;
import ipsis.woot.modules.anvil.AnvilSetup;
import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class AnvilBlock extends Block implements WootDebug, EntityBlock {

    // From vanilla
    private static final VoxelShape PART_BASE = Shapes.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    private static final VoxelShape PART_LOWER_X = Block.makeCuboidShape(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
    private static final VoxelShape PART_MID_X = Block.makeCuboidShape(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    private static final VoxelShape PART_UPPER_X = Block.makeCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
    private static final VoxelShape PART_LOWER_Z = Block.makeCuboidShape(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
    private static final VoxelShape PART_MID_Z = Block.makeCuboidShape(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
    private static final VoxelShape PART_UPPER_Z = Block.makeCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
    private static final VoxelShape X_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER_X, PART_MID_X, PART_UPPER_X);
    private static final VoxelShape Z_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER_Z, PART_MID_Z, PART_UPPER_Z);

    public AnvilBlock() {
        super(Properties.of().sound(SoundType.METAL).strength(3.5F));
        registerDefaultState(getStateDefinition().any());
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnvilBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().rotateY());
    }

    @Override
    protected void S(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(BlockStateProperties.HORIZONTAL_FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (level.isClientSide)
            super.useItemOn(stack, state, level, pos, player, hand, hitResult);

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof AnvilBlockEntity) {
            AnvilBlockEntity anvil = (AnvilBlockEntity)te;

            if (player.isCrouching() && stack.isEmpty()) {
                // Sneak with empty hand to empty
                anvil.dropItem(player);
            } else if (stack.getItem() == AnvilSetup.HAMMER_ITEM.get()) {
                // Crafting
                anvil.tryCraft(player);
            } else {
                if (!anvil.hasBaseItem()) {
                    // Check if valid base item
                    if (AnvilRecipe.isValidInput(stack)) {
                        ItemStack baseItem = stack.copy();
                        baseItem.setCount(1);
                        anvil.setBaseItem(baseItem);
                        stack.shrink(1);
                        if (stack.isEmpty())
                            player.getInventory().setItem( player.getInventory().selected, ItemStack.EMPTY);
                        else
                            player.getInventory().setItem( player.getInventory().selected, stack);
                        player.containerMenu.broadcastChanges();
                    } else {
                        player.sendSystemMessage(Component.translatable("chat.woot.anvil.nobase"));
                    }
                } else {
                    // Base item already present
                    ItemStack ingredient = stack.copy();
                    ingredient.setCount(1);
                    if (anvil.addIngredient(ingredient)) {
                        stack.shrink(1);
                        if (stack.isEmpty())
                            player.getInventory().setItem( player.getInventory().selected, ItemStack.EMPTY);
                        else
                            player.getInventory().setItem( player.getInventory().selected, stack);
                        player.containerMenu.broadcastChanges();
                    }
                }
            }

        }

        return ItemInteractionResult.SUCCESS;
    }

    public boolean isAnvilHot(Level world, BlockPos pos) {
        return world.getBlockState(pos.below()).getBlock() == Blocks.MAGMA_BLOCK;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
        super.animateTick(stateIn, worldIn, pos, rand);
        if (AnvilConfiguration.ANVIL_PARTICILES.get() && rand.nextInt(10) == 0 && isAnvilHot(worldIn, pos))
            worldIn.addParticle(ParticleTypes.LAVA, (double) ((float) pos.getX() + rand.nextFloat()), (double) ((float) pos.getY() + 1.1F), (double) ((float) pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void onReplaced(BlockState blockState, Level world, BlockPos pos, BlockState newBlockState, boolean isMoving) {
        if (blockState.getBlock() != newBlockState.getBlock()) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof AnvilBlockEntity)
                ((AnvilBlockEntity) te).dropContents(world, pos);
            super.onReplaced(blockState, world, pos, newBlockState, isMoving);
        }
    }

    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> AnvilBlock");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }

}
