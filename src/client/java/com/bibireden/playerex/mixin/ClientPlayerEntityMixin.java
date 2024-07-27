package com.bibireden.playerex.mixin;

import com.bibireden.playerex.ui.PlayerEXScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin {
    @Shadow @Final protected MinecraftClient client;

    @Inject(method = "setExperience", at = @At("TAIL"))
    private void setExperience(CallbackInfo ci) {
        if (client.currentScreen instanceof PlayerEXScreen screen) screen.onExperienceUpdated();
    }
}
