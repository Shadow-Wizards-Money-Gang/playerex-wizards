package com.bibireden.playerex.mixin;

import com.bibireden.playerex.util.PlayerEXUtil;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
abstract class ItemRendererMixin {
    @ModifyVariable(
            method = "render",
            at = @At(value = "HEAD"), index = 6, argsOnly = true
    )
    public int renderBlackWhenBroken(int value, ItemStack stack, ItemDisplayContext ctx) {
        if (PlayerEXUtil.isBroken(stack) && ctx == ItemDisplayContext.GUI) {
            return 15;
        }
        return value;
    }
}
