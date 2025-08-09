package ipsis.woot.modules.layout.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ipsis.woot.modules.factory.FactoryComponent;
import ipsis.woot.modules.factory.FactorySetup;
import ipsis.woot.modules.layout.LayoutSetup;
import ipsis.woot.modules.layout.blocks.LayoutBlockEntity;
import ipsis.woot.modules.factory.layout.PatternBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;


import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.EmptyModel;
import net.neoforged.neoforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class LayoutTileEntitySpecialRenderer implements BlockEntityRenderer<LayoutBlockEntity> {




    void textureRender(LayoutBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        //boolean showAll = tileEntityIn.getLevel().get == -1;
        boolean showAll = true;
        int validY = showAll ? 0 : tileEntityIn.getYForLevel();
        BlockPos origin = tileEntityIn.getBlockPos();
        Direction facing = Direction.SOUTH;

        // Watch for this being called after the block is broken
        // Ensure that we still have a Layout Block at the position to extract the facing from
        Block layoutBlock = tileEntityIn.getLevel().getBlockState(origin).getBlock();
        if (layoutBlock == LayoutSetup.LAYOUT_BLOCK.get())
            facing = tileEntityIn.getLevel().getBlockState(origin).getValue(BlockStateProperties.HORIZONTAL_FACING);

        float minX = 0.0F, minY = 0.0F, minZ = 0.0F;
        float maxX = 0.0F, maxY = 0.0F, maxZ = 0.0F;
        matrixStack.pushPose();
        {
            matrixStack.translate(0.0F, 0.0F, 0.0F);
            for (PatternBlock block : tileEntityIn.getAbsolutePattern().getBlocks()) {
                if (!showAll && block.getBlockPos().getY() != validY)
                    continue;

                matrixStack.pushPose();
                {
                    float x = (origin.getX() - block.getBlockPos().getX()) * -1.0F;
                    float y = (origin.getY() - block.getBlockPos().getY()) * -1.0F;
                    float z = (origin.getZ() - block.getBlockPos().getZ()) * -1.0F;
                    matrixStack.translate(x, y, z);
                    minX = x < minX ? x : minX;
                    minY = y < minY ? y : minY;
                    minZ = z < minZ ? z : minZ;
                    maxX = x > maxX ? x : maxX;
                    maxY = y > maxY ? y : maxY;
                    maxZ = z > maxZ ? z : maxZ;

                    BlockState blockState = block.getFactoryComponent().getDefaultBlockState();
                    if (block.getFactoryComponent() == FactoryComponent.HEART)
                        blockState = FactorySetup.HEART_BLOCK.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);

                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState,
                            matrixStack, bufferIn, 0x00f000f0, combinedOverlayIn, ModelData.EMPTY, RenderType.LINES);
                }
                matrixStack.popPose();;
            }
        }
        matrixStack.popPose();
        matrixStack.pushPose();
        {
            maxX += 1.0F;
            maxY += 1.0F;
            maxZ += 1.0F;
            matrixStack.translate(0.0F, 0.0F, 0.0F);
            VertexConsumer iVertexBuilder = bufferIn.getBuffer(RenderType.LINES);
            LevelRenderer.renderLineBox(matrixStack, iVertexBuilder,
                minX, minY, minZ, maxX, maxY, maxZ,
               0.9F, 0.9F, 0.9F, 1.0F, 0.5F, 0.5F, 0.5F);
        }
        matrixStack.popPose();
    }

    @Override
    public void render(LayoutBlockEntity layoutBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        Level world = layoutBlockEntity.getLevel();
        if (world != null) {
            if (layoutBlockEntity.getAbsolutePattern() == null)
                layoutBlockEntity.refresh();

            textureRender(layoutBlockEntity, v, poseStack, multiBufferSource, i, i1);
        }
    }

    public static final Dispatcher DISPATCHER = new Dispatcher();

    public static class Dispatcher implements BlockEntityRendererProvider<LayoutBlockEntity>{

        @Override
        public BlockEntityRenderer<LayoutBlockEntity> create(Context context) {
            return new LayoutTileEntitySpecialRenderer();
        }
    }
}
