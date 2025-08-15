package ipsis.woot.jei;

import ipsis.woot.Woot;
import ipsis.woot.crafting.WootRecipes;
import ipsis.woot.crafting.anvil.AnvilRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class WootJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Woot.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration){
        registration.addRecipeCategories(new AnvilRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new InfuserRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FluidConvertorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new DyeSqueezerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new EnchantSqueezerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        registration.addRecipes(AnvilRecipeCategory.ANVIL_TYPE, rm.getAllRecipesFor(WootRecipes.ANVIL_RECIPE_TYPE.get())
                .stream().map(RecipeHolder::value).toList());

        registration.addRecipes(InfuserRecipeCategory.INFUSER_TYPE, rm.getAllRecipesFor(WootRecipes.INFUSER_TYPE.get())
                .stream().map(RecipeHolder::value).toList());

        registration.addRecipes(FluidConvertorRecipeCategory.FLUID_CONVERTOR_TYPE, rm.getAllRecipesFor(WootRecipes.FLUID_CONVERTOR_TYPE.get())
                .stream().map(RecipeHolder::value).toList());

        registration.addRecipes(DyeSqueezerRecipeCategory.SQUEEZER_TYPE, rm.getAllRecipesFor(WootRecipes.DYE_SQUEEZER_TYPE.get())
                .stream().map(RecipeHolder::value).toList());

        registration.addRecipes(EnchantSqueezerRecipeCategory.ENCHANT_TYPE, rm.getAllRecipesFor(WootRecipes.ENCHANT_SQUEEZER_TYPE.get())
                .stream().map(RecipeHolder::value).toList());



    }
}
