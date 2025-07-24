package ipsis.woot.simulator.spawning;

import ipsis.woot.Woot;
import ipsis.woot.util.FakeMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

public class SlimeSpawner extends AbstractMobSpawner {

    @Override
    public void apply(Mob mobEntity, FakeMob fakeMob, Level world) {

        if (!(mobEntity instanceof Slime))
            return;

        Slime slimeEntity = (Slime)mobEntity;
        if (fakeMob.isSmallSlime()) {
            slimeEntity.setSize(1, false);
            //Woot.setup.getLogger().debug("SlimeSpawner: set size to small {}", slimeEntity.getSlimeSize());
        } else {
            slimeEntity.setSize(2, false);
            //Woot.setup.getLogger().debug("SlimeSpawner: set size to small {}", slimeEntity.getSlimeSize());
        }
    }
}
