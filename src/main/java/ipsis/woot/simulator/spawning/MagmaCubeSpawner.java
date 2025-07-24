package ipsis.woot.simulator.spawning;

import ipsis.woot.Woot;
import ipsis.woot.util.FakeMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.level.Level;

public class MagmaCubeSpawner extends AbstractMobSpawner {

    @Override
    public void apply(Mob mobEntity, FakeMob fakeMob, Level world) {

        if (!(mobEntity instanceof MagmaCube))
            return;

        MagmaCube magmaCubeEntity = (MagmaCube)mobEntity;
        if (fakeMob.isSmallMagmaCube()) {
            magmaCubeEntity.setSize(1, false);
            //Woot.setup.getLogger().debug("SlimeSpawner: set size to small {}", magmaCubeEntity.getSlimeSize());
        } else {
            magmaCubeEntity.setSize(2, false);
            //Woot.setup.getLogger().debug("SlimeSpawner: set size to small {}", magmaCubeEntity.getSlimeSize());
        }
    }
}
