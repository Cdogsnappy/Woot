package ipsis.woot.modules.factory.blocks;

import ipsis.woot.Woot;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.perks.Perk;
import ipsis.woot.modules.factory.items.PerkItem;
import ipsis.woot.modules.factory.multiblock.MultiBlockBlockEntity;
import ipsis.woot.modules.factory.multiblock.MultiBlockTracker;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class UpgradeBlockEntity extends MultiBlockBlockEntity implements WootDebug {

    public UpgradeBlockEntity(BlockPos pos, BlockState state) {
        super(FactorySetup.FACTORY_UPGRADE_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean tryAddUpgrade(Level world, Player playerEntity, BlockState state, Perk type) {

        if (state.getValue(UpgradeBlock.UPGRADE) == Perk.empty) {
            // Add to empty must be level 1
            if (Perk.LEVEL_1_PERKS.contains(type)) {
                world.setBlock(getBlockPos(),
                        state.setValue(UpgradeBlock.UPGRADE, type), 2);
                glue.onGoodbye();
                MultiBlockTracker.get().addEntry(world, getBlockPos());
                Woot.setup.getLogger().debug("tryAddUpgrade: added {}", type);
                return true;
            } else {
                playerEntity.sendSystemMessage(Component.translatable("chat.woot.perk.fail.0"));
                return false;
            }
        } else {
            // Add to non-empty, must be same type and level + 1
            Perk upgrade = getBlockState().getValue(UpgradeBlock.UPGRADE);
            Perk.Group currType = Perk.getGroup(upgrade);
            Perk.Group addType = Perk.getGroup(type);
            int currLevel = Perk.getLevel(upgrade);
            int addLevel = Perk.getLevel(type);
            if (currType != addType) {
                playerEntity.sendSystemMessage(Component.translatable("chat.woot.perk.fail.1"));
                return false;
            }

            if (currLevel == 3) {
                playerEntity.sendSystemMessage(Component.translatable("chat.woot.perk.fail.2"));
                return false;
            }

            if (currLevel == addLevel) {
                playerEntity.sendSystemMessage(Component.translatable("chat.woot.perk.fail.4"));
                return false;
            }

            if (currLevel + 1 != addLevel) {
                playerEntity.sendSystemMessage(Component.translatable("chat.woot.perk.fail.4", currLevel + 1));
                return false;
            }

            world.setBlock(getBlockPos(),
                    state.setValue(UpgradeBlock.UPGRADE, type), 2);
            glue.onGoodbye();
            MultiBlockTracker.get().addEntry(world, getBlockPos());
            Woot.setup.getLogger().debug("tryAddUpgrade: added {}", type);
            return true;
        }
    }

    public void dropItems(BlockState state, Level world, BlockPos pos) {
        Perk upgrade = state.getValue(UpgradeBlock.UPGRADE);
        if (upgrade == Perk.empty)
            return;

        int currLevel = Perk.getLevel(upgrade);
        for (int i = 1; i <= 3; i++) {
            if (i <= currLevel) {
                Perk.Group type = Perk.getGroup(upgrade);
                ItemStack itemStack = PerkItem.getItemStack(type, i);
                if (!itemStack.isEmpty()) {
                    itemStack.setCount(1);
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                }
            }
        }
    }

    public @Nullable
    Perk getUpgrade(BlockState state) {
        return state.getValue(UpgradeBlock.UPGRADE);
    }

    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> UpgradeTileEntity");
        debug.add("      hasMaster: " + glue.hasMaster());
        debug.add("      upgrade: " + level.getBlockState(getBlockPos()).getValue(UpgradeBlock.UPGRADE));
        return debug;
    }

}
