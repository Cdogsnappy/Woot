package ipsis.woot.util;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import ipsis.woot.Woot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WootContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public WootContainerScreen(T menu, Inventory playerInventory, Component name) {
        super(menu, playerInventory, name);
    }

    /**
     * x1, y1 is the bottom right of the energy bar
     */
    public void renderEnergyBar(GuiGraphics guiGraphics, int x1, int y1, int height, int width, int curr, int max) {
        int filled = 0;
        if (max > 0)
            filled = curr * 100 / max;
        filled = Math.clamp(filled, 0, 100);
        int h = filled * height / 100;
        guiGraphics.fill(this.getGuiLeft() + x1,
                this.getGuiTop() + y1 - h + 1,
                this.getGuiLeft() + x1 + width,
                this.getGuiTop() + y1 + 1, 0xffff0000);
    }

    /**
     * x1, y1 is the bottom right of the fluid tank
     */
    public void renderFluidTank(GuiGraphics guiGraphics, int x1, int y1, int height, int width, int curr, int max, FluidStack fluidStack)  {
        int filled = 0;
        if (max > 0)
            filled = curr * 100 / max;
        filled = Math.clamp(filled, 0, 100);
        int h = filled * height / 100;
        drawFluid(guiGraphics, x1, y1 - h + 1, fluidStack, width,  h);
    }

    public void renderFluidTank(GuiGraphics guiGraphics, int x1, int y1, int height, int width, int max, FluidStack fluidStack)  {
        int filled = 0;
        if (max > 0)
            filled = fluidStack.getAmount() * 100 / max;
        filled = Math.clamp(filled, 0, 100);
        int h = filled * height / 100;
        drawFluid(guiGraphics, x1, y1 - h + 1, fluidStack, width,  h);
    }

    public void renderFluidTank(GuiGraphics guiGraphics, int x1, int y1, int height, int width, int max, FluidStack fluidStack, int amount)  {
        int filled = 0;
        if (max > 0)
            filled = amount * 100 / max;
        filled = Math.clamp(filled, 0, 100);
        int h = filled * height / 100;
        drawFluid(guiGraphics, x1, y1 - h + 1, fluidStack, width,  h);
    }

    public void renderHorizontalBar(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int curr, int max, int color) {
        int filled = 0;
        if (max > 0)
            filled = curr * max / 100;
        filled = Math.clamp(filled, 0, 100);
        int l = filled * (x2 - x1) / 100;
        ScreenRectangle rect = this.getRectangle();
        guiGraphics.fill(rect.left() + x1, rect.top() + y2,
                rect.left() + x2 + l, rect.top() + y2, color);
    }

    public void renderHorizontalGauge(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        guiGraphics.fill(getGuiLeft() + x1, getGuiTop() + y1, getGuiLeft() + x2, getGuiTop() + y2, color);

    }

    public void renderFluidTankTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, FluidStack fluidStack, int capacity) {
        List<Component> tooltip = new ArrayList<>();
        if (!fluidStack.isEmpty()) {
            tooltip.add(fluidStack.getHoverName());
            tooltip.add(Component.literal(String.format("%d/%d mb", fluidStack.getAmount(), capacity)));
        } else {
            tooltip.add(Component.literal(String.format("0/%d mb", capacity)));
        }
        guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
    }

    public void renderEnergyTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int curr, int capacity, int rate) {
        List<Component> tooltip = Arrays.asList(
                Component.literal(String.format("%d/%d RF", curr, capacity)),
                Component.literal( String.format("%d RF/tick", rate)));
        guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
    }

    public void drawFluid(GuiGraphics guiGraphics, int x, int y, FluidStack fluidStack, int width, int height) {

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

    private void setGLColorFromInt(int color) {
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        GlStateManager._clearColor(red, green, blue, alpha);
    }

    private void drawTiledTexture(GuiGraphics guiGraphics, int x, int y, TextureAtlasSprite icon, int width, int height) {
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

    public void drawScaledTexturedModelRectFromIcon(GuiGraphics guiGraphics, int x, int y, TextureAtlasSprite icon, int width, int height) {

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
}
