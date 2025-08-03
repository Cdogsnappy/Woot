package ipsis.woot.crafting;

import ipsis.woot.Woot;
import ipsis.woot.crafting.anvil.AnvilRecipe;
import ipsis.woot.crafting.dyesqueezer.DyeSqueezerRecipe;
import ipsis.woot.crafting.factory.FactoryRecipe;
import ipsis.woot.crafting.infuser.InfuserRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WootRecipes {


    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Woot.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Woot.MODID);



    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DyeSqueezerRecipe>> DYE_SQUEEZER_SERIALIZER =
            SERIALIZERS.register("dye_squeezer", DyeSqueezerRecipe.DyeSqueezerRecipeType::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DyeSqueezerRecipe>> DYE_SQUEEZER_TYPE =
            TYPES.register("dye_squeezer", DyeSqueezerRecipe.DyeSqueezerRecipeType::new);


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnvilRecipe>> ANVIL_RECIPE_SERIALIZER =
            SERIALIZERS.register("anvil", AnvilRecipe.AnvilRecipeType::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AnvilRecipe>> ANVIL_RECIPE_TYPE =
            TYPES.register("anvil", AnvilRecipe.AnvilRecipeType::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FactoryRecipe>> FACTORY_SERIALIZER =
            SERIALIZERS.register("factory", FactoryRecipe.FactoryRecipeType::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FactoryRecipe>> FACTORY_RECIPE_TYPE =
            TYPES.register("factory", FactoryRecipe.FactoryRecipeType::new);


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfuserRecipe>> INFUSER_SERIALIZER =
            SERIALIZERS.register("infuser", InfuserRecipe.InfuserRecipeType::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<InfuserRecipe>> INFUSER_TYPE =
            TYPES.register("infuser", InfuserRecipe.InfuserRecipeType::new);
}
