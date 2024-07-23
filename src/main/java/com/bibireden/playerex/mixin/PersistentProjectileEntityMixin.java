package com.bibireden.playerex.mixin;

import com.bibireden.data_attributes.api.DataAttributesAPI;
import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {
    // Constructor for the mixin class
    private PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;isCritical()Z"))
    private void playerex_onEntityHit(EntityHitResult entityHitResult, CallbackInfo info) {
        PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)(Object)this;
        Entity owner = persistentProjectileEntity.getOwner();

        if(owner instanceof LivingEntity) {

            Optional<Double> rangedCritChanceOptional = DataAttributesAPI.getValue(PlayerEXAttributes.RANGED_CRITICAL_CHANCE, (LivingEntity)owner);

            if (rangedCritChanceOptional.isPresent())
            {
                persistentProjectileEntity.setCritical(false);
            }
        }
    }

    @SuppressWarnings("UnreachableCode")
    @ModifyArg(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private float playerex_onEntityHit(float i) {
        PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)(Object) this;
        Entity owner = persistentProjectileEntity.getOwner();
        double damage = i;

        if(owner instanceof LivingEntity livingEntity) {
            Optional<Double> damageOptional = DataAttributesAPI.getValue(PlayerEXAttributes.RANGED_DAMAGE, livingEntity);

            damage = damageOptional.isPresent() ? damageOptional.get() + i : i;

            final double amount = damage;

            Optional<Double> rangedCritOptional = DataAttributesAPI.getValue(PlayerEXAttributes.RANGED_CRITICAL_CHANCE, livingEntity);

            if (rangedCritOptional.isPresent())
            {
                boolean cache = livingEntity.getRandom().nextFloat() < rangedCritOptional.get();
                persistentProjectileEntity.setCritical(cache);

                if (cache)
                {
                    Optional<Double> rangedCritDamageOptional = DataAttributesAPI.getValue(PlayerEXAttributes.RANGED_CRITICAL_DAMAGE, livingEntity);
                    if (rangedCritOptional.isPresent())
                    {
                        damage = amount * (1.0 + (10.0 * rangedCritDamageOptional.get()));
                    }
                }
            }
        }

        return (float) damage;
    }
}
