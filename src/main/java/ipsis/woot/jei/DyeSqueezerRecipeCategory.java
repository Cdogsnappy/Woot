package ipsis.woot.jei;

import ipsis.woot.Woot;
import ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipe;
import ipsis.woot.modules.fluidconvertor.FluidConvertorSetup;
import ipsis.woot.modules.squeezer.SqueezerConfiguration;
import ipsis.woot.modules.squeezer.SqueezerSetup;
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

public class DyeSqueezerRecipeCategory implements IRecipeCategory<DyeSqueezerRecipe> {

    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "dyesqueezing");
    public final static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/jei/squeezer.png");

    public final static RecipeType<DyeSqueezerRecipe> SQUEEZER_TYPE = new RecipeType<>(UID, DyeSqueezerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public DyeSqueezerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(SqueezerSetup.SQUEEZER_BLOCK.get()));
    }

    @Override
    public RecipeType<DyeSqueezerRecipe> getRecipeType() {
        return SQUEEZER_TYPE;
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
    public void setRecipe(IRecipeLayoutBuilder builder, DyeSqueezerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 40).addIngredients(recipe.input());


    }

    @Override
    public void draw(DyeSqueezerRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {

        RenderHelper.renderEnergyBar(guiGraphics, 10, 77, 60, 16, recipe.energy(), SqueezerConfiguration.DYE_SQUEEZER_MAX_ENERGY.get());
        if(mouseX > 10 && mouseX < 26 && mouseY < 77 && mouseY > 18){
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(recipe.energy() + " RF"),(int) mouseX, (int)mouseY);
        }

    }
}
