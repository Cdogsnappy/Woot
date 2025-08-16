package ipsis.woot.mod;

import ipsis.woot.Woot;
import ipsis.woot.simulator.spawning.SpawnController;
import ipsis.woot.util.FakeMob;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Supplier;

public class IndustrialForegoingPlugin {


    private static final Supplier<Fluid> LIQUID_MEAT_FLUID = () ->
            Woot.INDUSTRIAL_FOREGOING_LOADED ? BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "meat")) : null;

    private static final Supplier<Fluid> ESSENCE_FLUID = () ->
            Woot.INDUSTRIAL_FOREGOING_LOADED ? BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "essence")) : null;

    private static final Supplier<Fluid> ETHER_FLUID = () ->
            Woot.INDUSTRIAL_FOREGOING_LOADED ? BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "ether_gas")) : null;

    private static final Supplier<Fluid> PINK_SLIME_FLUID = () ->
            Woot.INDUSTRIAL_FOREGOING_LOADED ? BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("industrialforegoing", "pink_slime")) : null;


    public static FluidStack getLiquidMeatAmount(FakeMob fakeMob, Level world) {
        if (LIQUID_MEAT_FLUID == null || world == null || fakeMob == null || !fakeMob.isValid())
            return FluidStack.EMPTY;

        int health = SpawnController.get().getMobHealth(fakeMob, world);
        if (!SpawnController.get().isAnimal(fakeMob, world))
            health *= 20;

        return new FluidStack(LIQUID_MEAT_FLUID.get(), health);
    }

    public static FluidStack getPinkSlimeAmount(FakeMob fakeMob, Level world) {
        if (PINK_SLIME_FLUID == null || world == null || fakeMob == null || !fakeMob.isValid())
            return FluidStack.EMPTY;

        int health = SpawnController.get().getMobHealth(fakeMob, world);
        if (!SpawnController.get().isAnimal(fakeMob, world))
            health *= 20;

        return new FluidStack(PINK_SLIME_FLUID.get(), health);
    }

    public static FluidStack getEssenceAmount(FakeMob fakeMob, Level world) {
        if (ESSENCE_FLUID == null || world == null || fakeMob == null || !fakeMob.isValid())
            return FluidStack.EMPTY;

        int xp = SpawnController.get().getMobExperience(fakeMob, world);
        return new FluidStack(ESSENCE_FLUID.get(), xp * 20);
    }

    public static FluidStack getEtherAmount(FakeMob fakeMob, Level world) {
        if (ETHER_FLUID == null || world == null || fakeMob == null || !fakeMob.isValid() || !fakeMob.isWither())
            return FluidStack.EMPTY;

        return new FluidStack(ETHER_FLUID.get(), 600);
    }
}
