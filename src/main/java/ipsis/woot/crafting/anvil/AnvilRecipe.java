package ipsis.woot.crafting.anvil;

import ipsis.woot.Woot;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.items.MobShardItem;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ipsis.woot.crafting.anvil.AnvilRecipeBuilder.SERIALIZER;

public class AnvilRecipe implements Recipe<AnvilRecipeInput> {

    private final NonNullList<Ingredient> ingredients;
    private final Ingredient baseIngredient;
    private final Item result;
    private final int count;
    private final ResourceLocation id;
    private final RecipeType<?> type;

    public AnvilRecipe(ResourceLocation id, Ingredient baseIngredient, ItemLike result, int count, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.baseIngredient = baseIngredient;
        this.result = result.asItem();
        this.count = count;
        this.ingredients = ingredients;
        this.type = ANVIL_TYPE;


        if (baseIngredient.getMatchingStacks().length == 1 && baseIngredient.getMatchingStacks()[0].getItem() == FactorySetup.MOB_SHARD_ITEM.get()) {
            ItemStack itemStack = new ItemStack(FactorySetup.MOB_SHARD_ITEM.get());
            MobShardItem.setJEIEnderShard(itemStack);
            inputs.add(Arrays.asList(itemStack));
        } else {
            inputs.add(Arrays.asList(baseIngredient.getMatchingStacks()));
        }

        for (Ingredient i : ingredients)
            inputs.add(Arrays.asList(i.getMatchingStacks()));
    }

    public Ingredient getBaseIngredient() { return this.baseIngredient; }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    public NonNullList<Ingredient> getIngredients() { return this.ingredients; }
    public ItemStack getOutput() { return new ItemStack(result, count); }

    public static final RecipeType<AnvilRecipe> ANVIL_TYPE = RecipeType.register(Woot.MODID + ":anvil");

    /**
     * Valid inputs
     */
    private static List<ItemStack> validInputs = new ArrayList<>();
    public static void clearValidInputs() { validInputs.clear(); }
    public static void addValidInput(ItemStack itemStack) { validInputs.add(itemStack); }
    public static boolean isValidInput(ItemStack itemStack) {
        for (ItemStack i : validInputs) {
            if (i.is(itemStack.getItem()))
                return true;
        }
        return false;
    }

    /**
     * Jei
     */
    private List<List<ItemStack>> inputs = new ArrayList<>();
    public List<List<ItemStack>> getInputs() { return inputs; }

    /**
     * IRecipe
     * Matches base item and all ingredients
     */
    @Override
    public boolean matches(Inventory inv, Level worldIn) {
        if (!baseIngredient.test(inv.getItem(0)))
            return false;

        int count = 0;
        for (int i = 1; i < 4; i++) {
            if (!inv.getItem(i).isEmpty())
                count++;
        }

        if (ingredients.size() != count)
            return false;

        List<Integer> matchedSlots = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            for (int i = 1; i < 4; i++) {
                if (!matchedSlots.contains(i) && ingredient.test(inv.getItem(i))) {
                    // found ingredient in one of the slots
                    matchedSlots.add(i);
                    break;
                }
            }
        }

        if (matchedSlots.size() == ingredients.size())
            return true;

        return false;
    }

    @Override
    public ItemStack getCraftingResult(Inventory inv) {
        return null;
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }
}
