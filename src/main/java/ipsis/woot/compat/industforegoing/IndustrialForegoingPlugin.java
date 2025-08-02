package ipsis.woot.compat.industforegoing;

import ipsis.woot.simulator.spawning.SpawnController;
import ipsis.woot.util.FakeMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class IndustrialForegoingPlugin {

    private static Fluid LIQUID_MEAT_FLUID = null;

    private static Fluid PINK_SLIME_FLUID = null;

    private static Fluid ESSENCE_FLUID = null;

    private static Fluid ETHER_FLUID = null;

    public static FluidStack getLiquidMeatAmount(FakeMob fakeMob, Level world) {
        if (LIQUID_MEAT_FLUID == null || world == null || fakeMob == null || !fakeMob.isValid())
            return FluidStack.EMPTY;

        int health = SpawnController.get().getMobHealth(fakeMob, world);
        if (!SpawnController.get().isAnimal(fakeMob, world))
            health *= 20;

        return new FluidStack(LIQUID_MEAT_FLUID, health);
    }

    public static FluidStack getPinkSlimeAmount(FakeMob fakeMob, Level world) {
        if (PINK_SLIME_FLUID == null || world == null || fakeMob == null || !fakeMob.isValid())
            return FluidStack.EMPTY;

        int health = SpawnController.get().getMobHealth(fakeMob, world);
        if (!SpawnController.get().isAnimal(fakeMob, world))
            health *= 20;

        return new FluidStack(PINK_SLIME_FLUID, health);
    }

    public static FluidStack getEssenceAmount(FakeMob fakeMob, Level world) {
        if (ESSENCE_FLUID == null || world == null || fakeMob == null || !fakeMob.isValid())
            return FluidStack.EMPTY;

        int xp = SpawnController.get().getMobExperience(fakeMob, world);
        return new FluidStack(ESSENCE_FLUID, xp * 20);
    }

    public static FluidStack getEtherAmount(FakeMob fakeMob, Level world) {
        if (ETHER_FLUID == null || world == null || fakeMob == null || !fakeMob.isValid() || !fakeMob.isWither())
            return FluidStack.EMPTY;

        return new FluidStack(ETHER_FLUID, 600);
    }
}
