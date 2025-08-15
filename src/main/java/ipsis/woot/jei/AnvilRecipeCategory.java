package ipsis.woot.jei;

import ipsis.woot.Woot;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.anvil.AnvilRecipe;
import ipsis.woot.modules.anvil.AnvilSetup;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnvilRecipeCategory implements IRecipeCategory<AnvilRecipe> {

    public final static ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Woot.MODID, "gem_infusing");
    public final static ResourceLocation TEXTURE =
             ResourceLocation.fromNamespaceAndPath(Woot.MODID, "textures/gui/jei/anvil.png");

    public final static RecipeType<AnvilRecipe> ANVIL_TYPE = new RecipeType<>(UID, AnvilRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public AnvilRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AnvilSetup.ANVIL_BLOCK.get()));
    }

    @Override
    public RecipeType<AnvilRecipe> getRecipeType() {
        return ANVIL_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Stygian Anvil");
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
    public void setRecipe(IRecipeLayoutBuilder builder, AnvilRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ing = recipe.ingredients();
        builder.addSlot(RecipeIngredientRole.INPUT, 82, 40).addIngredients(recipe.baseItem());
        int x = 28;
        int y = 29;

        builder.addSlot(RecipeIngredientRole.INPUT, x, y).addIngredients(ing.get(0));
        if(ing.size() > 1){
            builder.addSlot(RecipeIngredientRole.INPUT, x+ 18, y).addIngredients(ing.get(1));
        }
        if(ing.size() > 2){
            builder.addSlot(RecipeIngredientRole.INPUT, x, y + 18).addIngredients(ing.get(2));
        }
        if(ing.size() > 3){
            builder.addSlot(RecipeIngredientRole.INPUT, x+ 18, y + 18).addIngredients(ing.get(3));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 118, 40).addItemStack(recipe.output());
    }


}
