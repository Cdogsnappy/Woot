package ipsis.woot.util.helper;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WorldHelper {

    public static void updateClient(Level world, BlockPos pos) {
        if (world != null) {
            BlockState blockState = world.getBlockState(pos);
            world.sendBlockUpdated(pos,  blockState, blockState, 4);
        }
    }

    public static void updateNeighbours(Level world, BlockPos pos) {
        if (world != null)
            world.updateNeighborsAt(pos, world.getBlockState(pos).getBlock());
    }
}
