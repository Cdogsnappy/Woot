package ipsis.woot.modules.squeezer.client;

import com.mojang.blaze3d.platform.GlStateManager;
import ipsis.woot.Woot;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.modules.squeezer.SqueezerConfiguration;
import ipsis.woot.modules.squeezer.blocks.DyeSqueezerContainer;
import ipsis.woot.util.WootContainerScreen;
import ipsis.woot.util.helper.RenderHelper;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;


@OnlyIn(Dist.CLIENT)
public class DyeSqueezerScreen extends WootContainerScreen<DyeSqueezerContainer> {

    private ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/squeezer.png");
    private static final int GUI_XSIZE = 180;
    private static final int GUI_YSIZE = 177;

    private static final int ENERGY_LX = 10;
    private static final int ENERGY_LY = 18;
    private static final int ENERGY_RX = 25;
    private static final int ENERGY_RY = 77;
    private static final int ENERGY_WIDTH = ENERGY_RX - ENERGY_LX + 1;
    private static final int ENERGY_HEIGHT = ENERGY_RY - ENERGY_LY + 1;

    private static final int TANK_LX = 154;
    private static final int TANK_LY = 18;
    private static final int TANK_RX = 169;
    private static final int TANK_RY = 77;
    private static final int TANK_WIDTH = TANK_RX - TANK_LX + 1;
    private static final int TANK_HEIGHT = TANK_RY - TANK_LY + 1;

    public DyeSqueezerScreen(DyeSqueezerContainer menu, Inventory playerInventory, Component name) {
        super(menu, playerInventory, name);
        imageWidth = GUI_XSIZE;
        imageHeight = GUI_YSIZE;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if (RenderHelper.isPointInRegion(82, 30, 51, 8, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            guiGraphics.renderTooltip(font, Component.translatable(
                          "gui.woot.squeezer.red",
                        menu.getRedDyeAmount(),
                        SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get()),
                    mouseX, mouseY);
        if (RenderHelper.isPointInRegion(82, 40, 51, 8, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            guiGraphics.renderTooltip(font,  Component.translatable(
                    "gui.woot.squeezer.yellow",
                    menu.getYellowDyeAmount(),
                    SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get()),
                    mouseX, mouseY);
        if (RenderHelper.isPointInRegion(82, 50, 51, 8, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            guiGraphics.renderTooltip(font, Component.translatable(
                    "gui.woot.squeezer.blue",
                        menu.getBlueDyeAmount(),
                        SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get()),
                    mouseX, mouseY);
        if (RenderHelper.isPointInRegion(82, 60, 51, 8, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            guiGraphics.renderTooltip(font, Component.translatable(
                    "gui.woot.squeezer.white",
                    menu.getWhiteDyeAmount(),
                    SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get()),
                    mouseX, mouseY);
        if (RenderHelper.isPointInRegion(TANK_LX, TANK_LY, TANK_WIDTH, TANK_HEIGHT, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            renderFluidTankTooltip(guiGraphics, mouseX, mouseY,
                    menu.getPureDye(),
                    SqueezerConfiguration.DYE_SQUEEZER_TANK_CAPACITY.get());
        if (RenderHelper.isPointInRegion(ENERGY_LX, ENERGY_LY, ENERGY_WIDTH, ENERGY_HEIGHT, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            renderEnergyTooltip(guiGraphics, mouseX, mouseY, menu.getEnergy(),
                    SqueezerConfiguration.DYE_SQUEEZER_MAX_ENERGY.get(), 10);
    }
    

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        String text2 = menu.getDumpExcess() ?
                StringHelper.translate("gui.woot.squeezer.dump") :
                StringHelper.translate("gui.woot.squeezer.strict");
        guiGraphics.drawString(font, text2,82, 70, 4210752);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, imageWidth, imageHeight);

        // Progress
        int progress = menu.getProgress();
        guiGraphics.blit(GUI, getGuiLeft() + 58, getGuiTop() + 30, 180, 0,(int)(19 * (progress / 100.0F)) , 40);

        // NB: The tanks will change the texture so progress has to be above that or rebind the texture
        renderHorizontalGauge(guiGraphics, 82, 30, 132, 37,
                menu.getRedDyeAmount(), SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get(),
                0xff000000 | DyeColor.RED.getMapColor().col);
        renderHorizontalGauge(guiGraphics, 82, 40, 132, 47,
                menu.getYellowDyeAmount(), SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get(),
                0xff000000 | DyeColor.YELLOW.getMapColor().col);
        renderHorizontalGauge(guiGraphics, 82, 50, 132, 57,
                menu.getBlueDyeAmount(), SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get(),
                0xff000000 | DyeColor.BLUE.getMapColor().col);
        renderHorizontalGauge(guiGraphics, 82, 60, 132, 67,
                menu.getWhiteDyeAmount(), SqueezerConfiguration.DYE_SQUEEZER_INTERNAL_FLUID_MAX.get(),
                0xff000000 | DyeColor.WHITE.getMapColor().col);

        renderEnergyBar(
                guiGraphics,
                ENERGY_LX,
                ENERGY_RY,
                ENERGY_HEIGHT,
                ENERGY_WIDTH,
                menu.getEnergy(), SqueezerConfiguration.DYE_SQUEEZER_MAX_ENERGY.get());

        renderFluidTank(
                guiGraphics,
                TANK_LX,
                TANK_RY,
                TANK_HEIGHT,
                TANK_WIDTH,
                SqueezerConfiguration.DYE_SQUEEZER_TANK_CAPACITY.get(),
                menu.getPureDye());
    }
}
