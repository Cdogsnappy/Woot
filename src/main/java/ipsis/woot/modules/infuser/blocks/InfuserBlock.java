package ipsis.woot.modules.infuser.blocks;

import ipsis.woot.modules.anvil.blocks.AnvilTileEntity;
import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.infuser.InfuserConfiguration;
import ipsis.woot.modules.squeezer.SqueezerConfiguration;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;


import javax.annotation.Nullable;
import java.util.List;

public class InfuserBlock extends Block implements WootDebug {

    public InfuserBlock() {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(3.5F));
        setDefaultState(getStateContainer().getBaseState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new InfuserTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult blockRayTraceResult) {
        if (worldIn.isRemote)
            return ActionResultType.SUCCESS;

        if (!(worldIn.getTileEntity(pos) instanceof InfuserTileEntity))
            throw new IllegalStateException("Tile entity is missing");

        InfuserTileEntity infuser = (InfuserTileEntity)worldIn.getTileEntity(pos);
        ItemStack heldItem = player.getHeldItem(handIn);

        if (FluidUtil.getFluidHandler(heldItem).isPresent()) {
            return FluidUtil.interactWithFluidHandler(player, handIn, worldIn, pos, null) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        } else  {
            // open the gui
            if (infuser instanceof INamedContainerProvider)
                NetworkHooks.openGui((ServerPlayerEntity) player, infuser, infuser.getPos());
            else
                throw new IllegalStateException("Named container provider is missing");
            return ActionResultType.SUCCESS;

        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof InfuserTileEntity)
                ((InfuserTileEntity) te).dropContents(level, pos);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }



    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        CompoundTag nbt = stack.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();

        if (nbt.contains("energy")) {
            CompoundTag nbtEnergy = nbt.getCompound("energy");
            tooltip.add(Component.translatable("info.woot.energy",
                    nbtEnergy.getInt("energy"), InfuserConfiguration.INFUSER_MAX_ENERGY.get()));
        }

        if (nbt.contains("tank")) {
            FluidStack fluidStack = FluidStack.parse(context.registries(), nbt.getCompound("tank")).get();
            if (!fluidStack.isEmpty()) {
                tooltip.add(Component.translatable("info.woot.input_tank",
                        StringHelper.translate(fluidStack.getDescriptionId()),
                        fluidStack.getAmount(),
                        InfuserConfiguration.INFUSER_TANK_CAPACITY.get()));
            } else {
                tooltip.add(Component.translatable("info.woot.input_tank.empty",
                        InfuserConfiguration.INFUSER_TANK_CAPACITY.get()));
            }
        }
    }

    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> InfuserBlock");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }
}
