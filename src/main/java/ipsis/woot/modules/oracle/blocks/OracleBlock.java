package ipsis.woot.modules.oracle.blocks;



import com.mojang.serialization.MapCodec;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class OracleBlock extends BaseEntityBlock {

    public static final MapCodec CODEC = simpleCodec(OracleBlock::new);

    public OracleBlock(Properties prop) {
        super(Properties.of().sound(SoundType.METAL).strength(3.5F));
    }



    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        BlockEntity te = level.getBlockEntity((pos));
        if (te instanceof MenuProvider)
            player.openMenu(te.getBlockState().getMenuProvider(level, pos));
        else
            throw new IllegalStateException("Named container provider is missing");

        return ItemInteractionResult.SUCCESS; // Block was activated
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new OracleTileEntity(blockPos, blockState);
    }
}
