package ipsis.woot.modules.debug.blocks;



import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class TickConvertorBlock extends Block implements EntityBlock {

    public TickConvertorBlock() {
        super(Block.Properties.of().sound(SoundType.METAL));
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TickConvertorBlockEntity(pos, state);
    }
}
