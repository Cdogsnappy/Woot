package ipsis.woot.simulator;

import com.ibm.icu.impl.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ipsis.woot.Woot;
import ipsis.woot.simulator.tartarus.TartarusChunkGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.OptionalLong;


public class MobSimulatorSetup {

    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, Woot.MODID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<TartarusChunkGenerator>> MY_CHUNK_GENERATOR =
            CHUNK_GENERATORS.register("my_chunk_generator", () -> TartarusChunkGenerator.CODEC);

    public static void register(IEventBus eventBus) {
        CHUNK_GENERATORS.register(eventBus);
    }

    public static final ResourceKey<DimensionType> TARTARUS_DIMENSION_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE,  ResourceLocation.fromNamespaceAndPath(Woot.MODID, "mobsimulator"));
    public static final ResourceKey<LevelStem> TARTARUS = ResourceKey.create(
            Registries.LEVEL_STEM, ResourceLocation.fromNamespaceAndPath(Woot.MODID, "tartarus"));

    public static final ResourceKey<Level> TARTARUS_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(Woot.MODID, "tartarus_type"));

    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        context.register(TARTARUS_DIMENSION_TYPE, new DimensionType(
                OptionalLong.of(12000), // fixedTime
                false, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                false, // natural
                1.0, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                0, // minY
                256, // height
                256, // logicalHeight
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn
                BuiltinDimensionTypes.OVERWORLD_EFFECTS, // effectsLocation
                0.0f, // ambientLight
                new DimensionType.MonsterSettings(true, false, ConstantInt.of(0), 15)));
    }

    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);
        HolderGetter<DimensionType> dimTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseGenSettings = context.lookup(Registries.NOISE_SETTINGS);

        ChunkGenerator wrappedChunkGenerator = new TartarusChunkGenerator(new FixedBiomeSource(biomeRegistry.getOrThrow(Biomes.OCEAN)),
                noiseGenSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD));


        LevelStem stem = new LevelStem(dimTypes.getOrThrow(TARTARUS_DIMENSION_TYPE), wrappedChunkGenerator);

        context.register(TARTARUS, stem);
    }
}

