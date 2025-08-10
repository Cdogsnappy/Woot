package ipsis.woot.simulator.tartarus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sun.jna.platform.win32.WinBase;
import ipsis.woot.Woot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;


import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class TartarusChunkGenerator extends ChunkGenerator {

    public final Holder<NoiseGeneratorSettings> settings;
    public static final MapCodec<TartarusChunkGenerator> CODEC =
            RecordCodecBuilder.mapCodec((inst) ->
                inst.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter((i) -> i.biomeSource),
                        NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter((j) -> j.settings)).apply(inst, inst.stable(TartarusChunkGenerator::new)));

    public TartarusChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;

    }

    public static final int WORK_CHUNK_X = 0;
    public static final int WORK_CHUNK_Z = 0;

    private List<BlockPos> cell0Blocks;
    private List<BlockPos> cell1Blocks;
    private List<BlockPos> cell2Blocks;
    private List<BlockPos> cell3Blocks;

    private void calcCellStructures() {

        if (cell1Blocks != null)
            return;

        List<BlockPos> fullBlocks = new ArrayList<>(8 * 8 * 8);
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                for (int y = 0; y < 8; y++) {
                    fullBlocks.add(new BlockPos(x, y, z));
                }
            }
        }

        List<BlockPos> innerBlocks = new ArrayList<>(8 * 8 * 8);
        for (int x = 1; x < 7; x++) {
            for (int z = 1; z < 7; z++) {
                for (int y = 1; y < 7; y++) {
                    innerBlocks.add(new BlockPos(x, y, z));
                }
            }
        }

        cell0Blocks = new ArrayList<>();
        for (BlockPos p : fullBlocks) {
            if (!innerBlocks.contains(p))
                cell0Blocks.add(new BlockPos(p));
        }

        cell1Blocks = new ArrayList<>(cell0Blocks.size());
        for (BlockPos p : cell0Blocks)
            cell1Blocks.add(new BlockPos(p.getX() + 8, p.getY(), p.getZ()));
        cell2Blocks = new ArrayList<>(cell0Blocks.size());
        for (BlockPos p : cell0Blocks)
            cell2Blocks.add(new BlockPos(p.getX() + 8, p.getY(), p.getZ() + 8));
        cell3Blocks = new ArrayList<>(cell0Blocks.size());
        for (BlockPos p : cell0Blocks)
            cell3Blocks.add(new BlockPos(p.getX(), p.getY(), p.getZ() + 8));

        fullBlocks = null;
        innerBlocks = null;
    }

    private void buildCell(ChunkAccess iChunk, List<BlockPos> posList, int y, BlockState blockState) {
        for (BlockPos pos : posList)
            iChunk.setBlockState(new BlockPos(pos.getX(), pos.getY() + y, pos.getZ()), blockState, false);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long l, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {
    }

    @Override
    public void buildSurface(WorldGenRegion worldGenRegion, StructureManager structureManager, RandomState randomState, ChunkAccess chunkAccess) {
        BlockState blockState = Blocks.AIR.defaultBlockState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x1 = 0; x1 < 16; x1++) {
            for (int z1 = 0; z1 < 16; z1++) {
                for (int y1 = 0; y1 < 256; y1++) {
                    chunkAccess.setBlockState(pos.set(x1, y1, z1), blockState, false);
                }
            }
        }

        if (chunkAccess.getPos().x == WORK_CHUNK_X && chunkAccess.getPos().z == WORK_CHUNK_Z) {
            Woot.setup.getLogger().debug("generateSurface: work chunk creating cells");
            BlockState wallState = Blocks.BEDROCK.defaultBlockState();
            calcCellStructures();

            for (int y = 0; y < 256; y += 8) {
                buildCell(chunkAccess, cell0Blocks, y, wallState);
                buildCell(chunkAccess, cell1Blocks, y, wallState);
                buildCell(chunkAccess, cell2Blocks, y, wallState);
                buildCell(chunkAccess, cell3Blocks, y, wallState);
            }
        }
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {

    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        return CompletableFuture.supplyAsync(() -> {
            // Create a simple flat stone platform at y=64
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos pos = new BlockPos(x, 0, z);
                    chunkAccess.setBlockState(pos, Blocks.BEDROCK.defaultBlockState(), false);
                }
            }

            return chunkAccess;
        });

    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int i, int i1, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return 0;
    }

    @Override
    public @NotNull NoiseColumn getBaseColumn(int i, int i1, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        int minY = levelHeightAccessor.getMinBuildHeight();
        int maxY = levelHeightAccessor.getMaxBuildHeight();

        // Create an array to hold the block states for this column
        BlockState[] column = new BlockState[maxY - minY];
        for( int x = minY; x < maxY; x++){
            column[x-minY] = Blocks.AIR.defaultBlockState();
        }

        // Fill the column with blocks (basic example)
        return new NoiseColumn(minY, column);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {

    }

}
