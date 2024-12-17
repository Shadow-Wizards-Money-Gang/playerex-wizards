package com.bibireden.playerex.mixin;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.ui.PlayerEXScreen;
import com.mojang.authlib.GameProfile;
import de.dafuqs.additionalentityattributes.AdditionalEntityAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerMixin extends LivingEntity {
    @Shadow @Final protected Minecraft minecraft;

    protected LocalPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "setExperienceValues", at = @At("TAIL"))
    private void playerex$setExperienceValues(CallbackInfo ci) {
        if (minecraft.screen instanceof PlayerEXScreen screen) screen.onExperienceUpdated();
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (minecraft == null) return;
        if (minecraft.screen instanceof PlayerEXScreen screen) {
            screen.onAttributeUpdated(Attributes.MAX_HEALTH, getMaxHealth());
        }
    }

    @Override
    public void setAirSupply(int air) {
        super.setAirSupply(air);
        if (minecraft == null) return;
        if (minecraft.screen instanceof PlayerEXScreen screen) {
            screen.onAttributeUpdated(AdditionalEntityAttributes.LUNG_CAPACITY, DataAttributesAPI.getValue(AdditionalEntityAttributes.LUNG_CAPACITY, this).orElse(0.0));
        }
    }
}
