package ipsis.woot.modules.oracle.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import ipsis.woot.Woot;
import ipsis.woot.modules.oracle.blocks.OracleContainer;
import ipsis.woot.simulator.SimulatedMobDropSummary;
import ipsis.woot.util.FakeMob;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.item.ItemStack;


import java.awt.font.FontRenderContext;
import java.util.List;
import java.util.Optional;

import static net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft;

@OnlyIn(Dist.CLIENT)
public class OracleScreen extends AbstractContainerScreen<OracleContainer> {

    private ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/oracle.png");

    public OracleScreen(OracleContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = 180;
        imageHeight = 177;
    }


    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font,title, this.titleLabelX, this.titleLabelY, 4210752);

        if (menu.simulatedMobs.isEmpty()) {
            String mob = "N/A";
            guiGraphics.drawString(this.font, mob, (this.imageWidth / 2 - this.font.width(mob) / 2), 25, 4210752);
        } else {
            FakeMob fakeMob = menu.simulatedMobs.get(mobIndex);
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(fakeMob.getResourceLocation());
            if (entityType != null) {
                String mob = Component.literal(entityType.toShortString()).getString();
                if (fakeMob.hasTag())
                    mob += "[" + fakeMob.getTag() + "]";
                guiGraphics.drawString(this.font, mob, (this.imageWidth / 2 - this.font.width(mob) / 2), 25, 4210752);
            }
        }

        if (!menu.simulatedDrops.isEmpty()) {
            int currRow = 0;
            int currCol = 0;
            for (SimulatedMobDropSummary summary : menu.simulatedDrops) {

                int stackX = (currCol * 18) + 10;
                int stackY = (currRow * 18) + 41;


                if (mouseX - leftPos > stackX && mouseX - leftPos <= stackX + 20 && mouseY - topPos >= stackY && mouseY - topPos <= stackY + 20) {
                    Font fontRenderer = Minecraft.getInstance().font;
                    List<Component> tooltip = getTooltipFromItem(Minecraft.getInstance(), summary.stack());
                    tooltip.add(Component.translatable("gui.woot.oracle.looting.0", summary.chanceToDrop().getFirst()));
                    tooltip.add(Component.translatable("gui.woot.oracle.looting.1", summary.chanceToDrop().get(1)));
                    tooltip.add(Component.translatable("gui.woot.oracle.looting.2", summary.chanceToDrop().get(2)));
                    tooltip.add(Component.translatable("gui.woot.oracle.looting.3", summary.chanceToDrop().get(3)));
                    guiGraphics.renderTooltip(fontRenderer, tooltip, summary.stack().getTooltipImage(), summary.stack(), mouseX - leftPos, mouseY - topPos);
                    break;
                }

                currCol++;
                if (currCol == 9) {
                    currCol = 0;
                    currRow++;
                }
            }
        }
    }

    private Button nextMobButton;
    private Button prevMobButton;
    private int mobIndex = 0;

    @Override
    protected void init() {
        // This is called twice ?
        super.init(); // This sets leftPos/topPos

        this.nextMobButton = this.addButton(new Button(
                this.leftPos + 9 + (8 * 18),
                this.topPos + 18, 18, 18, new Component(">"),
                h -> {
                    if (!menu.simulatedMobs.isEmpty()) {
                        mobIndex = (mobIndex + 1);
                        mobIndex = MathHelper.clamp(mobIndex, 0, menu.simulatedMobs.size() - 1);
                        menu.refreshDrops(mobIndex);
                    }
                }));

        this.prevMobButton = this.addButton(new Button(
                this.leftPos + 9,
                this.topPos + 18, 18, 18, new StringTextComponent("<"),
                h -> {
                    if (!menu.simulatedMobs.isEmpty()) {
                        mobIndex = (mobIndex - 1);
                        mobIndex = MathHelper.clamp(mobIndex, 0, menu.simulatedMobs.size() - 1);
                        menu.refreshDrops(mobIndex);
                    }
                }));

        menu.refreshMobs();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        getMinecraft().getTextureManager().getTexture(GUI).bind();
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY,0, 0.0F, 0.0F, imageWidth, imageHeight, 32, 32);

        if (!menu.simulatedDrops.isEmpty()) {
            int currRow = 0;
            int currCol = 0;
            for (SimulatedMobDropSummary summary : menu.simulatedDrops) {

                int stackX = leftPos + (currCol * 18) + 10;
                int stackY = topPos + (currRow * 18) + 41;

                guiGraphics.renderItem(summary.stack(), stackX, stackY);

                currCol++;
                if (currCol == 9) {
                    currCol = 0;
                    currRow++;
                }
            }
        }
    }
}
