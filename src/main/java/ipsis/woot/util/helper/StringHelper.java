package ipsis.woot.util.helper;


import ipsis.woot.util.FakeMob;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public class StringHelper {

    public static String translate(String key) {
        return ClientLanguage.getInstance().getOrDefault(key);
    }

    public static String translateFormat(String key, Object... format) {
        return String.format(ClientLanguage.getInstance().getOrDefault(key), format);
    }

    public static String translate(FakeMob fakeMob) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(fakeMob.getResourceLocation());

        String mob = translate(entityType.getDescriptionId());

        if (fakeMob.hasTag())
            return translateFormat("misc.woot.tagged_mob", mob, fakeMob.getTag());

        return mob;
    }
}
