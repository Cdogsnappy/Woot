package ipsis.woot.modules.factory;

import ipsis.woot.util.helper.StringHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public enum Exotic {
    /**
     * Empty exotic
     */
    NONE,
    /**
     * Fluid ingredient reduction
     */
    EXOTIC_A,
    /**
     * Item ingredient reduction
     */
    EXOTIC_B,
    /**
     * Conatus efficiency
     */
    EXOTIC_C,
    /**
     * Fixed spawn time
     */
    EXOTIC_D,
    /**
     * Fixed mass count
     */
    EXOTIC_E
    ;

    public static Exotic[] VALUES = values();
    public String getName() { return name().toLowerCase(Locale.ROOT); }
    public static int getExoticCount() { return VALUES.length - 1; }

    public static Exotic getExotic(int index) {
        index = Math.clamp(index, 0, VALUES.length);
        return VALUES[index];
    }

    public Component getTooltip() {
        return Component.translatable("info.woot.exotic." + getName());
    }

    public ItemStack getItemStack() {
        if (this == EXOTIC_A)
            return new ItemStack(FactorySetup.EXOTIC_A_BLOCK.get());
        else if (this == EXOTIC_B)
            return new ItemStack(FactorySetup.EXOTIC_B_BLOCK.get());
        else if (this == EXOTIC_C)
            return new ItemStack(FactorySetup.EXOTIC_C_BLOCK.get());
        else if (this == EXOTIC_D)
            return new ItemStack(FactorySetup.EXOTIC_D_BLOCK.get());
        else if (this == EXOTIC_E)
            return new ItemStack(FactorySetup.EXOTIC_E_BLOCK.get());

        return ItemStack.EMPTY;
    }

}
