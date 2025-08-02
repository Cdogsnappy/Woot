package ipsis.woot.modules.layout;

import com.mojang.blaze3d.vertex.PoseStack;
import ipsis.woot.modules.factory.layout.PatternRepository;
import ipsis.woot.modules.layout.blocks.LayoutBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.IBlockEntityRendererExtension;

public class LayoutRenderer implements IBlockEntityRendererExtension<LayoutBlockEntity> {

    @Override
    public AABB getRenderBoundingBox(LayoutBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(
                pos.offset(-PatternRepository.get().getMaxXZOffset(), -1, -PatternRepository.get().getMaxXZOffset())
        );
    }
}
