package ipsis.woot.simulator.tartarus;

import ipsis.woot.simulator.spawning.SpawnController;
import ipsis.woot.util.FakeMobKey;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Cell {

    private BlockPos origin; // Bottom left-hand block position of cell
    private BlockPos spawnPos;
    private AABB axisAlignedBB;
    private FakeMobKey fakeMobKey;

    public Cell(BlockPos origin) {
        this.origin = origin;
        spawnPos = origin.offset(4, 4, 4);
        fakeMobKey = null;
        axisAlignedBB = null;
    }

    public boolean isOccupied() { return fakeMobKey != null; }
    public void free() { fakeMobKey = null; }
    public @Nullable FakeMobKey getOccupant() { return fakeMobKey; }

    public boolean setMob(@Nonnull FakeMobKey fakeMobKey) {
        if (isOccupied())
            return false;

        this.fakeMobKey = fakeMobKey;
        if (axisAlignedBB == null)
            axisAlignedBB = new AABB(spawnPos).inflate(3); // 6x6x6 cell
        return true;
    }

    public void clean(@Nonnull Level world) {
        /**
         * Remove everything from the cell.
         * This should catch any entity that spawns entities on death
         */
        for (LivingEntity livingEntity : world.getEntities(EntityTypeTest.forClass(LivingEntity.class), axisAlignedBB, Entity::isAlive)) {
            livingEntity.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    public @Nonnull List<ItemStack> sweep(@Nonnull Level world) {
        List<ItemStack> drops = new ArrayList<>();
        if (isOccupied()) {
            for (ItemEntity itemEntity : world.getEntities(EntityTypeTest.forClass(ItemEntity.class), axisAlignedBB, Entity::isAlive)) {
                drops.add(itemEntity.getItem().copy());
                itemEntity.lifespan = 0;
            }
        }
        return drops;
    }

    public void run(@Nonnull Level world) {
        if (isOccupied() && world instanceof ServerLevel)
            SpawnController.get().spawnKill(fakeMobKey, (ServerLevel) world, spawnPos);
    }

}
