package ipsis.woot.modules.squeezer.blocks;

import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.squeezer.SqueezerConfiguration;
import ipsis.woot.util.WootBaseEntityBlock;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
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
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;


import javax.annotation.Nullable;
import java.util.List;

public class EnchantSqueezerBlock extends WootBaseEntityBlock implements WootDebug, EntityBlock {

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
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                           Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        if (!(level.getBlockEntity(pos) instanceof EnchantSqueezerBlockEntity))
            throw new IllegalStateException("Tile entity is missing");

        EnchantSqueezerBlockEntity squeezer = (EnchantSqueezerBlockEntity) level.getBlockEntity(pos);

        if (FluidUtil.getFluidHandler(stack).isPresent())
            return FluidUtil.interactWithFluidHandler(player, hand, level, pos, null) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.FAIL;

        // open the gui
        if (squeezer instanceof MenuProvider)
            player.openMenu(squeezer, squeezer.getBlockPos());
        else
            throw new IllegalStateException("Named container provider is missing");

        return ItemInteractionResult.SUCCESS; // Block was activated
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof EnchantSqueezerBlockEntity)
                ((EnchantSqueezerBlockEntity) te).dropContents(level, pos);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    //-------------------------------------------------------------------------
    //region WootDebug

   
    //endregion


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        CompoundTag nbt = data == null ? new CompoundTag() : data.copyTag();

        if (nbt.contains("energy")) {
            CompoundTag nbtEnergy = nbt.getCompound("energy");
            tooltip.add(Component.translatable("info.woot.energy",
                    nbtEnergy.getInt("energy"), SqueezerConfiguration.ENCH_SQUEEZER_MAX_ENERGY.get()));
        }

        if (nbt.contains("tank")) {
            FluidStack fluidStack = FluidStack.parse(context.registries(), nbt.getCompound("tank")).get();
            if (!fluidStack.isEmpty()) {
                tooltip.add(Component.translatable("info.woot.output_tank",
                        StringHelper.translate(fluidStack.getDescriptionId()),
                        fluidStack.getAmount(),
                        SqueezerConfiguration.ENCH_SQUEEZER_TANK_CAPACITY.get()));
            } else {
                tooltip.add(Component.translatable("info.woot.output_tank.empty",
                        SqueezerConfiguration.ENCH_SQUEEZER_TANK_CAPACITY.get()));
            }
        }
    }

    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> EnchantSqueezerBlock");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EnchantSqueezerBlockEntity(blockPos, blockState);
    }
}
