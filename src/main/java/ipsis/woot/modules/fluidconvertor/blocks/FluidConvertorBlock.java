package ipsis.woot.modules.fluidconvertor.blocks;

import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.fluidconvertor.FluidConvertorConfiguration;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;


import javax.annotation.Nullable;
import java.util.List;

public class FluidConvertorBlock extends Block implements EntityBlock, WootDebug {

    public FluidConvertorBlock() {
        super(Block.Properties.of().sound(SoundType.METAL).strength(3.5F));
        registerDefaultState(getStateDefinition().any().setValue(
                BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH
        ));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(
                BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidConvertorBlockEntity(pos, state);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult blockRayTraceResult) {
        if (worldIn.isRemote)
            return ActionResultType.SUCCESS;

        if (!(worldIn.getTileEntity(pos) instanceof FluidConvertorBlockEntity))
            throw new IllegalStateException("Tile entity is missing");

        FluidConvertorBlockEntity tileEntity = (FluidConvertorBlockEntity) worldIn.getTileEntity(pos);
        ItemStack heldItem = player.getHeldItem(handIn);

        if (FluidUtil.getFluidHandler(heldItem).isPresent()) {
            return FluidUtil.interactWithFluidHandler(player, handIn, worldIn, pos, blockRayTraceResult.getFace()) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        } else {
            // open the gui
            if (tileEntity instanceof INamedContainerProvider)
                NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity, tileEntity.getPos());
            else
                throw new IllegalStateException("Named container provider is missing");
            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof FluidConvertorBlockEntity)
                ((FluidConvertorBlockEntity) te).dropContents(level, pos);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    //-------------------------------------------------------------------------
    //region WootDebug

    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> " + this.getClass().toString());
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }
    //endregion

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        CompoundTag nbt = stack.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
        if (nbt == null)
            return;

        if (nbt.contains("energy")) {
            CompoundTag nbtEnergy = nbt.getCompound("energy");
            tooltip.add(Component.translatable("info.woot.energy",
                    nbtEnergy.getInt("energy"), FluidConvertorConfiguration.FLUID_CONV_MAX_ENERGY.get()));
        }

        if (nbt.contains("inputTank")) {
            FluidStack fluidStack = FluidStack.parse(context.registries(), nbt.getCompound("inputTank")).get();
            if (!fluidStack.isEmpty()) {
                tooltip.add(Component.translatable("info.woot.input_tank",
                        StringHelper.translate(fluidStack.getDescriptionId()),
                        fluidStack.getAmount(),
                        FluidConvertorConfiguration.FLUID_CONV_INPUT_TANK_CAPACITY.get()));
            } else {
                tooltip.add(Component.translatable("info.woot.input_tank.empty",
                        FluidConvertorConfiguration.FLUID_CONV_INPUT_TANK_CAPACITY.get()));
            }

        }

        if (nbt.contains("outputTank")) {
            FluidStack fluidStack = FluidStack.parse(context.registries(), nbt.getCompound("outputTank")).get();
            if (!fluidStack.isEmpty()) {
                tooltip.add(Component.translatable("info.woot.output_tank",
                        StringHelper.translate(fluidStack.getDescriptionId()),
                        fluidStack.getAmount(),
                        FluidConvertorConfiguration.FLUID_CONV_OUTPUT_TANK_CAPACITY.get()));
            } else {
                tooltip.add(Component.translatable("info.woot.output_tank.empty",
                    FluidConvertorConfiguration.FLUID_CONV_OUTPUT_TANK_CAPACITY.get()));
            }
        }
    }

}
