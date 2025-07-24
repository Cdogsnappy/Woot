package ipsis.woot.simulator.spawning;

import ipsis.woot.util.FakeMob;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;


public class ChargedCreeperSpawner extends AbstractMobSpawner {

    @Override
    public void apply(Mob mobEntity, FakeMob fakeMob, Level world) {

        if (!(mobEntity instanceof Creeper))
            return;

        if (!(world instanceof ServerLevel))
            return;

        if (fakeMob.isChargedCreeper()) {
            LightningBolt b = EntityType.LIGHTNING_BOLT.create(world);
            b.moveTo(mobEntity.getOnPos(), 0, 0);
            world.addFreshEntity(b);
        }
    }
}
