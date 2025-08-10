package ipsis.woot.modules.layout.items;

import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.Tier;
import ipsis.woot.modules.factory.blocks.HeartBlock;
import ipsis.woot.modules.factory.layout.FactoryHelper;
import ipsis.woot.modules.factory.layout.PatternRepository;
import ipsis.woot.util.helper.RandomHelper;
import ipsis.woot.util.helper.StringHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;


import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

/**
 * This is the main tool for the mod.
 * It is used to construct, validate and form the factory.
 */
public class InternItem extends Item {

    public InternItem() {
        super(new Item.Properties().stacksTo(1));
    }

    public enum ToolMode {
        BUILD_1(Tier.TIER_1),
        BUILD_2(Tier.TIER_2),
        BUILD_3(Tier.TIER_3),
        BUILD_4(Tier.TIER_4),
        BUILD_5(Tier.TIER_5),
        VALIDATE_1(Tier.TIER_1),
        VALIDATE_2(Tier.TIER_2),
        VALIDATE_3(Tier.TIER_3),
        VALIDATE_4(Tier.TIER_4),
        VALIDATE_5(Tier.TIER_5);

        public static ToolMode[] VALUES = values();
        private static EnumSet<ToolMode> BUILD_MODES = EnumSet.range(BUILD_1, BUILD_5);
        private static EnumSet<ToolMode> VALIDATE_TIERS = EnumSet.range(VALIDATE_1, VALIDATE_5);

        public ToolMode getNext() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }

        public boolean isBuildMode() { return BUILD_MODES.contains(this); }
        public boolean isValidateMode() { return VALIDATE_TIERS.contains(this); }

        Tier tier;
        ToolMode(Tier tier) {
            this.tier = tier;
        }

