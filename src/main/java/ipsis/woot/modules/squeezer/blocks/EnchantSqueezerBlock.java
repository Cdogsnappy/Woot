package ipsis.woot.modules.squeezer.blocks;

import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.squeezer.SqueezerConfiguration;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;


import javax.annotation.Nullable;
import java.util.List;

public class EnchantSqueezerBlock extends Block implements WootDebug {

    public EnchantSqueezerBlock() {
        super(Block.Properties.of().sound(SoundType.METAL).strength(3.5F));
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
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnchantSqueezerTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (world.isRemote)
            return ActionResultType.SUCCESS;

        if (!(world.getTileEntity(pos) instanceof EnchantSqueezerTileEntity))
            throw new IllegalStateException("Tile entity is missing");

        EnchantSqueezerTileEntity squeezer = (EnchantSqueezerTileEntity) world.getTileEntity(pos);
        ItemStack heldItem = playerEntity.getHeldItem(hand);

        if (FluidUtil.getFluidHandler(heldItem).isPresent())
            return FluidUtil.interactWithFluidHandler(playerEntity, hand, world, pos, null) ? ActionResultType.SUCCESS : ActionResultType.FAIL;

        // open the gui
        if (squeezer instanceof INamedContainerProvider)
            NetworkHooks.openGui((ServerPlayerEntity)playerEntity, squeezer, squeezer.getPos());
        else
            throw new IllegalStateException("Named container provider is missing");

        return ActionResultType.SUCCESS; // Block was activated
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof EnchantSqueezerTileEntity)
                ((EnchantSqueezerTileEntity) te).dropContents(worldIn, pos);
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    //-------------------------------------------------------------------------
    //region WootDebug

    @Override
    public List<String> getDebugText(List<String> debug, ItemUseContext itemUseContext) {
        debug.add("====> EnchantSqueezerBlock");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }
    //endregion


    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT nbt = stack.getChildTag("BlockEntityTag");
        if (nbt == null)
            return;

        if (nbt.contains("energy")) {
            CompoundNBT nbtEnergy = nbt.getCompound("energy");
            tooltip.add(new TranslationTextComponent("info.woot.energy",
                    nbtEnergy.getInt("energy"), SqueezerConfiguration.ENCH_SQUEEZER_MAX_ENERGY.get()));
        }

        if (nbt.contains("tank")) {
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(nbt.getCompound("tank"));
            if (!fluidStack.isEmpty()) {
                tooltip.add(new TranslationTextComponent("info.woot.output_tank",
                        StringHelper.translate(fluidStack.getTranslationKey()),
                        fluidStack.getAmount(),
                        SqueezerConfiguration.ENCH_SQUEEZER_TANK_CAPACITY.get()));
            } else {
                tooltip.add(new TranslationTextComponent("info.woot.output_tank.empty",
                        SqueezerConfiguration.ENCH_SQUEEZER_TANK_CAPACITY.get()));
            }
        }
    }
}
