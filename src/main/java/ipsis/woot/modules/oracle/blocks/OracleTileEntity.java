package ipsis.woot.modules.oracle.blocks;

import ipsis.woot.modules.oracle.OracleSetup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.awt.*;

public class OracleTileEntity extends BlockEntity implements MenuProvider {

    public OracleTileEntity() {
        super(OracleSetup.ORACLE_BLOCK_TILE.get());
    }

    /**
     * INamedContainerProvider
     */
    @Override
    public TextComponent getDisplayName() {
        return new TranslationTextComponent("gui.woot.oracle.name");
    }


    @Override
    public @org.jetbrains.annotations.Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new OracleContainer(i, world, pos, playerInventory, playerEntity);
    }
}
