package ipsis.woot.modules.factory.blocks;

import ipsis.woot.config.Config;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.modules.factory.multiblock.MultiBlockBlockEntity;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.WootDebug;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ControllerBlockEntity extends MultiBlockBlockEntity implements WootDebug {

    public ControllerBlockEntity(BlockPos pos, BlockState state) {
        super(FactorySetup.CONTROLLER_BLOCK_TILE.get(), pos, state);
    }

    private FakeMob fakeMob = new FakeMob();

    @Override
    public void saveAdditional(CompoundTag compoundNBT, HolderLookup.Provider registries) {
        super.saveAdditional(compoundNBT, registries);
        CompoundTag nbt = new CompoundTag();
        FakeMob.writeToNBT(fakeMob, nbt);
        compoundNBT.put(ModNBT.Controller.MOB_TAG, nbt);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        readFromNBT(tag);
    }

    private void readFromNBT(CompoundTag compoundNBT) {
        if (compoundNBT.contains(ModNBT.Controller.MOB_TAG)) {
            CompoundTag nbt = compoundNBT.getCompound(ModNBT.Controller.MOB_TAG);
            fakeMob = new FakeMob(nbt);
        }
    }

    public FakeMob getFakeMob() {
        return fakeMob;
    }

    public void setFakeMob(FakeMob fakeMob){
        this.fakeMob = fakeMob;
    }

    public Tier getTier() {
        if (fakeMob == null || !fakeMob.isValid())
            return Tier.UNKNOWN;

        return Config.OVERRIDE.getMobTier(fakeMob, level);
    }

    public static ItemStack getItemStack(FakeMob fakeMob) {
        ItemStack itemStack = new ItemStack(FactorySetup.CONTROLLER_BLOCK.get());

        /**
         * setTileEntityNBT
         */
        CompoundTag nbt = new CompoundTag();
        FakeMob.writeToNBT(fakeMob, nbt);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        return itemStack;
    }


    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> ControllerTileEntity");
        debug.add("      hasMaster: " + glue.hasMaster());
        debug.add("      mob: " + fakeMob);
        return debug;
    }
}
