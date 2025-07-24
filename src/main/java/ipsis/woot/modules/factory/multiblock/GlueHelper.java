package ipsis.woot.modules.factory.multiblock;

import ipsis.woot.modules.factory.blocks.HeartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GlueHelper {

    private static boolean isGlueBlock(Block b) {
        return b instanceof MultiBlockGlueProvider;
    }

    private static boolean isMaster(Block b) {
        return b instanceof HeartBlock;
    }

    /**
     * This is safe to use when the chunk that the origin block is in, is still loading.
     * TEs are not valid to call, as they will call the validate method which will just recurse.
     * The only blocks that can find the master will be those on the edge next to a loaded chunk.
     */
    public static MultiBlockMaster findMasterNoTE(Level world, BlockPos origin) {

        List<BlockPos> connected = new ArrayList<>();
        Stack<BlockPos> traversing = new Stack<>();

        MultiBlockMaster master = null;

        traversing.add(origin);
        while (master == null && !traversing.isEmpty()) {
            BlockPos currPos = traversing.pop();
            connected.add(currPos);
            for (Direction facing : Direction.values()) {
                BlockPos pos = currPos.offset(facing.getNormal());
                if (world.isLoaded(pos)) {
                    Block b = world.getBlockState(pos).getBlock();
                    if (isGlueBlock(b) && !connected.contains(pos)) {
                        traversing.add(pos);
                    } else if (isMaster(b)) {
                        BlockEntity te = world.getBlockEntity(pos);
                        if (te instanceof MultiBlockMaster) {
                            master = (MultiBlockMaster)te;
                        }
                    }
                }
            }
        }
        return master;
    }

    public static MultiBlockMaster findMaster(Level world, MultiBlockGlueProvider iMultiBlockGlueProvider) {

        List<MultiBlockGlueProvider> connected = new ArrayList<>();
        Stack<MultiBlockGlueProvider> traversing = new Stack<>();

        MultiBlockMaster master = null;

        traversing.add(iMultiBlockGlueProvider);
        while (master == null && !traversing.isEmpty()) {
            MultiBlockGlueProvider curr = traversing.pop();
            connected.add(curr);
            for (Direction facing : Direction.values()) {
                BlockPos pos = curr.getGlue().getPos().offset(facing.getNormal());
                if (world.isLoaded(pos)) {
                    BlockEntity te = world.getBlockEntity(curr.getGlue().getPos().offset(facing.getNormal()));
                    if (te instanceof  MultiBlockGlueProvider && !connected.contains(te)) {
                        traversing.add((MultiBlockGlueProvider)te);
                    } else if (te instanceof MultiBlockMaster) {
                        master = (MultiBlockMaster) te;
                    }
                }
            }
        }

        return master;
    }
}
