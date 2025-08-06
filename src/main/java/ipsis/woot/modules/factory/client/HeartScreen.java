package ipsis.woot.modules.factory.client;


import com.mojang.blaze3d.platform.GlStateManager;
import ipsis.woot.Woot;
import ipsis.woot.fluilds.FluidSetup;
import ipsis.woot.modules.factory.*;
import ipsis.woot.modules.factory.blocks.ControllerBlockEntity;
import ipsis.woot.modules.factory.blocks.HeartMenu;
import ipsis.woot.modules.factory.items.PerkItem;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.setup.NetworkChannel;
import ipsis.woot.setup.ServerDataRequest;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.WootContainerScreen;
import ipsis.woot.util.helper.RenderHelper;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * There are two types of information displayed here.
 * Static - the factory recipe and drops - custom packet
 * Dynamic - progress - vanilla progress mechanism
 */

@OnlyIn(Dist.CLIENT)
public class HeartScreen extends WootContainerScreen<HeartMenu> {

    private ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/heart.png");

    public HeartScreen(HeartMenu menu, Inventory pInventory, Component name) {
        super(menu, pInventory, name);
        imageHeight = GUI_HEIGHT;
        imageWidth = GUI_WIDTH;
    }

    private List<GuiItemStackElement> dropElements = new ArrayList<>();
    private List<GuiItemStackElement> mobElements = new ArrayList<>();
    private List<GuiItemStackElement> upgradeElements = new ArrayList<>();
    private List<GuiStackElement> recipeElements = new ArrayList<>();
    private StackElement exoticElement = new StackElement(EXOTIC_X, EXOTIC_Y);

    private int GUI_WIDTH = 252;
    private int GUI_HEIGHT = 222;
    private int DROPS_COLS = 13;
    private int DROPS_ROWS = 4;
    private int DROPS_X = 10;
    private int DROPS_Y = 144;
    private int MOBS_X = 10;
    private int MOBS_Y = 76;
    private int PERKS_X = 99;
    private int PERKS_Y = 76;
    private int RECIPE_X = 10;
    private int RECIPE_Y = 110;
    private float DROP_CYCLE_MS = 5000.0F;
    private int TEXT_COLOR = 4210752;
    private static final int TANK_LX = 226;
    private static final int TANK_LY = 8;
    private static final int TANK_RX = 241;
    private static final int TANK_RY = 91;
    private static int EXOTIC_X = 190;
    private static int EXOTIC_Y = 76;

    private long renderTime;

    @Override
    protected void init() {
        super.init();

        // Mobs
        for (int i = 0; i < 4; i++)
            mobElements.add(new GuiItemStackElement(MOBS_X + (i * 18), MOBS_Y, true));

        // Upgrades
        for (int i = 0; i < 4; i++)
            upgradeElements.add(new GuiItemStackElement(PERKS_X + (i * 18), PERKS_Y, true));

        // Recipe

        // Drops
        for (int row = 0; row < DROPS_ROWS; row++) {
            for (int col = 0; col < DROPS_COLS; col++) {
                dropElements.add(new GuiItemStackElement(DROPS_X + (col * 18), DROPS_Y + (row * 18)));
            }
        }

        // Request the static data
        PacketDistributor.sendToServer(new ServerDataRequest("",
                ((HeartMenu)this.menu).getPos(),
                ServerDataRequest.Type.HEART_STATIC_DATA.ordinal()));
    }

    private int getCapacity() {
        int capacity = 0;
        HeartMenu heartMenu = (HeartMenu)menu;
        if (heartMenu.getCellType() == 0)
            capacity = FactoryConfiguration.CELL_1_CAPACITY.get();
        else if (heartMenu.getCellType() == 1)
            capacity = FactoryConfiguration.CELL_2_CAPACITY.get();
        else if (heartMenu.getCellType() == 2)
            capacity = FactoryConfiguration.CELL_3_CAPACITY.get();
        else if (heartMenu.getCellType() == 3)
            capacity = FactoryConfiguration.CELL_4_CAPACITY.get();
        return capacity;
    }

