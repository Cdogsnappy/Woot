package ipsis.woot.modules.infuser.client;


import com.mojang.blaze3d.platform.GlStateManager;
import ipsis.woot.Woot;
import ipsis.woot.modules.infuser.InfuserConfiguration;
import ipsis.woot.modules.infuser.blocks.InfuserMenu;
import ipsis.woot.util.WootContainerScreen;
import ipsis.woot.util.helper.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class InfuserScreen extends WootContainerScreen<InfuserMenu> {

    private ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/infuser.png");

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

    public InfuserScreen(InfuserMenu container, Inventory playerInventory, Component name) {
        super(container, playerInventory, name);
        imageWidth = GUI_XSIZE;
        imageHeight = GUI_YSIZE;
        inventoryLabelY = imageHeight - 94;
    }

    

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if (RenderHelper.isPointInRegion(TANK_LX, TANK_LY, TANK_WIDTH, TANK_HEIGHT, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            renderFluidTankTooltip(guiGraphics, mouseX, mouseY, menu.getInputFluid(),
                    InfuserConfiguration.INFUSER_TANK_CAPACITY.get());
        if (RenderHelper.isPointInRegion(ENERGY_LX, ENERGY_LY, ENERGY_WIDTH, ENERGY_HEIGHT, mouseX, mouseY, getGuiLeft(), getGuiTop()))
            renderEnergyTooltip(guiGraphics, mouseX, mouseY, menu.getEnergy(),
                    InfuserConfiguration.INFUSER_MAX_ENERGY.get(), InfuserConfiguration.INFUSER_ENERGY_PER_TICK.get());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        // Progress
        int progress = menu.getProgress();
        guiGraphics.blit(GUI, getGuiLeft() + 90, getGuiTop() + 39, 180, 0,(int)(18 * (progress / 100.0F)) , 17);

        renderEnergyBar(
                guiGraphics,
                ENERGY_LX,
                ENERGY_RY,
                ENERGY_HEIGHT,
                ENERGY_WIDTH,
                menu.getEnergy(), InfuserConfiguration.INFUSER_MAX_ENERGY.get());

        renderFluidTank(
                guiGraphics,
                TANK_LX,
                TANK_RY,
                TANK_HEIGHT,
                TANK_WIDTH,
                InfuserConfiguration.INFUSER_TANK_CAPACITY.get(),
                menu.getInputFluid());

    }
}



