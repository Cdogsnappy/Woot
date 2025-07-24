package ipsis.woot.simulator;

import ipsis.woot.Woot;
import ipsis.woot.simulator.tartarus.TartarusChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.bus.api.IEventBus;


public class MobSimulatorSetup {

    public static final ResourceKey<DimensionType> TARTARUS_DIMENSION_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,  ResourceLocation.fromNamespaceAndPath(Woot.MODID, "mobsimulator"));
    public static final ResourceKey<LevelStem> TARTARUS = ResourceKey.create(
            Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath(Woot.MODID, "tartarus"));

    public static void register(IEventBus bus) {
        Registries.CHUNK_GENERATOR.registry()
                Registries.,
                Woot.MODID + ":simulation_cells",
                TartarusChunkGenerator.codecTarturusChunk);
    }
}
