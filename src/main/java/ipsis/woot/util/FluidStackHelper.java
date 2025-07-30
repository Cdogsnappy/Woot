package ipsis.woot.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import ipsis.woot.Woot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.internal.NeoForgeRecipeProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


import javax.annotation.Nonnull;

public class FluidStackHelper {

    public static FluidStack parse(@Nonnull JsonObject jsonObject) {
        ResourceLocation id = ResourceLocation.parse(GsonHelper.convertToString(jsonObject, "fluid"));
        Fluid fluid = BuiltInRegistries.FLUID.get(id);

        return new FluidStack(fluid, GsonHelper.convertToInt(jsonObject, "amount"));
    }

    public static JsonObject create(@Nonnull FluidStack fluidStack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fluid", fluidStack.getFluid().toString());
        jsonObject.addProperty("amount", fluidStack.getAmount());
        return jsonObject;
    }

    public static FluidStack reduceByPercentage(FluidStack fluidStack, double p) {

        p = Math.clamp(p, 0.0F, 100.0F);

        Woot.setup.getLogger().debug("reduceByPercentage: {} @ {}%%",
                fluidStack.getAmount(), p);

        if (p == 0.0F || fluidStack.isEmpty())
            return fluidStack;

        if (p == 100.0F)
            return FluidStack.EMPTY;

        int reduction = (int)((fluidStack.getAmount() / 100.0F) * p);
        int left = fluidStack.getAmount() - reduction;
        fluidStack.setAmount(left);

        return fluidStack;
    }
}
