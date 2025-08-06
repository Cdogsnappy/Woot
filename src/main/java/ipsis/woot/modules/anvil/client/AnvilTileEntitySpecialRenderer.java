package ipsis.woot.modules.anvil.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import ipsis.woot.modules.anvil.blocks.AnvilBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class AnvilTileEntitySpecialRenderer implements BlockEntityRenderer<AnvilBlockEntity> {



    private void renderStack(ItemStack itemStack, PoseStack poseStack, MultiBufferSource multiBufferSource, double x, double y, double z, int combinedLight, int combinedOverlay) {
        float scale = 0.20F;
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        Minecraft.getInstance().getItemRenderer().render(itemStack, ItemDisplayContext.FIXED, false, poseStack, multiBufferSource, combinedLight, combinedOverlay, Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0));
        poseStack.popPose();
    }

    @Override
    public void render(AnvilBlockEntity anvilBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, int combinedOverlay) {

        ItemStack itemStack = anvilBlockEntity.getBaseItem();
        if (!itemStack.isEmpty()) {
            renderStack(itemStack, poseStack, multiBufferSource, 0.5F, 1.05F, 0.5F, combinedLight, combinedOverlay);
        }

        ItemStack[] ingredients = anvilBlockEntity.getIngredients();
        if (!ingredients[0].isEmpty())
            renderStack(ingredients[0],  poseStack, multiBufferSource, 0.5F, 1.05F, 0.5F - 0.2F, combinedLight, combinedOverlay);
        if (!ingredients[1].isEmpty())
            renderStack(ingredients[1],  poseStack, multiBufferSource, 0.5F, 1.05F, 0.5F + 0.2F, combinedLight, combinedOverlay);
        if (!ingredients[2].isEmpty())
            renderStack(ingredients[2],  poseStack, multiBufferSource, 0.5F, 1.05F, 0.5F - 0.4F, combinedLight, combinedOverlay);
        if (!ingredients[3].isEmpty())
            renderStack(ingredients[3],  poseStack, multiBufferSource, 0.5F, 1.05F, 0.5F + 0.4F, combinedLight, combinedOverlay);

    }
}
