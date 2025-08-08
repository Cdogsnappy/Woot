package ipsis.woot.util;

import com.mojang.serialization.MapCodec;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WootBaseEntityBlock extends BaseEntityBlock {
    protected WootBaseEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, FactorySetup.WOOT_MACHINE_ENTITY.get() ,
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level));
    }
}
