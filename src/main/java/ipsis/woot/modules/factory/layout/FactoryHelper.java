package ipsis.woot.modules.factory.layout;

import ipsis.woot.advancements.Advancements;
import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.modules.factory.multiblock.MultiBlockGlueProvider;
import ipsis.woot.modules.factory.multiblock.MultiBlockMaster;
import ipsis.woot.util.helper.PlayerHelper;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.BlockSnapshot;


import java.util.ArrayList;
import java.util.List;

public class FactoryHelper {

    public static void disconnectOld(Level world, AbsolutePattern absolutePattern) {
        for (PatternBlock pb : absolutePattern.getBlocks()) {
            if (world.isLoaded(pb.getBlockPos())) {
                BlockEntity te = world.getBlockEntity(pb.getBlockPos());
                if (te instanceof MultiBlockGlueProvider)
                    ((MultiBlockGlueProvider)te).getGlue().clearMaster();
            }
        }
    }

    public static void connectNew(Level world, AbsolutePattern absolutePattern, MultiBlockMaster master) {
        for (PatternBlock pb : absolutePattern.getBlocks()) {
            if (pb.getFactoryComponent() == FactoryComponent.CONTROLLER && !absolutePattern.isValidControllerPos(pb.getBlockPos()))
                continue;

            if (world.isLoaded(pb.getBlockPos())) {
                BlockEntity te = world.getBlockEntity(pb.getBlockPos());
                if (te instanceof MultiBlockGlueProvider)
                    ((MultiBlockGlueProvider)te).getGlue().setMaster(master);
            }
        }
    }

    static boolean isCorrectBlock(Block block, List<Block> validBlocks) {
        return validBlocks.contains(block);
    }


    public enum BuildResult {
        ERROR,
        SUCCESS,
        NO_BLOCK_IN_INV,
        ALL_BLOCKS_PLACED
    }
    public static BuildResult tryBuild(Level world, BlockPos pos, Player playerEntity, Direction facing, Tier tier) {
        if (!playerEntity.mayBuild()) {
            playerEntity.sendSystemMessage(
                    Component.translatable("chat.woot.intern.noedit"));
            return BuildResult.ERROR;
        }

        boolean allPlaced = true;
        AbsolutePattern absolutePattern = AbsolutePattern.create(world, tier, pos, facing);
        for (PatternBlock pb : absolutePattern.getBlocks()) {
            BlockState currState = world.getBlockState(pb.getBlockPos());
            Block currBlock = currState.getBlock();

            List<Block> correctBlocks = pb.getFactoryComponent().getBlocks();
            List<ItemStack> correctItemStacks = pb.getFactoryComponent().getStacks();

            // Is it the correct block already
            if (isCorrectBlock(currBlock, correctBlocks))
                continue;

            if (pb.getFactoryComponent() == FactoryComponent.CONTROLLER || pb.getFactoryComponent() == FactoryComponent.CELL)
                continue;

            allPlaced = false;

            Block placeBlock = correctBlocks.get(0);
            if (!PlayerHelper.playerHasFactoryComponent(playerEntity, correctItemStacks)) {
                playerEntity.sendSystemMessage(
                        Component.translatable("chat.woot.intern.missingblock",
                                StringHelper.translate(pb.getFactoryComponent().getTranslationKey())), false);
                return BuildResult.NO_BLOCK_IN_INV;
            }
            if (world.isModifiable(playerEntity, pb.getBlockPos()) && (world.isAirBlock(pb.getBlockPos()) || currState.getMaterial().isReplaceable())) {
                ItemStack takenStack = PlayerHelper.takeFactoryComponent(playerEntity, correctItemStacks);
                if (!takenStack.isEmpty()) {

                    BlockSnapshot blockSnapshot = BlockSnapshot.create(world.dimension(), world, pos);
                    world.setBlock(pb.getBlockPos(), placeBlock.defaultBlockState(), 11);
                    if (ForgeEventFactory.onBlockPlace(playerEntity, blockSnapshot, Direction.UP)) {
                        blockSnapshot.restore(true, false);
                        return BuildResult.ERROR;
                    }
                }
                return BuildResult.SUCCESS;
            } else {
                // cannot replace block
                playerEntity.sendSystemMessage(
                        new TranslationTextComponent("chat.woot.intern.noreplace",
                                StringHelper.translate(pb.getFactoryComponent().getTranslationKey()),
                                pb.getBlockPos().getX(), pb.getBlockPos().getY(), pb.getBlockPos().getZ()), false);
                return BuildResult.ERROR;
            }
        }
        return allPlaced ? BuildResult.ALL_BLOCKS_PLACED : BuildResult.ERROR;
    }

    public static void tryValidate(Level world, BlockPos pos, Player playerEntity, Direction facing, Tier tier) {

        playerEntity.sendSystemMessage(
                new TranslationTextComponent(
                "chat.woot.intern.validate.start",
                        StringHelper.translate(tier.getTranslationKey())),
                true);

        List<String> feedback = new ArrayList<>();
        AbsolutePattern absolutePattern = AbsolutePattern.create(world, tier, pos, facing);
        if (!FactoryScanner.compareToWorld(absolutePattern, world, feedback)) {
            playerEntity.sendSystemMessage(Component.literal("----"));
            playerEntity.sendSystemMessage(
                    Component.translatable(
                    "chat.woot.intern.validate.invalid", StringHelper.translate(tier.getTranslationKey())));
            for (String s : feedback)
                playerEntity.sendSystemMessage(Component.literal(s));
            playerEntity.sendSystemMessage(Component.literal("----"));
        } else {
            playerEntity.sendSystemMessage(
                    Component.translatable(
                    "chat.woot.intern.validate.valid", StringHelper.translate(tier.getTranslationKey())));
            if (playerEntity instanceof ServerPlayer)
                Advancements.TIER_VALIDATE_TRIGGER.trigger((ServerPlayer)playerEntity, tier);
        }
    }
}
