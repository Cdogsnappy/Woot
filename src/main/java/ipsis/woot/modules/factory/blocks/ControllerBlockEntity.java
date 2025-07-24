package ipsis.woot.modules.factory.blocks;

import ipsis.woot.config.Config;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.modules.factory.multiblock.MultiBlockBlockEntity;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.WootDebug;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class ControllerBlockEntity extends MultiBlockBlockEntity implements WootDebug {

    public ControllerBlockEntity() {
        super(FactorySetup.CONTROLLER_BLOCK_TILE.get());
    }

    private FakeMob fakeMob = new FakeMob();

    /**
     * NBT
     */
    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        super.deserializeNBT(compoundNBT);
        readFromNBT(compoundNBT);
    }

    @Override
    public CompoundNBT write(CompoundNBT compoundNBT) {
        super.write(compoundNBT);
        CompoundNBT nbt = new CompoundNBT();
        FakeMob.writeToNBT(fakeMob, nbt);
        compoundNBT.put(ModNBT.Controller.MOB_TAG, nbt);
        return compoundNBT;
    }

    @Override
    public void read(BlockState blockState, CompoundNBT compoundNBT) {
        super.read(blockState, compoundNBT);
        readFromNBT(compoundNBT);
    }

    private void readFromNBT(CompoundNBT compoundNBT) {
        if (compoundNBT.contains(ModNBT.Controller.MOB_TAG)) {
            CompoundNBT nbt = compoundNBT.getCompound(ModNBT.Controller.MOB_TAG);
            fakeMob = new FakeMob(nbt);
        }
    }

    public FakeMob getFakeMob() {
        return fakeMob;
    }

    public Tier getTier() {
        if (fakeMob == null || !fakeMob.isValid())
            return Tier.UNKNOWN;

        return Config.OVERRIDE.getMobTier(fakeMob, world);
    }

    public static ItemStack getItemStack(FakeMob fakeMob) {
        ItemStack itemStack = new ItemStack(FactorySetup.CONTROLLER_BLOCK.get());

        /**
         * setTileEntityNBT
         */
        CompoundTag compoundNBT = itemStack.save("BlockEntityTag");
        CompoundTag nbt = new CompoundTag();
        FakeMob.writeToNBT(fakeMob, nbt);
        compoundNBT.put(ModNBT.Controller.MOB_TAG, nbt);
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
