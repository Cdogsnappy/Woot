package ipsis.woot.modules.debug.blocks;


import ipsis.woot.util.WootBaseEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CreativePowerBlock extends WootBaseEntityBlock implements EntityBlock {

    public CreativePowerBlock() {
        super(Properties.of().sound(SoundType.METAL));
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativePowerBlockEntity(pos, state);
    }
}