    /**
     * ContainerScreen render
     *
     * drawGuiContainerBackgroundLayer
     *     draw slots and itemstacks
     *     gradient fill active slot to highlight
     * drawGuiContainerForegroundLayer
     *     draw dragged itemstack
     *
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        renderFg(guiGraphics, mouseX, mouseY);



        if (renderTime == 0L)
            renderTime = Util.getMillis();

        /*
        if (Util.milliTime() - renderTime > DROP_CYCLE_MS) {
            dropElements.forEach(e -> e.cycle());
            renderTime = Util.milliTime();
        } */

        ScreenRectangle rect = this.getRectangle();

        if (mouseX > getGuiLeft() + TANK_LX && mouseX < getGuiLeft() + TANK_RX && mouseY > getGuiTop() + TANK_LY && mouseY < getGuiTop() + TANK_RY)
            renderFluidTankTooltip(guiGraphics, mouseX, mouseY,
                    menu.getInputFluid(), getCapacity());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        int relX = (width - imageWidth) / 2;
        int relY = (height - imageHeight) / 2;
        guiGraphics.blit(GUI, relX, relY,0, 0.0F, 0.0F, width, height, GUI_WIDTH, GUI_HEIGHT);

        mobElements.forEach(e -> e.drawBackground(guiGraphics, mouseX, mouseY));
        upgradeElements.forEach(e -> e.drawBackground(guiGraphics, mouseX, mouseY));
        dropElements.forEach(e -> e.drawBackground(guiGraphics, mouseX, mouseY));
        recipeElements.forEach(e -> e.drawBackground(guiGraphics, mouseX, mouseY));
        exoticElement.drawBackground(mouseX, mouseY);

