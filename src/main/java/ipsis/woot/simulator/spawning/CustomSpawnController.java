package ipsis.woot.simulator.spawning;

import ipsis.woot.util.FakeMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;


import java.util.ArrayList;
import java.util.List;

public class CustomSpawnController {

    public static CustomSpawnController get() { return INSTANCE; }
    static CustomSpawnController INSTANCE;
    static { INSTANCE = new CustomSpawnController(); }

    private List<AbstractMobSpawner> spawnerList = new ArrayList<>();
    private CustomSpawnController() {
        spawnerList.add(new SlimeSpawner());
        spawnerList.add(new MagmaCubeSpawner());
        spawnerList.add(new ChargedCreeperSpawner());
    }

    public void apply(Mob mobEntity, FakeMob fakeMob, Level world) {
        if (!fakeMob.isValid() || world == null)
            return;

        for (AbstractMobSpawner abstractMobSpawner : spawnerList)
            abstractMobSpawner.apply(mobEntity, fakeMob, world);
    }
}
