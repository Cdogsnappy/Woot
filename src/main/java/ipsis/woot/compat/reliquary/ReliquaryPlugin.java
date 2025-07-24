package ipsis.woot.compat.reliquary;

import ipsis.woot.Woot;
import ipsis.woot.util.FakeMob;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReliquaryPlugin {

    private static Item MOB_CHARM_FRAGMENT = null;

    private static boolean isValidMonster(FakeMob fakeMob, Level world) {
        AtomicBoolean valid = new AtomicBoolean(false);
        BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.parse(fakeMob.toString()))
                .filter((e) -> {
                    Entity entity = e.create(world);
                    return entity != null && e.getCategory() == MobCategory.MONSTER;
                }).ifPresent(p -> {
                    valid.set(true);
                });

        return valid.get();
    }

    public static ItemStack getCharmFragment(FakeMob fakeMob, Level world) {

        if (!isValidMonster(fakeMob, world))
            return ItemStack.EMPTY;

        if (MOB_CHARM_FRAGMENT == null)
            return ItemStack.EMPTY;

        // data: {id: "xreliquary:mob_charm_fragment", Count: 2b, tag: {entity: "minecraft:phantom"}}

        ResourceLocation rs = fakeMob.getResourceLocation();
        String registryName = rs.toString();
        ItemStack itemStack = new ItemStack(MOB_CHARM_FRAGMENT);
        CompoundTag nbt = (CompoundTag)itemStack.save(world.registryAccess());

        nbt.putString("entity", registryName);
        return ItemStack.parse(world.registryAccess(), nbt).get();

    }


    public static boolean isCharmFragment(ItemStack itemStack) {

        if (MOB_CHARM_FRAGMENT == null)
            return false;

        return itemStack.getItem() == MOB_CHARM_FRAGMENT;
    }
}
