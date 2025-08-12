package ipsis.woot.modules.fluidconvertor.client;

import com.mojang.blaze3d.platform.GlStateManager;
import ipsis.woot.Woot;
import ipsis.woot.modules.fluidconvertor.FluidConvertorConfiguration;
import ipsis.woot.modules.fluidconvertor.blocks.FluidConvertorMenu;
import ipsis.woot.modules.infuser.InfuserConfiguration;
import ipsis.woot.util.WootContainerScreen;
import ipsis.woot.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class FluidConvertorScreen extends WootContainerScreen<FluidConvertorMenu> {

    private ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/fluidconvertor.png");

    private static final int GUI_XSIZE = 180;
    private static final int GUI_YSIZE = 177;
    private static final int ENERGY_LX = 10;
    private static final int ENERGY_LY = 18;
    private static final int ENERGY_RX = 25;
    private static final int ENERGY_RY = 77;
    private static final int ENERGY_WIDTH = ENERGY_RX - ENERGY_LX + 1;
    private static final int ENERGY_HEIGHT = ENERGY_RY - ENERGY_LY + 1;

    private static final int IN_TANK_LX = 46;
    private static final int IN_TANK_LY = 18;
    private static final int IN_TANK_RX = 61;
    private static final int IN_TANK_RY = 77;
    private static final int IN_TANK_WIDTH = IN_TANK_RX - IN_TANK_LX + 1;
    private static final int IN_TANK_HEIGHT = IN_TANK_RY - IN_TANK_LY + 1;

    private static final int OUT_TANK_LX = 154;
    private static final int OUT_TANK_LY = 18;
    private static final int OUT_TANK_RX = 169;
    private static final int OUT_TANK_RY = 77;
    private static final int OUT_TANK_WIDTH = OUT_TANK_RX - OUT_TANK_LX + 1;
    private static final int OUT_TANK_HEIGHT = OUT_TANK_RY - OUT_TANK_LY + 1;

    private float currInputRender = 0;
    private float currOutputRender = 0;

    public FluidConvertorScreen(FluidConvertorMenu container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
        imageWidth = GUI_XSIZE;
        imageHeight = GUI_YSIZE;
        inventoryLabelY = imageHeight - 94;
        currInputRender = container.getInputFluid().getAmount();
        currOutputRender = container.getOutputFluid().getAmount();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if (RenderHelper.isPointInRegion(IN_TANK_LX, IN_TANK_LY, IN_TANK_WIDTH, IN_TANK_HEIGHT, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            renderFluidTankTooltip(guiGraphics, mouseX, mouseY, menu.getInputFluid(),
                    FluidConvertorConfiguration.FLUID_CONV_INPUT_TANK_CAPACITY.get());
        if (RenderHelper.isPointInRegion(OUT_TANK_LX, OUT_TANK_LY, OUT_TANK_WIDTH, OUT_TANK_HEIGHT, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            renderFluidTankTooltip(guiGraphics, mouseX, mouseY, menu.getOutputFluid(),
                    FluidConvertorConfiguration.FLUID_CONV_OUTPUT_TANK_CAPACITY.get());
        if (RenderHelper.isPointInRegion(ENERGY_LX, ENERGY_LY, ENERGY_WIDTH, ENERGY_HEIGHT, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            renderEnergyTooltip(guiGraphics, mouseX, mouseY, menu.getEnergy(),
                    InfuserConfiguration.INFUSER_MAX_ENERGY.get(), InfuserConfiguration.INFUSER_ENERGY_PER_TICK.get());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width - imageWidth) / 2;
        int relY = (this.height - imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, imageWidth, imageHeight);

        // Progress
        int progress = menu.getProgress();
        guiGraphics.blit(GUI,getGuiLeft() + 73, getGuiTop() + 39, 180, 0,(int)(72 * (progress / 100.0F)) , 28);

        renderEnergyBar(
                guiGraphics,
                ENERGY_LX,
                ENERGY_RY,
                ENERGY_HEIGHT,
                ENERGY_WIDTH,
                menu.getEnergy(), InfuserConfiguration.INFUSER_MAX_ENERGY.get());

        float renderDiff = menu.getInputFluid().getAmount() - currInputRender;
        if(renderDiff == 0){}
        else if (Math.abs(renderDiff) < 80){
            currInputRender = menu.getInputFluid().getAmount();
        }
        else{
            currInputRender += renderDiff*.05F;
        }

        renderFluidTank(
                guiGraphics,
                IN_TANK_LX + getGuiLeft(),
                IN_TANK_RY + getGuiTop(),
                IN_TANK_HEIGHT,
                IN_TANK_WIDTH,
                FluidConvertorConfiguration.FLUID_CONV_INPUT_TANK_CAPACITY.get(),
                menu.getInputFluid(),
                (int) currInputRender);

        renderDiff = menu.getOutputFluid().getAmount() - currOutputRender;
        if(renderDiff == 0){}
        else if (Math.abs(renderDiff) < 80){
            currOutputRender = menu.getOutputFluid().getAmount();
        }
        else{
            currOutputRender +=renderDiff*.05F;
        }

        renderFluidTank(
                guiGraphics,
                OUT_TANK_LX + getGuiLeft(),
                OUT_TANK_RY + getGuiTop(),
                OUT_TANK_HEIGHT,
                OUT_TANK_WIDTH,
                FluidConvertorConfiguration.FLUID_CONV_OUTPUT_TANK_CAPACITY.get(),
                menu.getOutputFluid(),
                (int) currOutputRender);
    }


}
