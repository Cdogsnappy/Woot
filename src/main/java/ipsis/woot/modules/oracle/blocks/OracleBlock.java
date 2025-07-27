package ipsis.woot.modules.oracle.blocks;



import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class OracleBlock extends Block {

    public OracleBlock() {
        super(Properties.of(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(3.5F));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockReader world) {
        return new OracleTileEntity();
    }

    @Override
    public InteractionResult onBlockActivated(BlockState state, Level worldIn, BlockPos pos, Player player, BlockHitResult result) {
        if (worldIn.isClientSide)
            return super.useWithoutItem(state, worldIn, pos, player, result);

        BlockEntity te = worldIn.getBlockEntity((pos));
        if (te instanceof MenuProvider)
            player.openMenu(te.getBlockState().getMenuProvider(worldIn, pos));
        else
            throw new IllegalStateException("Named container provider is missing");

        return InteractionResult.SUCCESS; // Block was activated
    }
}
