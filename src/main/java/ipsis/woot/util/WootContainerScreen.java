package ipsis.woot.util;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
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
        drawFluid(guiGraphics, this.getRectangle().left() + x1, getRectangle().top() + y1 - h + 1, fluidStack, width,  h);
    }

    public void renderFluidTank(GuiGraphics guiGraphics, int x1, int y1, int height, int width, int max, FluidStack fluidStack)  {
        int filled = 0;
        if (max > 0)
            filled = fluidStack.getAmount() * 100 / max;
        filled = Math.clamp(filled, 0, 100);
        int h = filled * height / 100;
        drawFluid(guiGraphics, this.getRectangle().left() + x1, this.getRectangle().top() + y1 - h + 1, fluidStack, width,  h);
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

    public void renderHorizontalGauge(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int curr, int max, int color) {
        ScreenRectangle rect = this.getRectangle();
        guiGraphics.fill(rect.left() + x1, rect.top() + y1, rect.left() + x2, rect.top() + y2, color);

        if (max > 0) {
            int p = curr * (x2 - x1) / max;
            for (int i = 0; i < p; i++)
                guiGraphics.vLine(
                        rect.left() + x1 + 1 + i,
                        rect.top() + y1,
                        rect.top() + y2 - 1,
                        i % 2 == 0 ? color : 0xff000000);
        }
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

    public void drawFluid(GuiGraphics guiGraphics, int x, int y, FluidStack fluid, int width, int height) {

        if (fluid.getFluid() == Fluids.EMPTY)
            return;

        ResourceLocation stillTexture = IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture();
        // Get sprite from texture atlas
        TextureAtlas textureAtlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite sprite = textureAtlas.getSprite(stillTexture);

        // Get fluid color
        int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor();

        // Set color and render
        RenderSystem.setShaderColor(
                ((color >> 16) & 0xFF) / 255.0f,  // Red
                ((color >> 8) & 0xFF) / 255.0f,   // Green
                (color & 0xFF) / 255.0f,          // Blue
                ((color >> 24) & 0xFF) / 255.0f   // Alpha
        );

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        drawTiledTexture(guiGraphics, x, y, sprite, width, height);

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
        GlStateManager._clearColor(1.0F, 1.0F, 1.0F, 1.0F);
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

        guiGraphics.blit(
                icon.atlasLocation(),  // Resource location of the texture atlas
                x, y,                  // x, y position
                0,                     // z level (usually 0 for GUI elements)
                minU, minV,           // starting UV coordinates
                width, height,        // width and height to render
                icon.contents().width(), icon.contents().height()  // texture width and height
        );
    }
}
