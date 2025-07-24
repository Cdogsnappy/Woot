package ipsis.woot.modules.factory.multiblock;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface MultiBlockGlue {

    void clearMaster();
    void setMaster(MultiBlockMaster master);
    void onHello(Level world, BlockPos pos);
    void onGoodbye();
    boolean hasMaster();
    BlockPos getPos();
}
