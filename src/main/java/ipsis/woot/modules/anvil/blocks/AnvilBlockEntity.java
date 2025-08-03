package ipsis.woot.modules.anvil.blocks;

import ipsis.woot.crafting.anvil.AnvilRecipe;
import ipsis.woot.mod.ModNBT;
import ipsis.woot.modules.anvil.AnvilSetup;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.factory.blocks.ControllerBlockEntity;
import ipsis.woot.modules.factory.items.MobShardItem;
import ipsis.woot.util.FakeMob;
import ipsis.woot.util.WootDebug;
import ipsis.woot.util.helper.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;


public class AnvilBlockEntity extends BlockEntity implements WootDebug {

    public AnvilBlockEntity(BlockPos pos, BlockState blockState) {
        super(AnvilSetup.ANVIL_BLOCK_TILE.get(), pos, blockState);
    }

    private ItemStack baseItem = ItemStack.EMPTY;
    private ItemStack[] ingredients = { ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY };

    public boolean hasBaseItem() { return !baseItem.isEmpty(); }

    public void setBaseItem(ItemStack itemStack) {
        if (itemStack.isEmpty())
            return;

        this.baseItem = itemStack.copy();
        setChanged();
        if (level != null)
            WorldHelper.updateClient(level, getBlockPos());
    }

    public ItemStack[] getIngredients() { return ingredients; }

    public ItemStack getBaseItem() { return baseItem.copy(); }
    public boolean addIngredient(ItemStack itemStack) {
        if (itemStack.isEmpty())
            return false;

        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i].isEmpty()) {
                ingredients[i] = itemStack.copy();
                setChanged();
                if (level != null)
                    WorldHelper.updateClient(level, getBlockPos());
                return true;
            }
        }

        return false;
    }

    public void dropContents(Level world, BlockPos pos) {
        if (!baseItem.isEmpty()) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), baseItem);
            baseItem = ItemStack.EMPTY;
        }

        for (int i = 0; i < ingredients.length; i++) {
            if (!ingredients[i].isEmpty()) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ingredients[i]);
                ingredients[i] = ItemStack.EMPTY;
            }
        }

        setChanged();
        if (world != null)
            WorldHelper.updateClient(world, pos);
    }

    public void dropItem(Player playerEntity) {

        ItemStack itemStack = ItemStack.EMPTY;

        for (int i = 0; i < ingredients.length; i++) {
            if (!ingredients[i].isEmpty()) {
                itemStack = ingredients[i].copy();
                ingredients[i] = ItemStack.EMPTY;
                break;
            }
        }

        if (itemStack.isEmpty() && !baseItem.isEmpty()) {
            itemStack = baseItem;
            baseItem = ItemStack.EMPTY;
        }

        if (!itemStack.isEmpty()) {
            setChanged();
            if (this.level != null)
                WorldHelper.updateClient(level, getBlockPos());

            if (!playerEntity.getInventory().add(itemStack)) {
                Containers.dropItemStack(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), itemStack);
            } else {
                playerEntity.containerMenu.broadcastChanges();
            }
        }
    }

    public void tryCraft(Player playerEntity) {

        /**
         * Check anvil is hot
         */
        if (!AnvilSetup.ANVIL_BLOCK.get().isAnvilHot(level, getBlockPos())) {
            playerEntity.sendSystemMessage(Component.translatable("chat.woot.anvil.cold"));
            return;
        }

        AnvilRecipe recipe = level.getRecipeManager().getRecipeFor(ANVIL_TYPE,
                new BlockContainer(baseItem, ingredients[0], ingredients[1], ingredients[2], ingredients[3]), level).orElse(null);
        if (recipe == null)
            return;

        ItemStack output = recipe.output();
        /**
         * Handle the shard programming
         */
        if (baseItem.getItem() == FactorySetup.MOB_SHARD_ITEM.get()) {
            FakeMob fakeMob = MobShardItem.getProgrammedMob(baseItem);
            output = ControllerBlockEntity.getItemStack(fakeMob);
        }

        baseItem = ItemStack.EMPTY;
        Arrays.fill(ingredients, ItemStack.EMPTY);

        setChanged();
        WorldHelper.updateClient(level, getBlockPos());
        ItemEntity itemEntity = new ItemEntity(level,
                getBlockPos().getX(), getBlockPos().getY() + 1, getBlockPos().getZ(),
                output);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    /**
     * NBT
     */
    @Override
    public void loadAdditional(CompoundTag compoundNBT, HolderLookup.Provider registries) {
        super.loadAdditional(compoundNBT, registries);
        readFromNBT(compoundNBT);
    }


    private void readFromNBT(CompoundTag compoundNBT) {
        if (compoundNBT.contains(ModNBT.Anvil.BASE_ITEM_TAG)) {
            ListTag listNBT = compoundNBT.getList(ModNBT.INVENTORY_TAG, 10);
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundTag itemTags = listNBT.getCompound(i);
                int j = itemTags.getInt(ModNBT.INVENTORY_SLOT_TAG);
                if (j >= 0 && j < ingredients.length) {
                    ingredients[j] = ItemStack.parse(level.registryAccess(), itemTags).get();
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag compoundNBT, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compoundNBT, lookupProvider);

        if (!baseItem.isEmpty()) {
            CompoundTag compoundNBT1 = new CompoundTag();
            baseItem.save(lookupProvider, compoundNBT1);
            compoundNBT.put(ModNBT.Anvil.BASE_ITEM_TAG, compoundNBT1);

            ListTag listNBT = new ListTag();
            for (int i = 0; i < ingredients.length; i++) {
                if (!ingredients[i].isEmpty()) {
                    CompoundTag itemTags = new CompoundTag();
                    itemTags.putInt(ModNBT.INVENTORY_SLOT_TAG, i);
                    ingredients[i].save(lookupProvider, itemTags);
                    listNBT.add(itemTags);
                }
            }
            compoundNBT.put(ModNBT.INVENTORY_TAG, listNBT);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, level.registryAccess());
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag compoundNBT = new CompoundTag();
        this.saveAdditional(compoundNBT, level.registryAccess());
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        loadAdditional(pkt.getTag(), lookupProvider);
    }

    /**
     * WootDebug
     */
    @Override
    public List<String> getDebugText(List<String> debug, UseOnContext itemUseContext) {
        debug.add("====> AnvilTileEntity");
        debug.add("      base: " + baseItem);
        for (int i = 0; i < ingredients.length; i++)
            debug.add("      ingredient: " + ingredients[i]);
        return debug;
    }
}
