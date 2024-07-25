package com.bibireden.playerex.mixin;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.PlayerEX;
import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin<T extends Entity> {
    @Unique
    private boolean playerex$shouldRenderLevel() { return PlayerEX.CONFIG.getShowLevelOnNameplates(); }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"), index = 1)
    private Text playerex$renderLabelIfPresent(Text text, @Local(argsOnly = true) Entity entity) {
        if (playerex$shouldRenderLevel() && entity instanceof PlayerEntity livingEntity) {
            Optional<Double> maybeLevel = DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, livingEntity);
            if (maybeLevel.isPresent()) {
                text = text.copy().append(" ").append(Text.translatable("playerex.ui.nameplate.level", maybeLevel.get().intValue()).styled((style) -> style.withColor(0xFFAA00)));
            }
        }
        return text;
    }
}
