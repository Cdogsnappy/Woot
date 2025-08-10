package ipsis.woot.modules.layout.blocks;

import ipsis.woot.modules.factory.Tier;
import ipsis.woot.modules.factory.layout.AbsolutePattern;
import ipsis.woot.modules.factory.layout.PatternRepository;
import ipsis.woot.modules.layout.LayoutSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;


import javax.annotation.Nullable;

public class LayoutBlockEntity extends BlockEntity {

    private static final int LAYOUT_Y_OFFSET = 1;
    public LayoutBlockEntity(BlockPos pos, BlockState state) {
        super(LayoutSetup.LAYOUT_BLOCK_TILE.get(), pos, state);
    }

    public int getYLevel() { return yLevel; }
    public int setNextLevel() {
        yLevel++;
        if (yLevel >= PatternRepository.get().getPattern(tier).getHeight())
            yLevel = -1;
        setChanged();
        refresh();
        return yLevel;
    }

    public int getYForLevel() {
        // Heart is one off the top
        // Layout is offset from the heart
        int height = PatternRepository.get().getPattern(tier).getHeight();
        return getBlockPos().getY() - height - LAYOUT_Y_OFFSET + yLevel + 2;
    }

    public Tier getTier() { return tier; }

    public Tier setNextTier() {
        tier = tier.getNextValid();
        setChanged();
        refresh();
        return tier;
    }

    AbsolutePattern absolutePattern = null;
    public void refresh() {
        if(!level.isClientSide)
            return;

        BlockPos origin = getBlockPos().below(LAYOUT_Y_OFFSET);
        absolutePattern = AbsolutePattern.create(level, tier, origin, level.getBlockState(getBlockPos()).getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider){
        this.loadAdditional(tag, lookupProvider);
    }

    public AbsolutePattern getAbsolutePattern() { return absolutePattern; }

    /**
     * Client sync
     * S:getUpdatePacket() -> C:onDataPacket()
     */
    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag compoundNBT = getUpdateTag(level.registryAccess());
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(pkt.getTag(), lookupProvider);
        refresh();
    }

    /**
     * Initial chunk load
     * S:getUpdateTag() -> C:handleUpdateTag()
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProviders) {
        CompoundTag compoundNBT = super.getUpdateTag(level.registryAccess());
        this.saveAdditional(compoundNBT, level.registryAccess());
        return compoundNBT;
    }

    /**
     * NBT
     */
    Tier tier = Tier.TIER_1;
    int yLevel = -1; // yLevel in the structure to show
    static final String KEY_LEVEL = "yLevel";
    static final String KEY_TIER = "tier";

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(KEY_LEVEL, yLevel);
        tag.putInt(KEY_TIER, tier.ordinal());
    }


    @Override
    public void loadAdditional(CompoundTag compoundNBT, HolderLookup.Provider registries) {
        super.loadAdditional(compoundNBT, registries);
        readFromNBT(compoundNBT);
    }

    private void readFromNBT(CompoundTag compound) {
        yLevel = Math.clamp(compound.getInt(KEY_LEVEL), -1, 16);
        tier = Tier.byIndex(compound.getInt(KEY_TIER));
    }
}
