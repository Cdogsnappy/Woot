package ipsis.woot.modules.factory.blocks;

import ipsis.woot.modules.factory.Exotic;
import ipsis.woot.modules.factory.FactoryConfiguration;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


import javax.annotation.Nullable;
import java.util.List;

public class ExoticBlock extends Block implements WootDebug {

    private final Exotic exotic;

    public ExoticBlock(Exotic exotic) {
        super(Properties.of().sound(SoundType.METAL).strength(3.5F));
        this.exotic = exotic;
    }

    public Exotic getExotic() {
        return exotic;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos.below());
            if (te instanceof HeartBlockEntity)
                ((HeartBlockEntity) te).interrupt();
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        if (level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos.below());
            if (te instanceof HeartBlockEntity)
                ((HeartBlockEntity) te).interrupt();
        }
        return state;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        tooltip.add(Component.translatable("info.woot.exotic.0"));
        if (stack.getItem() == FactorySetup.EXOTIC_A_BLOCK_ITEM.get()) {
            tooltip.add(Component.translatable("info.woot.exotic.exotic_a", FactoryConfiguration.EXOTIC_A.get()));
        } else if (stack.getItem() == FactorySetup.EXOTIC_B_BLOCK_ITEM.get()) {
            tooltip.add(Component.translatable("info.woot.exotic.exotic_b", FactoryConfiguration.EXOTIC_B.get()));
        } else if (stack.getItem() == FactorySetup.EXOTIC_C_BLOCK_ITEM.get()) {
            tooltip.add(Component.translatable("info.woot.exotic.exotic_c", FactoryConfiguration.EXOTIC_C.get()));
        } else if (stack.getItem() == FactorySetup.EXOTIC_D_BLOCK_ITEM.get()) {
            tooltip.add(Component.translatable("info.woot.exotic.exotic_d", FactoryConfiguration.EXOTIC_D.get()));
        } else if (stack.getItem() == FactorySetup.EXOTIC_E_BLOCK_ITEM.get()) {
            tooltip.add(Component.translatable("info.woot.exotic.exotic_e", FactoryConfiguration.EXOTIC_E.get()));
        }
    }

    /**
     * WootDebug
     */

    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> ExoticBlock (" + exotic + ")");
        return debug;
    }
}
