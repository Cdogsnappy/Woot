package ipsis.woot.modules.factory.blocks;

import ipsis.woot.modules.debug.DebugSetup;
import ipsis.woot.modules.layout.LayoutSetup;
import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.FactoryComponentProvider;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;


import javax.annotation.Nullable;
import java.util.List;

public class HeartBlock extends Block implements FactoryComponentProvider, WootDebug {

    public HeartBlock() {
        super(Properties.of().sound(SoundType.METAL).strength(3.5F));
        registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    public ItemInteractionResult useItemOn(ItemStack stack, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult blockHitResult) {

        if (worldIn.isClientSide || handIn == InteractionHand.OFF_HAND)
            return ItemInteractionResult.SUCCESS;

        if (player.isCrouching())
            return ItemInteractionResult.FAIL;

        if (player.getItemInHand(handIn).getItem() == LayoutSetup.INTERN_ITEM.get() || player.getItemInHand(handIn).getItem() == DebugSetup.DEBUG_ITEM.get()) {
                // intern is used on the heart, so cannot open the gui
                return ItemInteractionResult.FAIL; // Block was not activated
        }

        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof HeartBlockEntity && !((HeartBlockEntity) te).isFormed())
                return ItemInteractionResult.FAIL;

        if (te instanceof MenuProvider)
            player.openMenu((MenuProvider)te, te.getBlockPos());
        else
            throw new IllegalStateException("Named container provider is missing");

        return ItemInteractionResult.CONSUME; // Block was activated
    }

    /**
     * FactoryComponentProvider
     */
    @Override
    public FactoryComponent getFactoryComponent() {
        return FactoryComponent.HEART;
    }

    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> HeartBlock");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }
}
