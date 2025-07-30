package ipsis.woot.modules.factory.items;

import ipsis.woot.config.Config;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


import javax.annotation.Nullable;
import java.util.List;

public class ControllerBlockItem extends BlockItem {

    public ControllerBlockItem(Block block, Item.Properties builder) {
        super(block, builder);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null && data.contains(ModNBT.Controller.MOB_TAG)) {
            FakeMob fakeMob = new FakeMob(data.copyTag().getCompound(ModNBT.Controller.MOB_TAG));
            if (fakeMob.isValid()) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(fakeMob.getResourceLocation());
                tooltip.add(Component.literal(entityType.getDescriptionId()));
                if (fakeMob.hasTag())
                    tooltip.add(Component.literal("[" + fakeMob.getTag() + "]"));

                tooltip.add(Component.literal("info.woot.controller.0"));
            }
        }
    }
}
