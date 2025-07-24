package ipsis.woot.simulator.spawning;

import ipsis.woot.util.FakeMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public abstract class AbstractMobSpawner {

    abstract public void apply(Mob mobEntity, FakeMob fakeMob, Level world);
}
