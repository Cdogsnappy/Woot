package ipsis.woot.modules.factory;


import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum FactoryComponent {

    FACTORY_A,
    FACTORY_B,
    FACTORY_C,
    FACTORY_D,
    FACTORY_E,
    FACTORY_CONNECT,
    FACTORY_CTR_BASE_PRI,
    FACTORY_CTR_BASE_SEC,
    FACTORY_UPGRADE,
    HEART,
    CAP_A,
    CAP_B,
    CAP_C,
    CAP_D,
    IMPORT,
    EXPORT,
    CONTROLLER,
    CELL;

    public static FactoryComponent[] VALUES = values();
    public String getName() { return name().toLowerCase(Locale.ROOT); }
    public String getTranslationKey() { return "block.woot." + getName(); }

    public static boolean isSameComponentFuzzy(FactoryComponent componentA, FactoryComponent componentB) {
        return componentA == componentB;
    }

    public Block getBlock() {
        return switch (this) {
            case FACTORY_A -> FactorySetup.FACTORY_A_BLOCK.get();
            case FACTORY_B -> FactorySetup.FACTORY_B_BLOCK.get();
            case FACTORY_C -> FactorySetup.FACTORY_C_BLOCK.get();
            case FACTORY_D -> FactorySetup.FACTORY_D_BLOCK.get();
            case FACTORY_E -> FactorySetup.FACTORY_E_BLOCK.get();
            case FACTORY_CONNECT -> FactorySetup.FACTORY_CONNECT_BLOCK.get();
            case FACTORY_CTR_BASE_PRI -> FactorySetup.FACTORY_CTR_BASE_PRI_BLOCK.get();
            case FACTORY_CTR_BASE_SEC -> FactorySetup.FACTORY_CTR_BASE_SEC_BLOCK.get();
            case FACTORY_UPGRADE -> FactorySetup.FACTORY_UPGRADE_BLOCK.get();
            case HEART -> FactorySetup.HEART_BLOCK.get();
            case CAP_A -> FactorySetup.CAP_A_BLOCK.get();
            case CAP_B -> FactorySetup.CAP_B_BLOCK.get();
            case CAP_C -> FactorySetup.CAP_C_BLOCK.get();
            case CAP_D -> FactorySetup.CAP_D_BLOCK.get();
            case IMPORT -> FactorySetup.IMPORT_BLOCK.get();
            case EXPORT -> FactorySetup.EXPORT_BLOCK.get();
            case CONTROLLER -> FactorySetup.CONTROLLER_BLOCK.get();
            case CELL -> FactorySetup.CELL_1_BLOCK.get();
            default ->
                // Handle unexpected cases - you may want to throw an exception or return null
                    null;
        };
    }

    public List<Block> getBlocks() {
        List<Block> stacks = new ArrayList<>();
        if (this == CELL) {
            stacks.add(FactorySetup.CELL_1_BLOCK.get());
            stacks.add(FactorySetup.CELL_2_BLOCK.get());
            stacks.add(FactorySetup.CELL_3_BLOCK.get());
            stacks.add(FactorySetup.CELL_4_BLOCK.get());
        } else {
            stacks.add(this.getBlock());
        }
        return stacks;
    }

    public ItemStack getItemStack() {
        return switch (this) {
            case FACTORY_A -> new ItemStack(FactorySetup.FACTORY_A_BLOCK.get());
            case FACTORY_B -> new ItemStack(FactorySetup.FACTORY_B_BLOCK.get());
            case FACTORY_C -> new ItemStack(FactorySetup.FACTORY_C_BLOCK.get());
            case FACTORY_D -> new ItemStack(FactorySetup.FACTORY_D_BLOCK.get());
            case FACTORY_E -> new ItemStack(FactorySetup.FACTORY_E_BLOCK.get());
            case FACTORY_CONNECT -> new ItemStack(FactorySetup.FACTORY_CONNECT_BLOCK.get());
            case FACTORY_CTR_BASE_PRI -> new ItemStack(FactorySetup.FACTORY_CTR_BASE_PRI_BLOCK.get());
            case FACTORY_CTR_BASE_SEC -> new ItemStack(FactorySetup.FACTORY_CTR_BASE_SEC_BLOCK.get());
            case FACTORY_UPGRADE -> new ItemStack(FactorySetup.FACTORY_UPGRADE_BLOCK.get());
            case HEART -> new ItemStack(FactorySetup.HEART_BLOCK.get());
            case CAP_A -> new ItemStack(FactorySetup.CAP_A_BLOCK.get());
            case CAP_B -> new ItemStack(FactorySetup.CAP_B_BLOCK.get());
            case CAP_C -> new ItemStack(FactorySetup.CAP_C_BLOCK.get());
            case CAP_D -> new ItemStack(FactorySetup.CAP_D_BLOCK.get());
            case IMPORT -> new ItemStack(FactorySetup.IMPORT_BLOCK.get());
            case EXPORT -> new ItemStack(FactorySetup.EXPORT_BLOCK.get());
            case CONTROLLER -> new ItemStack(FactorySetup.CONTROLLER_BLOCK.get());
            case CELL -> new ItemStack(FactorySetup.CELL_1_BLOCK.get());
            default ->
                // Handle unexpected cases - you may want to throw an exception or return null
                    null;
        };
    }

    public List<ItemStack> getStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        if (this == CELL) {
            stacks.add(new ItemStack(FactorySetup.CELL_1_BLOCK.get()));
            stacks.add(new ItemStack(FactorySetup.CELL_2_BLOCK.get()));
            stacks.add(new ItemStack(FactorySetup.CELL_3_BLOCK.get()));
            stacks.add(new ItemStack(FactorySetup.CELL_4_BLOCK.get()));
        } else {
            stacks.add(this.getItemStack());
        }
        return stacks;
    }

    public BlockState getDefaultBlockState() {
        return switch (this) {
            case FACTORY_A -> FactorySetup.FACTORY_A_BLOCK.get().defaultBlockState();
            case FACTORY_B -> FactorySetup.FACTORY_B_BLOCK.get().defaultBlockState();
            case FACTORY_C -> FactorySetup.FACTORY_C_BLOCK.get().defaultBlockState();
            case FACTORY_D -> FactorySetup.FACTORY_D_BLOCK.get().defaultBlockState();
            case FACTORY_E -> FactorySetup.FACTORY_E_BLOCK.get().defaultBlockState();
            case FACTORY_CONNECT -> FactorySetup.FACTORY_CONNECT_BLOCK.get().defaultBlockState();
            case FACTORY_CTR_BASE_PRI -> FactorySetup.FACTORY_CTR_BASE_PRI_BLOCK.get().defaultBlockState();
            case FACTORY_CTR_BASE_SEC -> FactorySetup.FACTORY_CTR_BASE_SEC_BLOCK.get().defaultBlockState();
            case FACTORY_UPGRADE -> FactorySetup.FACTORY_UPGRADE_BLOCK.get().defaultBlockState();
            case CAP_A -> FactorySetup.CAP_A_BLOCK.get().defaultBlockState();
            case CAP_B -> FactorySetup.CAP_B_BLOCK.get().defaultBlockState();
            case CAP_C -> FactorySetup.CAP_C_BLOCK.get().defaultBlockState();
            case CAP_D -> FactorySetup.CAP_D_BLOCK.get().defaultBlockState();
            case IMPORT -> FactorySetup.IMPORT_BLOCK.get().defaultBlockState();
            case EXPORT -> FactorySetup.EXPORT_BLOCK.get().defaultBlockState();
            case CONTROLLER -> FactorySetup.CONTROLLER_BLOCK.get().defaultBlockState();
            case CELL -> FactorySetup.CELL_1_BLOCK.get().defaultBlockState();
            default -> FactorySetup.HEART_BLOCK.get().defaultBlockState();
        };
    }
}
