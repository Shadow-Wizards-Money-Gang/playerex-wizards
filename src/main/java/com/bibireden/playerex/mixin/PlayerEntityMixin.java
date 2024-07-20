package com.bibireden.playerex.mixin;

import com.bibireden.playerex.api.event.PlayerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @ModifyVariable(method = "attack", at = @At("STORE"), name = "bl3", ordinal = 2)
    private boolean playerex_attack(boolean bl3, Entity target) {
        return PlayerEntityEvents.SHOULD_CRITICAL.invoker().shouldCritical((PlayerEntity)(Object) this, target, bl3);
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 2), name = "f", ordinal = 0)
    private float playerex_attack(float f, Entity target) {
        return PlayerEntityEvents.ON_CRITICAL.invoker().onCriticalDamage((PlayerEntity) (Object) this, target, f);
    }
}
