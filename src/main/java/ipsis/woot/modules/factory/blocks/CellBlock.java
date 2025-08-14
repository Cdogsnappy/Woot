package ipsis.woot.modules.factory.blocks;

import ipsis.woot.Woot;
import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.FactoryComponentProvider;
import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.util.WootBaseEntityBlock;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class CellBlock extends Block implements WootDebug, FactoryComponentProvider, EntityBlock{

    final Class<? extends CellBlockEntityBase> tileEntityClazz;
    public CellBlock(Class<? extends CellBlockEntityBase> clazz) {
        super(Properties.of().sound(SoundType.METAL).strength(3.5F));
        registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.ATTACHED, false));
        this.tileEntityClazz = clazz;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.ATTACHED);
    }


    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult p_225533_6_) {
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        if (!(level.getBlockEntity(pos) instanceof CellBlockEntityBase))
            throw new IllegalStateException("Tile entity is missing");


        CellBlockEntityBase cb = (CellBlockEntityBase) level.getBlockEntity(pos);
        Woot.setup.getLogger().debug(cb.tank.getCapacity());
        Woot.setup.getLogger().debug(level.getCapability(Capabilities.FluidHandler.BLOCK, pos, null));
        if (FluidUtil.getFluidHandler(stack).isPresent())
            return FluidUtil.interactWithFluidHandler(player, handIn, level, pos, null) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.FAIL;

        return super.useItemOn(stack, state, level, pos, player, handIn, p_225533_6_);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        int transfer = 0;
        int capacity = 0;
        if (stack.getItem() == FactorySetup.CELL_1_BLOCK.get().asItem()) {
            capacity = FactoryConfiguration.CELL_1_CAPACITY.get();
            transfer = FactoryConfiguration.CELL_1_MAX_TRANSFER.get();
        } else if (stack.getItem() == FactorySetup.CELL_2_BLOCK.get().asItem()) {
            capacity = FactoryConfiguration.CELL_2_CAPACITY.get();
            transfer = FactoryConfiguration.CELL_2_MAX_TRANSFER.get();
        } else if (stack.getItem() == FactorySetup.CELL_3_BLOCK.get().asItem()) {
            capacity = FactoryConfiguration.CELL_3_CAPACITY.get();
            transfer = FactoryConfiguration.CELL_3_MAX_TRANSFER.get();
        } else if (stack.getItem() == FactorySetup.CELL_4_BLOCK.get().asItem()) {
            capacity = FactoryConfiguration.CELL_4_CAPACITY.get();
            transfer = FactoryConfiguration.CELL_4_MAX_TRANSFER.get();
        }

        int contents = 0;
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        CompoundTag compoundNBT = data == null ? new CompoundTag() : data.copyTag();
        if (compoundNBT.contains("Tank")) {
            FluidStack fluidStack = FluidStack.parse(context.registries(), compoundNBT.getCompound("Tank")).get();
            contents = fluidStack.getAmount();
        }
        tooltip.add(Component.translatable("info.woot.cell.0", contents, capacity));
        tooltip.add(Component.translatable("info.woot.cell.1", transfer));
    }

    /**
     * FactoryComponentProvider
     */
    @Override
    public FactoryComponent getFactoryComponent() {
        return FactoryComponent.CELL;
    }

    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> CellBlock");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {

        try {
            return tileEntityClazz.getDeclaredConstructor(BlockPos.class, BlockState.class).newInstance(blockPos, blockState);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