        public Tier getTier() { return this.tier; }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context){
        return useOnFirst(context);
        /**
        if(firstUse(context.getItemInHand())){
            return useOnFirst(context);
        }
        Player playerEntity = context.getPlayer();
        Level world = context.getLevel();
        ItemStack itemStack = context.getItemInHand();
        playerEntity.startUsingItem(context.getHand());
        if (!context.getLevel().isClientSide) {
            if (playerEntity.isCrouching()) {
                HitResult rayTraceResult = getPlayerPOVHitResult(world, playerEntity, ClipContext.Fluid.NONE);
                if (rayTraceResult.getType() == HitResult.Type.BLOCK)
                    return super.useOn(context);

                ToolMode mode = getToolModeFromStack(itemStack);
                mode = mode.getNext();
                setToolModeInStack(itemStack, mode);
                if (mode.isBuildMode()) {
                    playerEntity.sendSystemMessage(
                            Component.translatable(
                                    "info.woot.intern.mode.build",
                                    StringHelper.translate(mode.getTier().getTranslationKey())));
                } else if (mode.isValidateMode()) {
                    playerEntity.sendSystemMessage(
                            Component.translatable(
                                    "info.woot.intern.mode.validate",
                                    StringHelper.translate(mode.getTier().getTranslationKey())));
                }
            }
        }
        return InteractionResult.SUCCESS;
         **/
    }

    private boolean firstUse(ItemStack stack){
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = data == null ? new CompoundTag() : data.copyTag();
        if(tag.contains("USEDONCE")){
            return false;
        }
        tag.putInt("USEDONCE", 1);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return true;
    }

    /**
     * NBT
     */
    private static final String NBT_MODE = "mode";
    private static void setToolModeInStack(ItemStack itemStack, ToolMode toolMode) {
        CompoundTag compound = itemStack.get(DataComponents.CUSTOM_DATA).copyTag();

        compound.putString(NBT_MODE, toolMode.name());
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
    }

    private static ToolMode getToolModeFromStack(ItemStack itemStack) {
        ToolMode mode = ToolMode.BUILD_1; // default
        CompoundTag compound = itemStack.get(DataComponents.CUSTOM_DATA).copyTag();
        if (!compound.contains(NBT_MODE)) {
            setToolModeInStack(itemStack, mode);
        } else {
            try {
                mode = ToolMode.valueOf(compound.getString(NBT_MODE));
            } catch (Exception e) {
                setToolModeInStack(itemStack, mode);
            }
        }
        return mode;
    }

    public InteractionResult useOnFirst(UseOnContext context) {

        InteractionResult result = InteractionResult.PASS;
        ItemStack itemStack = context.getItemInHand();

        if (!context.getPlayer().isCrouching() && !context.getLevel().isClientSide) {
            Block b = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
            if (b instanceof HeartBlock) {
                BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
                Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                ToolMode toolMode = getToolModeFromStack(itemStack);
                if (toolMode.isBuildMode() && context.getPlayer().mayInteract(context.getLevel(), context.getClickedPos())) {
                    FactoryHelper.BuildResult buildResult = (FactoryHelper.tryBuild(context.getLevel(), context.getClickedPos(), context.getPlayer(), facing, toolMode.getTier()));
                    if (buildResult == FactoryHelper.BuildResult.SUCCESS) {
                        context.getLevel().playSound(
                                null,
                                context.getPlayer().getX(),
                                context.getPlayer().getY(),
                                context.getPlayer().getZ(),
                                SoundEvents.AMETHYST_BLOCK_PLACE,
                                SoundSource.BLOCKS,
                                1.0F,
                                0.5F * ((RandomHelper.RANDOM.nextFloat() - RandomHelper.RANDOM.nextFloat()) * 0.7F + 1.8F));
                    } else if (buildResult == FactoryHelper.BuildResult.ALL_BLOCKS_PLACED) {
                        FactoryHelper.tryValidate(context.getLevel(), context.getClickedPos(), context.getPlayer(), facing, toolMode.getTier());
                    } else {
                        context.getLevel().playSound(
                                null,
                                context.getPlayer().getX(),
                                context.getPlayer().getY(),
                                context.getPlayer().getZ(),
                                SoundEvents.ANVIL_DESTROY,
                                SoundSource.BLOCKS,
                                1.0F, 1.0F);
                    }
                    result = InteractionResult.SUCCESS;
                } else if (toolMode.isValidateMode()) {
                    if (!context.getLevel().isClientSide)
                        FactoryHelper.tryValidate(context.getLevel(), context.getClickedPos(), context.getPlayer(), facing, toolMode.getTier());
                    result = InteractionResult.SUCCESS;
                }
            }
        }

        // Returning SUCCESS will filter out the MAIN_HAND hand from onBlockActivated
        return InteractionResult.PASS;
    }

    @OnlyIn(Dist.CLIENT)
    void spawnParticle(Level world, BlockPos pos, int amount) {
        BlockState blockState = world.getBlockState(pos);
        Block b = world.getBlockState(pos).getBlock();

        // Based off the BoneMealItem code
        if (b.isEmpty(blockState)) {
            for(int i = 0; i < amount; ++i) {
                double d0 = RandomHelper.RANDOM.nextGaussian() * 0.02D;
                double d1 = RandomHelper.RANDOM.nextGaussian() * 0.02D;
                double d2 = RandomHelper.RANDOM.nextGaussian() * 0.02D;
                world.addParticle(ParticleTypes.HAPPY_VILLAGER,
                        (double)((float)pos.getX() + RandomHelper.RANDOM.nextFloat()),
                        (double)((float)pos.getY() + RandomHelper.RANDOM.nextFloat()),
                        (double)((float)pos.getZ() + RandomHelper.RANDOM.nextFloat()),
                        d0, d1, d2);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable(StringHelper.translate("info.woot.intern")));
        tooltip.add(Component.translatable(StringHelper.translate("info.woot.intern.0")));
        tooltip.add(Component.translatable(StringHelper.translate("info.woot.intern.1")));

        ToolMode toolMode = getToolModeFromStack(stack);

        if (toolMode.isBuildMode()) {
            tooltip.add(Component.translatable(StringHelper.translateFormat(
                            "info.woot.intern.mode.build",
                            StringHelper.translate(toolMode.getTier().getTranslationKey()))));
            PatternRepository.Pattern pattern = PatternRepository.get().getPattern(toolMode.getTier());
            if (pattern != null) {
                for (FactoryComponent component : FactoryComponent.VALUES) {
                    int count = pattern.getFactoryBlockCount((component));
                    if (count > 0) {
                        String key = "info.woot.intern.other.count";
                        Component text = Component.translatable(component.getTranslationKey());
                        if (component == FactoryComponent.CELL) {
                            text = Component.translatable("info.woot.intern.cell");
                        } else if (toolMode == ToolMode.BUILD_1 && component == FactoryComponent.CONTROLLER) {
                            key = "info.woot.intern.controller.count.0";
                        } else if (component == FactoryComponent.CONTROLLER) {
                            key = "info.woot.intern.controller.count.1";
                        }

                        tooltip.add(Component.translatable(key, count, text));
                    }
                }
            }
        } else if (toolMode.isValidateMode()) {
            tooltip.add(Component.translatable(StringHelper.translateFormat(
                    "info.woot.intern.mode.validate",
                    StringHelper.translate(toolMode.getTier().getTranslationKey()))));
        }
    }
}
