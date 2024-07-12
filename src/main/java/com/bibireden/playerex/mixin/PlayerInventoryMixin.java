package com.bibireden.playerex.mixin;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;

    @ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At("RETURN"))
    private float playerex$getBlockBreakingSpeed(float original) {
        Optional<Double> maybeBreakingSpeed = DataAttributesAPI.INSTANCE.getValue(PlayerEXAttributes.BREAKING_SPEED, this.player);
        return maybeBreakingSpeed.map(v -> original + v.floatValue() - 1.0F).orElse(original);
    }
}
