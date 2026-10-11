package com.example.likehearts.mixin;

import com.example.likehearts.HeartStore;
import com.example.likehearts.HeartText;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Добавляет сердечко слева от ника в списке игроков (таб). */
@Mixin(PlayerTabOverlay.class)
abstract class PlayerTabOverlayMixin {

    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void likehearts$addHeart(PlayerInfo info, CallbackInfoReturnable<Component> cir) {
        Component original = cir.getReturnValue();
        if (original == null) {
            return;
        }
        int type = HeartStore.typeFor(original.getString());
        if (type <= 0) {
            return;
        }
        cir.setReturnValue(Component.empty()
                .append(HeartText.tab(type))
                .append(Component.literal(" "))
                .append(original));
    }
}
