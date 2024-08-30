package com.bibireden.playerex.mixin;

import com.bibireden.playerex.PlayerEX;
import com.bibireden.playerex.api.event.PlayerEntityEvents;
import com.bibireden.playerex.util.PlayerEXUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    public void preventAttack(Entity target, CallbackInfo ci) {
        if (!PlayerEX.CONFIG.getItemBreakingEnabled()) return;

        Player player = (Player)(Object)this;
        // TODO: BetterCombat compat
        if (PlayerEXUtil.isBroken(player.getMainHandItem())) {
            ci.cancel();
        }
    }

    @Inject(method = "interactOn(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    public void preventInteract(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!PlayerEX.CONFIG.getItemBreakingEnabled()) return;

        Player player = (Player)(Object)this;
        if (PlayerEXUtil.isBroken(player.getItemInHand(hand))) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Inject(method = "blockActionRestricted(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/GameType;)Z", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    public void preventBreakBlock(Level level, BlockPos pos, GameType gameMode, CallbackInfoReturnable<Boolean> cir) {
        if (!PlayerEX.CONFIG.getItemBreakingEnabled()) return;

        Player player = (Player)(Object)this;
        if (PlayerEXUtil.isBroken(player.getMainHandItem())) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "attack", at = @At("STORE"), name = "bl3", ordinal = 2)
    private boolean playerex$attack(boolean bl3, Entity target) {
        return PlayerEntityEvents.SHOULD_CRITICAL.invoker().shouldCritical((Player)(Object) this, target, bl3);
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 2), name = "f", ordinal = 0)
    private float playerex$attack(float f, Entity target) {
        return PlayerEntityEvents.ON_CRITICAL.invoker().onCriticalDamage((Player) (Object) this, target, f);
    }
}
