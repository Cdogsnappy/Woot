package ipsis.woot.modules.factory.blocks;

import ipsis.woot.modules.debug.items.DebugItem;
import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.FactoryComponentProvider;
import ipsis.woot.modules.factory.multiblock.MultiBlockBlockEntity;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


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
        super(Block.Properties.create(Material.IRON).sound(SoundType.STONE).hardnessAndResistance(3.5F));
        setDefaultState(getStateContainer().getBaseState().with(BlockStateProperties.ATTACHED, false));
        this.component = component;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.ATTACHED);
    }

    /**
     * Block display since we are less than a full block
     */
    private final VoxelShape shape = Block.makeCuboidShape(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (this.component == FactoryComponent.FACTORY_UPGRADE)
            return super.getShape(state, worldIn, pos, context);
        
        if (state.get(BlockStateProperties.ATTACHED))
            return VoxelShapes.fullCube();
        else
            return shape;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MultiBlockBlockEntity();
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
        return null;
    }
}
