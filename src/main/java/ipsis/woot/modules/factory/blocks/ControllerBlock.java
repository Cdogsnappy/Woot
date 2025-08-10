package ipsis.woot.modules.factory.blocks;

import com.mojang.serialization.MapCodec;
import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.FactoryComponentProvider;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.List;

public class ControllerBlock extends Block implements FactoryComponentProvider, WootDebug, EntityBlock {


    public ControllerBlock(Properties prop) {

        super(Properties.of().sound(SoundType.METAL).strength(3.5F));
        registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.ATTACHED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.ATTACHED);
    }

    /**
     * Block display since we are less than a full block
     */
    private final VoxelShape shape = Shapes.box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (state.getValue(BlockStateProperties.ATTACHED))
            return Shapes.block();
        else
            return shape;
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult){
        if (level.isClientSide)
            return super.useItemOn(stack, state,level, pos, player, hand, hitResult);

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof ControllerBlockEntity) {
            Tier tier = ((ControllerBlockEntity) te).getTier();
            if (tier != Tier.UNKNOWN)
                player.sendSystemMessage(Component.translatable(tier.getTranslationKey()));
        }

        return ItemInteractionResult.SUCCESS;
    }

    /**
     * FactoryComponentProvider
     */
    @Override
    public FactoryComponent getFactoryComponent() {
        return FactoryComponent.CONTROLLER;
    }

    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> ControllerBlock");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ControllerBlockEntity(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if(data != null){
            ControllerBlockEntity cbe = level.getBlockEntity(pos, FactorySetup.CONTROLLER_BLOCK_TILE.get()).get();
            cbe.setFakeMob(new FakeMob(data.copyTag()));
        }
    }

    }
