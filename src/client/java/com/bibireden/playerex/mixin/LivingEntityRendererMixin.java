package com.bibireden.playerex.mixin;


import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.PlayerEX;
import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {
    @Unique
    private static final double PLAYEREX$MAX_LEVEL_DISTANCE = 4096.0;

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) { super(ctx); }

    @Unique
    private boolean playerex$shouldRenderLevel(T entity) {
        if (this.dispatcher.camera == null) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        double dist = this.dispatcher.getSquaredDistanceToCamera(entity);
        double f = entity.isSneaky() ? 32.0 : 64.0;
        if (dist >= (f * f)) return false;

        boolean isEntityVisible = !entity.isInvisibleTo(player);

        if (player != null && entity != player) {
            AbstractTeam team1 = entity.getScoreboardTeam();
            AbstractTeam team2 = player.getScoreboardTeam();

            if (team1 != null) {
                AbstractTeam.VisibilityRule rule = team1.getNameTagVisibilityRule();
                return switch (rule) {
                    case ALWAYS -> isEntityVisible;
                    case NEVER -> false;
                    case HIDE_FOR_OTHER_TEAMS -> team2 == null ? isEntityVisible
                            : team1.isEqual(team2)
                            && (team1.shouldShowFriendlyInvisibles() || isEntityVisible);
                    case HIDE_FOR_OWN_TEAM -> team2 == null ? isEntityVisible : !team1.isEqual(team2) && isEntityVisible;
                };
            }
        }

        return MinecraftClient.isHudEnabled() && entity != client.getCameraEntity() && isEntityVisible && !entity.hasPassengers();
    }

    @Unique
    private void playerex$renderLevel(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        double dist = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (dist > PLAYEREX$MAX_LEVEL_DISTANCE) return;

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = this.getTextRenderer();

        boolean isNotSneaky = !entity.isSneaky();
        double height = entity.getHeight() + PlayerEX.CONFIG.getLevelNameplateHeight();

        TextRenderer.TextLayerType textLayer = isNotSneaky ? TextRenderer.TextLayerType.NORMAL : TextRenderer.TextLayerType.SEE_THROUGH;

        {
            matrices.push();
            matrices.translate(0.0, height, 0.0);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
        }
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        float bop = client.options.getTextBackgroundOpacity(0.25F);
        int j = (int) (bop * 255.0F) << 24;
        float h = -textRenderer.getWidth(text) / 2.0F;

        int i = 0;
        textRenderer.draw(text, h, (float) i, 553648127, false, matrix4f, vertexConsumers, textLayer, j, light);
        if (isNotSneaky) {
            textRenderer.draw(text, h, (float) i, -1, false, matrix4f, vertexConsumers, textLayer, 0, light);
        }

        matrices.pop();
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void playerex$onRender(T entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        if (this.playerex$shouldRenderLevel(entity) && PlayerEX.CONFIG.getShowLevelNameplates()) {
            DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, entity).ifPresent((value) -> {
                Text tag = Text.translatable("playerex.ui.text.nameplate", String.valueOf(Math.round(value))).formatted(Formatting.WHITE);
                this.playerex$renderLevel(entity, tag, matrixStack, vertexConsumerProvider, i);
            });
        }
    }
}
