package ipsis.woot.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class RenderHelper {

    public static void renderFluidTank(GuiGraphics guiGraphics, int x1, int y1, int height, int width, int max, FluidStack fluidStack)  {
        int filled = 0;
        if (max > 0)
            filled = fluidStack.getAmount() * 100 / max;
        filled = Math.clamp(filled, 0, 100);
        int h = filled * height / 100;
        drawFluid(guiGraphics, x1, y1 - h + 1, fluidStack, width,  h);
    }

    public static void drawFluid(GuiGraphics guiGraphics, int x, int y, FluidStack fluidStack, int width, int height) {

        if (fluidStack.getFluid() == Fluids.EMPTY)
            return;

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation sprite = clientFluid.getStillTexture(fluidStack);
        ResourceLocation sprite_flowing = clientFluid.getFlowingTexture(fluidStack);

        TextureAtlasSprite fluidTexture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(sprite);

        // Get fluid color
        int color = clientFluid.getTintColor();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Set color

        // Set color and render
        RenderSystem.setShaderColor(
                ((color >> 16) & 0xFF) / 255.0f,  // Red
                ((color >> 8) & 0xFF) / 255.0f,   // Green
                (color & 0xFF) / 255.0f,          // Blue
                ((color >> 24) & 0xFF) / 255.0f   // Alpha
        );

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        drawTiledTexture(guiGraphics, x, y, fluidTexture, width, height);

        // Reset color
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void drawTiledTexture(GuiGraphics guiGraphics, int x, int y, TextureAtlasSprite icon, int width, int height) {
        int i;
        int j;
        int drawHeight;
        int drawWidth;

        for (i = 0; i < width; i += 16) {
            for (j = 0; j < height; j += 16) {
                drawWidth = Math.min(width - i, 16);
                drawHeight = Math.min(height - j, 16);
                drawScaledTexturedModelRectFromIcon(guiGraphics,x + i, y + j, icon, drawWidth, drawHeight);
            }
        }
    }

    public static void drawScaledTexturedModelRectFromIcon(GuiGraphics guiGraphics, int x, int y, TextureAtlasSprite icon, int width, int height) {

        if (icon == null) {
            return;
        }
        float minU = icon.getU0();
        float maxU = icon.getU1();
        float minV = icon.getV0();
        float maxV = icon.getV1();

        float u1 = minU + (maxU - minU) * width / 16F;
        float v1 = minV + (maxV - minV) * height / 16F;

        guiGraphics.blit(x,y,0,width,height,icon);
    }

    /**
     * x1, y1 is the bottom right of the energy bar
     */
    public static void renderEnergyBar(GuiGraphics guiGraphics, int x1, int y1, int height, int width, int curr, int max) {
        int filled = 0;
        if (max > 0)
            filled = curr * 100 / max;
        filled = Math.clamp(filled, 0, 100);
        int h = filled * height / 100;
        guiGraphics.fill( x1,
                 y1 - h + 1,
                 x1 + width,
                 y1 + 1, 0xffff0000);
    }
}
