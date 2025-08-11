package ipsis.woot.util;

import com.mojang.serialization.MapCodec;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.squeezer.SqueezerSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.property.Properties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class WootBaseEntityBlock extends Block implements EntityBlock {
    public WootBaseEntityBlock(BlockBehaviour.Properties prop) {
        super(prop);
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


        return (level1, blockPos, blockState, blockEntity) -> ((WootMachineBlockEntity)blockEntity).tick(level);
    }
}
