package ipsis.woot.jei;

import ipsis.woot.Woot;
import ipsis.woot.crafting.fluidconvertor.FluidConvertorRecipe;
import ipsis.woot.crafting.infuser.InfuserRecipe;
import ipsis.woot.modules.fluidconvertor.FluidConvertorConfiguration;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.infuser.InfuserConfiguration;
import ipsis.woot.modules.infuser.InfuserSetup;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FluidConvertorRecipeCategory implements IRecipeCategory<FluidConvertorRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "converting");
    public final static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/jei/fluidconvertor.png");

    public final static RecipeType<FluidConvertorRecipe> FLUID_CONVERTOR_TYPE = new RecipeType<>(UID, FluidConvertorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public FluidConvertorRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FluidConvertorSetup.FLUID_CONVERTOR_BLOCK.get()));
    }

    @Override
    public RecipeType<FluidConvertorRecipe> getRecipeType() {
        return FLUID_CONVERTOR_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Infuser");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidConvertorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 99, 21).addIngredients(recipe.catalyst());
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 18).addFluidStack(recipe.inputFluid().getFluid(), recipe.inputFluid().getAmount())
                .setFluidRenderer(10000, true, 16, 60);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 154, 18).addFluidStack(recipe.outputFluid().getFluid(), recipe.outputFluid().getAmount())
                .setFluidRenderer(10000, true, 16, 60);
    }

    @Override
    public void draw(FluidConvertorRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {

        RenderHelper.renderEnergyBar(guiGraphics, 10, 77, 60, 16, recipe.energy(), FluidConvertorConfiguration.FLUID_CONV_MAX_ENERGY.get());
        if(mouseX > 10 && mouseX < 26 && mouseY < 77 && mouseY > 18){
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(recipe.energy() + " RF"),(int) mouseX, (int)mouseY);
        }

    }
}
