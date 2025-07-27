package ipsis.woot.modules.factory.perks;

import ipsis.woot.modules.factory.FactoryConfiguration;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Helper {

    public static Perk getPerk(Perk.Group group, int level) {
        level = Math.clamp(level, 1,3) - 1;
        EnumSet<Perk> perks = Perk.getPerksByGroup(group);
        return perks.toArray(new Perk[0])[level];
    }

    public static Component getTooltip(Perk perk) {
        return Component.translatable("info.woot.perk." + Perk.getGroup(perk).getLowerCaseName());
    }

    public static List<Component> getTooltip(Perk perk, int level) {
        Perk.Group group = Perk.getGroup(perk);
        List<Component> tooltips = new ArrayList<>();
        level = Math.clamp(level, 1, 3);
        String tag = "info.woot.perk." + group.getLowerCaseName() + ".0";
        ModConfigSpec.IntValue intValue = FactoryConfiguration.getPerkIntValue(group, level);
        if (intValue != null)
            tooltips.add(Component.translatable(tag, intValue.get()));
        return tooltips;
    }
}
