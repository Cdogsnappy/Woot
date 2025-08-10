package ipsis.woot.modules.factory.blocks;

import com.mojang.serialization.MapCodec;
import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.FactoryComponentProvider;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.multiblock.MultiBlockBlockEntity;
import ipsis.woot.util.WootBaseEntityBlock;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


import javax.annotation.Nullable;
import java.util.List;

public class FactoryBlock extends Block implements FactoryComponentProvider, WootDebug, EntityBlock {

    private final FactoryComponent component;

    public static final String FACTORY_A_REGNAME = "factory_a";
    public static final String FACTORY_B_REGNAME = "factory_b";
    public static final String FACTORY_C_REGNAME = "factory_c";
    public static final String FACTORY_D_REGNAME = "factory_d";
    public static final String FACTORY_E_REGNAME = "factory_e";
    public static final String CAP_A_REGNAME = "cap_a";
    public static final String CAP_B_REGNAME = "cap_b";
    public static final String CAP_C_REGNAME = "cap_c";
    public static final String CAP_D_REGNAME = "cap_d";
    public static final String FACTORY_CONNECT_REGNAME = "factory_connect";
    public static final String FACTORY_CTR_BASE_PRI_REGNAME = "factory_ctr_base_pri";
    public static final String FACTORY_CTR_BASE_SEC_REGNAME = "factory_ctr_base_sec";
    public static final String IMPORT_REGNAME = "import";
    public static final String EXPORT_REGNAME = "export";

    public FactoryBlock(FactoryComponent component) {
        super(Block.Properties.of().sound(SoundType.STONE).strength(3.5F));
        registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.ATTACHED, false));
        this.component = component;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.ATTACHED);
    }

    /**
     * Block display since we are less than a full block
     */
    private final VoxelShape shape = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (this.component == FactoryComponent.FACTORY_UPGRADE)
            return super.getShape(state, level, pos, context);
        
        if (state.getValue(BlockStateProperties.ATTACHED))
            return Shapes.block();
        else
            return shape;
    }

    /**
     * FactoryComponentProvider
     */
    public FactoryComponent getFactoryComponent() {
        return this.component;
    }



    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> FactoryBlock (" + component + ")");
        DebugItem.getTileEntityDebug(debug, itemUseContext);
        return debug;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MultiBlockBlockEntity(FactorySetup.MULTIBLOCK_BLOCK_TILE.get(), blockPos, blockState);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }
}
