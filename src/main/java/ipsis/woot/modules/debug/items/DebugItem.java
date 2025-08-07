package ipsis.woot.modules.debug.items;

import ipsis.woot.util.WootDebug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;


import java.util.ArrayList;
import java.util.List;

public class DebugItem extends Item {

    public DebugItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            Block b =  context.getLevel().getBlockState(context.getClickedPos()).getBlock();
            if (b instanceof WootDebug) {
                List<String> debug = new ArrayList<>();
                ((WootDebug)b).getDebugText(debug, context);
                for (String s : debug)
                    context.getPlayer().sendSystemMessage(Component.literal(s));
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static List<String> getTileEntityDebug(List<String> debug, UseOnContext context) {
        BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
        if (te instanceof WootDebug)
            ((WootDebug) te).getDebugText(debug, context);
        return debug;
    }
}
