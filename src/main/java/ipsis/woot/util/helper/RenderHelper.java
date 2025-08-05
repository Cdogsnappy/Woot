package ipsis.woot.util.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class RenderHelper {

    public static void drawTexturedCube(PoseStack poseStack, TextureAtlasSprite texture, float size) {

        if (texture == null)
            return;


        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture.atlasLocation());

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder worldRenderer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);



        float minU = texture.getU0();
        float maxU = texture.getU1();
        float minV = texture.getV0();
        float maxV = texture.getV1();

        Matrix4f matrix = poseStack.last().pose();

        // xy anti-clockwise - front
        worldRenderer.addVertex(matrix, -size, -size, size).setUv(maxU, maxV);
        worldRenderer.addVertex(matrix, size, -size, size).setUv(minU, maxV);
        worldRenderer.addVertex(matrix, size, size, size).setUv(minU, minV);
        worldRenderer.addVertex(matrix, -size, size, size).setUv(maxU, minV);

        // xy clockwise - back
        worldRenderer.addVertex(matrix, -size, -size, -size).setUv(maxU, maxV);
        worldRenderer.addVertex(matrix, -size, size, -size).setUv(maxU, minV);
        worldRenderer.addVertex(matrix, size, size, -size).setUv(minU, minV);
        worldRenderer.addVertex(matrix, size, -size, -size).setUv(minU, maxV);

        // anti-clockwise - left
        worldRenderer.addVertex(matrix, -size, -size, -size).setUv(minU, minV);
        worldRenderer.addVertex(matrix, -size, -size, size).setUv(minU, maxV);
        worldRenderer.addVertex(matrix, -size, size, size).setUv(maxU, maxV);
        worldRenderer.addVertex(matrix, -size, size, -size).setUv(maxU, minV);

        // clockwise - right
        worldRenderer.addVertex(matrix, size, -size, -size).setUv(minU, minV);
        worldRenderer.addVertex(matrix, size, size, -size).setUv(maxU, minV);
        worldRenderer.addVertex(matrix, size, size, size).setUv(maxU, maxV);
        worldRenderer.addVertex(matrix, size, -size, size).setUv(minU, maxV);

        // anticlockwise - top
        worldRenderer.addVertex(matrix, -size, size, -size).setUv(minU, minV);
        worldRenderer.addVertex(matrix, -size, size, size).setUv(minU, maxV);
        worldRenderer.addVertex(matrix, size, size, size).setUv(maxU, maxV);
        worldRenderer.addVertex(matrix, size, size, -size).setUv(maxU, minV);

        // clockwise - bottom
        worldRenderer.addVertex(matrix, -size, -size, -size).setUv(minU, minV);
        worldRenderer.addVertex(matrix, size, -size, -size).setUv(maxU, minV);
        worldRenderer.addVertex(matrix, size, -size, size).setUv(maxU, maxV);
        worldRenderer.addVertex(matrix, -size, -size, size).setUv(minU, maxV);

        worldRenderer.build();
    }

    public static boolean isPointInRegion(int regionX, int regionY, int regionWidth, int regionHeight, double mouseX, double mouseY, int leftPos, int topPos) {
        int relMouseX = (int)(mouseX - leftPos);
        int relMouseY = (int)(mouseY - topPos);
        return relMouseX >= regionX && relMouseX < regionX + regionWidth &&
                relMouseY >= regionY && relMouseY < regionY + regionHeight;
    }

    public static void drawShadedCube(PoseStack poseStack, TextureAtlasSprite texture,float size) {

        if (texture == null)
            return;


        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture.atlasLocation());

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder worldRenderer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);



        float minU = texture.getU0();
        float maxU = texture.getU1();
        float minV = texture.getV0();
        float maxV = texture.getV1();

        Matrix4f matrix = poseStack.last().pose();

        // xy anti-clockwise - front
        worldRenderer.addVertex(matrix, -size, -size, size);
        worldRenderer.addVertex(matrix, size, -size, size);
        worldRenderer.addVertex(matrix, size, size, size);
        worldRenderer.addVertex(matrix, -size, size, size);

        // xy clockwise - back
        worldRenderer.addVertex(matrix, -size, -size, -size);
        worldRenderer.addVertex(matrix, -size, size, -size);
        worldRenderer.addVertex(matrix, size, size, -size);
        worldRenderer.addVertex(matrix, size, -size, -size);

        // anti-clockwise - left
        worldRenderer.addVertex(matrix, -size, -size, -size);
        worldRenderer.addVertex(matrix, -size, -size, size);
        worldRenderer.addVertex(matrix, -size, size, size);
        worldRenderer.addVertex(matrix, -size, size, -size);

        // clockwise - right
        worldRenderer.addVertex(matrix, size, -size, -size);
        worldRenderer.addVertex(matrix, size, size, -size);
        worldRenderer.addVertex(matrix, size, size, size);
        worldRenderer.addVertex(matrix, size, -size, size);

        // anticlockwise - top
        worldRenderer.addVertex(matrix, -size, size, -size);
        worldRenderer.addVertex(matrix, -size, size, size);
        worldRenderer.addVertex(matrix, size, size, size);
        worldRenderer.addVertex(matrix, size, size, -size);

        // clockwise - bottom
        worldRenderer.addVertex(matrix, -size, -size, -size);
        worldRenderer.addVertex(matrix, size, -size, -size);
        worldRenderer.addVertex(matrix, size, -size, size);
        worldRenderer.addVertex(matrix, -size, -size, size);

        worldRenderer.build();
    }
}
