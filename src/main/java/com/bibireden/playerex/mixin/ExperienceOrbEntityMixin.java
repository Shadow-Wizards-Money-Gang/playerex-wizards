package com.bibireden.playerex.mixin;

import com.bibireden.playerex.PlayerEX;
import com.bibireden.playerex.components.PlayerEXComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {
    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDI)V", at = @At("TAIL"))
    private void playerex$init(World world, double x, double y, double z, int amount, CallbackInfo ci) {
        BlockPos pos = BlockPos.ofFloored(x, y, z);
        Chunk chunk = world.getChunk(pos);
        PlayerEXComponents.EXPERIENCE_DATA.maybeGet(chunk).ifPresent(data -> {
            if (data.updateExperienceNegationFactor(amount)) {
                ((ExperienceOrbEntity) (Object) this).remove(Entity.RemovalReason.DISCARDED);
            }
        });
    }
}
