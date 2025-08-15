package ipsis.woot.jei;

import ipsis.woot.Woot;
import ipsis.woot.crafting.anvil.AnvilRecipe;
import ipsis.woot.crafting.infuser.InfuserRecipe;
import ipsis.woot.modules.anvil.AnvilSetup;
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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class InfuserRecipeCategory implements IRecipeCategory<InfuserRecipe> {
    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "infusing");
    public final static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/jei/infuser.png");

    public final static RecipeType<InfuserRecipe> INFUSER_TYPE = new RecipeType<>(UID, InfuserRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public InfuserRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(InfuserSetup.INFUSER_BLOCK.get()));
    }

    @Override
    public RecipeType<InfuserRecipe> getRecipeType() {
        return INFUSER_TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, InfuserRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 40).addIngredients(recipe.ingredient());
        builder.addSlot(RecipeIngredientRole.INPUT, 64, 40).addIngredients(recipe.augment());
        builder.addSlot(RecipeIngredientRole.INPUT, 154, 18).addFluidStack(recipe.fluid().getFluid(), recipe.fluid().getAmount())
                .setFluidRenderer(10000, true, 16, 60);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 118, 40).addItemStack(recipe.result());
    }

    @Override
    public void draw(InfuserRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {

        RenderHelper.renderEnergyBar(guiGraphics, 10, 77, 60, 16, recipe.energy(), InfuserConfiguration.INFUSER_MAX_ENERGY.get());
        if(mouseX > 10 && mouseX < 26 && mouseY < 77 && mouseY > 18){
            guiGraphics.renderTooltip(Minecraft.getInstance().font, (Component) Component.literal(recipe.energy() + " RF"),(int) mouseX, (int)mouseY);
        }

    }
}
