package com.example.likehearts.mixin;

import com.example.likehearts.HeartStore;
import com.example.likehearts.HeartText;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * После обычной отрисовки неймплейта добавляет над ним ещё одну строку — сердечко.
 * Строка рисуется тем же способом, что и сам ник, поэтому ведёт себя так же
 * (поворот к камере, видимость сквозь блоки, приседание, освещение).
 */
@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin {

    /** Расстояние между строками неймплейта в блоках: 9 * 1.15 * 0.025. */
    private static final float LINE_STEP = 0.25875F;

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("TAIL")
    )
    private void likehearts$submitHeart(EntityRenderState state, PoseStack poseStack,
                                        SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        if (!(state instanceof AvatarRenderState) || state.nameTag == null || state.nameTagAttachment == null) {
            return;
        }
        int type = HeartStore.typeFor(state.nameTag.getString());
        if (type <= 0) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, LINE_STEP * 1.1F, 0.0F);
        collector.order(0).submitNameTag(poseStack, state.nameTagAttachment, 0, HeartText.nameplate(type),
                !state.isDiscrete, state.lightCoords, state.distanceToCameraSq, camera);
        poseStack.popPose();
    }
}
