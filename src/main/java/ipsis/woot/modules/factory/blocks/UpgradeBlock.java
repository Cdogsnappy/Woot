package ipsis.woot.modules.factory.blocks;

import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.modules.factory.items.PerkItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;


import javax.annotation.Nullable;

public class UpgradeBlock extends FactoryBlock {

    public UpgradeBlock(FactoryComponent component) {
        super(component);
        this.registerDefaultState(this.getStateDefinition().any().setValue(UPGRADE, Perk.EMPTY).setValue(BlockStateProperties.ATTACHED, false));
    }

    public static final EnumProperty<Perk> UPGRADE_TYPE;
    static { UPGRADE_TYPE = EnumProperty.create("upgrade", Perk.class); }

    public static final EnumProperty<Perk> UPGRADE = UPGRADE_TYPE;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UPGRADE, BlockStateProperties.ATTACHED);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!level.isClientSide) {
            if (!stack.isEmpty() && stack.getItem() instanceof PerkItem perkItem) {

                BlockEntity te = level.getBlockEntity(pos);
                if (te instanceof UpgradeBlockEntity) {
                    if (((UpgradeBlockEntity) te).tryAddUpgrade(level, player, state, perkItem.getFactoryUpgrade())) {
                        if (!player.isCreative())
                            stack.shrink(1);
                    }
                }
            }
        }

        return super.useItemOn(stack, state,level, pos, player, hand, hitResult);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        // This is how the chest, hopper etc drop their contents
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof UpgradeBlockEntity) {
                ((UpgradeBlockEntity) te).dropItems(state, level, pos);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos,BlockState state) {
        return new UpgradeBlockEntity(pos, state);
    }
}
