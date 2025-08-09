package ipsis.woot.modules.oracle.blocks;

import ipsis.woot.modules.oracle.OracleSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class OracleTileEntity extends BlockEntity implements MenuProvider {

    public OracleTileEntity(BlockPos pos, BlockState blockState) {
        super(OracleSetup.ORACLE_BLOCK_TILE.get(), pos, blockState);
    }

    /**
     * INamedContainerProvider
     */
    @Override
    public Component getDisplayName() {
        return Component.literal("gui.woot.oracle.name");
    }


    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new OracleContainer(i, inventory,this);
    }
}