        renderFluidTank(
                guiGraphics,
                TANK_LX,
                TANK_RY,
                TANK_RY - TANK_LY + 1,
                TANK_RX - TANK_LX + 1,
                getCapacity(),
                menu.getInputFluid());
    }

    /**
     * 0,0 is top left hand corner of the gui texture
     */
    private boolean sync = false;

    /**
     * return true if unique drop added
     */
    private boolean addToDropElements(int idx, FakeMob fakeMob, ItemStack itemStack) {
        List<Component> tooltip = getTooltipFromItem(getMinecraft(), itemStack);
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(fakeMob.getResourceLocation());
        if (entityType != null) {
            Component iTextComponent =Component.translatable(entityType.getDescriptionId());
            tooltip.add(Component.literal(String.format("%s : %.2f%%",
                    iTextComponent.getString(), itemStack.getCount() / 100.0F)));
        }

        boolean found = false;
        for (GuiItemStackElement guiItemStackElement : dropElements) {
            if (guiItemStackElement.itemStack.is(itemStack.getItem())) {
                guiItemStackElement.addToolTip(tooltip);
                found = true;
                break;
            }
        }
        if (!found) {
            dropElements.get(idx).setItemStack(itemStack);
            dropElements.get(idx).addToolTip(tooltip);
            return true;
        }
        return false;
    }


    protected void renderFg(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        ClientFactorySetup clientFactorySetup = ((HeartMenu)menu).getTileEntity().clientFactorySetup;
        if (clientFactorySetup == null)
            return;

        if (!sync) {
            int idx = 0;

            // Drops
            List<FakeMob> knownMobs = new ArrayList<>();
            for (FakeMob fakeMob : clientFactorySetup.controllerMobs) {
                if (knownMobs.contains(fakeMob))
                    continue;

                knownMobs.add(fakeMob);
                ClientFactorySetup.Mob mobInfo = clientFactorySetup.mobInfo.get(fakeMob);
                for (ItemStack itemStack : mobInfo.drops()) {
                    if (addToDropElements(idx, fakeMob, itemStack))
                        idx = (idx + 1) % dropElements.size();
                }
            }

            if (clientFactorySetup.tier != Tier.TIER_1) {
                for (int i = 0; i < 4; i++) {
                    mobElements.get(i).unlock();
                    upgradeElements.get(i).unlock();
                }
            } else {
                mobElements.get(0).unlock();
                upgradeElements.get(0).unlock();
            }

            idx = 0;
            for (FakeMob fakeMob : clientFactorySetup.controllerMobs) {
                ItemStack controllerStack = ControllerBlockEntity.getItemStack(fakeMob);
                List<Component> tooltip = getTooltipFromItem(getMinecraft(), controllerStack);
                mobElements.get(idx).setItemStack(controllerStack);
                mobElements.get(idx).addToolTip(tooltip);
                mobElements.get(idx).unlock();
                idx++;
            }

            idx = 0;
            for (Perk perk : clientFactorySetup.perks) {
                ItemStack itemStack = PerkItem.getItemStack(perk);
                List<Component> tooltip = getTooltipFromItem(getMinecraft(), itemStack);
                for (FakeMob fakeMob : clientFactorySetup.controllerMobs) {
                    MobParam mobParam = clientFactorySetup.mobParams.get(fakeMob);
                    EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(fakeMob.getResourceLocation());
                    Component iTextComponent = Component.translatable(entityType.getDescriptionId());
                    if (Perk.EFFICIENCY_PERKS.contains(perk))
                        tooltip.add(Component.literal(String.format("%s : %d%%", iTextComponent.getString(), mobParam.getPerkEfficiencyValue())));
                    else if (Perk.RATE_PERKS.contains(perk))
                        tooltip.add(Component.literal(String.format("%s : %d%%", iTextComponent.getString(), mobParam.getPerkRateValue())));
                    else if (Perk.MASS_PERKS.contains(perk))
                        tooltip.add(Component.literal(
                                String.format("%s : %d mobs",
                                        iTextComponent.getString(),
                                        mobParam.getMobCount(true, clientFactorySetup.exotic == Exotic.EXOTIC_E))));
                    else if (Perk.XP_PERKS.contains(perk))
                        tooltip.add(Component.literal(String.format("%s : %d%%", iTextComponent.getString(), mobParam.getPerkXpValue())));
                }
                if (Perk.TIER_SHARD_PERKS.contains(perk)) {
                    tooltip.add(Component.literal(String.format("%d rolls @ %.2f%%", clientFactorySetup.shardRolls, clientFactorySetup.shardDropChance)));
                    tooltip.add(Component.literal(String.format("Basic: %.2f%%", clientFactorySetup.shardDrops[0])));
                    tooltip.add(Component.literal(String.format("Advanced: %.2f%%", clientFactorySetup.shardDrops[1])));
                    tooltip.add(Component.literal(String.format("Elite: %.2f%%", clientFactorySetup.shardDrops[2])));
                }
                if (clientFactorySetup.perkCapped) {
                    Component iTextComponent1 = Component.translatable(clientFactorySetup.tier.getTranslationKey());
                    Component iTextComponent2 = Component.translatable("gui.woot.heart.8");
                    tooltip.add(Component.literal(ChatFormatting.RED + String.format("%s : %s", iTextComponent2.getString(), iTextComponent1.getString())));
                }
                upgradeElements.get(idx).setItemStack(itemStack);
                upgradeElements.get(idx).addToolTip(tooltip);
                upgradeElements.get(idx).unlock();
                idx = (idx + 1) % upgradeElements.size();
            }

            idx = 0;
            for (ItemStack itemStack : clientFactorySetup.itemIng) {
                GuiItemStackElement stackElement = new GuiItemStackElement(RECIPE_X + (idx * 18), RECIPE_Y);
                List<Component> tooltip = getTooltipFromItem(getMinecraft(), itemStack);
                tooltip.add(Component.literal(String.format("%d items", itemStack.getCount())));
                stackElement.setItemStack(itemStack.copy());
                stackElement.addToolTip(tooltip);
                recipeElements.add(stackElement);
                idx++;
            }
            for (FluidStack fluidStack : clientFactorySetup.fluidIng) {
                GuiFluidStackElement stackElement = new GuiFluidStackElement(RECIPE_X + (idx * 18), RECIPE_Y);
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(fluidStack.getHoverName());
                tooltip.add(Component.literal(String.format("%d mb", fluidStack.getAmount())));
                stackElement.setStack(fluidStack.copy());
                stackElement.addTooltip(tooltip);
                recipeElements.add(stackElement);
                idx++;
            }

            sync = true;
        }

        if (clientFactorySetup.tier != Tier.TIER_5) {
            exoticElement.isLocked = true;
        } else {
            if (clientFactorySetup.exotic != Exotic.NONE) {
                List<Component> tooltip = getTooltipFromItem(getMinecraft(), clientFactorySetup.exotic.getItemStack());
                exoticElement.addDrop(clientFactorySetup.exotic.getItemStack(), tooltip);
            }
        }

        addInfoLine(guiGraphics, 0, StringHelper.translate("gui.woot.heart.0"), title.getString());
        addInfoLine(guiGraphics, 1, StringHelper.translate(
                new FluidStack(FluidSetup.CONATUS_FLUID.get(), 1).getDescriptionId()), clientFactorySetup.recipeFluid + " mB");
        addInfoLine(guiGraphics, 2,StringHelper.translate("gui.woot.heart.1"), clientFactorySetup.recipeTicks + " ticks");
        addInfoLine(guiGraphics, 3, StringHelper.translate("gui.woot.heart.2"), ((HeartMenu)menu).getProgress() + "%");

        guiGraphics.drawString(font, StringHelper.translate("gui.woot.heart.3"), MOBS_X, MOBS_Y - 10, TEXT_COLOR);
        guiGraphics.drawString(font, StringHelper.translate("gui.woot.heart.4"), PERKS_X, PERKS_Y - 10, TEXT_COLOR);
        guiGraphics.drawString(font, StringHelper.translate("gui.woot.heart.5"), DROPS_X, DROPS_Y - 10, TEXT_COLOR);
        guiGraphics.drawString(font, StringHelper.translate("gui.woot.heart.6"), RECIPE_X, RECIPE_Y - 10, TEXT_COLOR);
        guiGraphics.drawString(font, StringHelper.translate("gui.woot.heart.7"), EXOTIC_X, EXOTIC_Y - 10, TEXT_COLOR);

        mobElements.forEach(e -> e.drawForeground(guiGraphics, mouseX, mouseY));
        upgradeElements.forEach(e -> e.drawForeground(guiGraphics, mouseX, mouseY));
        recipeElements.forEach(e -> e.drawForeground(guiGraphics, mouseX, mouseY));
        dropElements.forEach(e -> e.drawForeground(guiGraphics, mouseX, mouseY));
        exoticElement.drawForeground(guiGraphics, mouseX, mouseY);
    }

    private void addInfoLine(GuiGraphics guiGraphics, int offset, String tag, String value) {
        int INFO_X = 10;
        int INFO_Y = 10;
        int TEXT_HEIGHT = 10;
        guiGraphics.drawString(font, tag, INFO_X, INFO_Y + (TEXT_HEIGHT * offset), TEXT_COLOR);
        guiGraphics.drawString(font, value, INFO_X + 80, INFO_Y + (TEXT_HEIGHT * offset), TEXT_COLOR);
    }


    class StackElement {
        int x;
        int y;
        boolean isLocked = false;
        int idx = 0;
        List<ItemStack> itemStacks = new ArrayList<>();
        List<List<Component>> tooltips = new ArrayList<>();
        public StackElement(int x, int y, boolean locked) {
            this.x = x;
            this.y = y;
            this.isLocked = locked;
        }

        public StackElement(int x, int y) {
            this(x, y, false);
        }

        public void addDrop(ItemStack itemStack, List<Component> tooltip) {
            isLocked = false;
            itemStacks.add(itemStack);
            tooltips.add(tooltip);
        }

        public void unlock() { isLocked = false; }

        public void cycle() {
            if (!itemStacks.isEmpty())
                idx = (idx + 1) % itemStacks.size();
        }

        public void drawBackground(int mouseX, int mouseY) {
            if (isLocked)
                return;

            if (itemStacks.isEmpty())
                return;
        }

        public void drawTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY)  {
            if (isLocked)
                return;

            if (itemStacks.isEmpty())
                    return;

            ItemStack itemStack = itemStacks.get(idx);
            List<Component> tooltip = tooltips.get(idx);
            if (RenderHelper.isPointInRegion(x, y, 16, 16, mouseX, mouseY, getRectangle().left(),getRectangle().top())) {
                guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
            }
        }


        public void drawForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {

            if (isLocked) {
                guiGraphics.fill( x - 1, y - 1, x - 1 + 18, y - 1 + 18, -2130706433);
                return;
            }

            if (itemStacks.isEmpty())
                return;

            ItemStack itemStack = itemStacks.get(idx);
            guiGraphics.renderItem(itemStack, x, y, 100, 100);
        }
    }

    class GuiStackElement {
        int posX;
        int posY;
        boolean isLocked;

        public GuiStackElement() {
            isLocked = false;
        }

        public GuiStackElement(int posX, int posY) {
            this();
            this.posX = posX;
            this.posY = posY;
        }

        public GuiStackElement(int posX, int posY, boolean isLocked) {
            this(posX, posY);
            this.isLocked = isLocked;
        }

        public void unlock() {
            isLocked = false;
        }

        public void lock() {
            isLocked = true;
        }

        public void drawBackground(GuiGraphics guiGraphics, int mouseX, int mouseY) {}
        public void drawForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (isLocked)
                guiGraphics.fill(posX - 1, posY - 1, posX - 1 + 18, posY - 1 + 18, -2130706433);
        }

        public void drawTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        }
    }

    public class GuiItemStackElement extends GuiStackElement {
        private ItemStack itemStack;
        List<Component> tooltip = new ArrayList<>();

        public GuiItemStackElement(int posX, int posY, boolean isLocked) {
            super(posX, posY, isLocked);
            this.itemStack = ItemStack.EMPTY;
        }

        public GuiItemStackElement(int posX, int posY) {
            super(posX, posY);
            this.itemStack = ItemStack.EMPTY;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack.copy();
        }

        public void addToolTip(List<Component> tooltip) {
            this.tooltip.addAll(tooltip);
        }

        @Override
        public void drawBackground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (isLocked || itemStack.isEmpty())
                return;
        }

        @Override
        public void drawForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            super.drawForeground(guiGraphics, mouseX, mouseY);

            if (itemStack.isEmpty())
                return;


            guiGraphics.renderItem(itemStack, posX, posY, 100, 100);
        }

        @Override
        public void drawTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (isLocked || itemStack.isEmpty() || tooltip.isEmpty())
                return;

            if (!RenderHelper.isPointInRegion(posX, posY, 16, 16, mouseX, mouseY, getRectangle().left(),getRectangle().top()))
                return;

            guiGraphics.renderTooltip(font,tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    public class GuiFluidStackElement extends GuiStackElement {

        private FluidStack fluidStack;
        private List<Component> tooltip = new ArrayList<>();

        public GuiFluidStackElement(int posX, int posY, boolean isLocked) {
            super(posX, posY, isLocked);
            this.fluidStack = FluidStack.EMPTY;
        }

        public GuiFluidStackElement(int posX, int posY) {
            super(posX, posY);
            this.fluidStack = FluidStack.EMPTY;
        }

        public void setStack(FluidStack fluidStack) {
            this.fluidStack = fluidStack.copy();
        }

        public void addTooltip(List<Component> tooltip) {
            this.tooltip.addAll(tooltip);
        }

        @Override
        public void drawBackground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (isLocked || fluidStack.isEmpty())
                return;

            drawFluid(guiGraphics, getRectangle().left() + posX, getRectangle().top() + posY, fluidStack, 16, 16);
        }

        @Override
        public void drawForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            super.drawForeground(guiGraphics, mouseX, mouseY);

        }

        @Override
        public void drawTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            if (isLocked || fluidStack.isEmpty() || tooltip.isEmpty())
                return;

            if (!RenderHelper.isPointInRegion(posX, posY, 16, 16, mouseX, mouseY, getRectangle().left(),getRectangle().top()))
                return;

            guiGraphics.renderTooltip(font, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }
}
