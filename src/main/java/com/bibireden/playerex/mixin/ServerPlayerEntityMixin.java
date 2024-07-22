package com.bibireden.playerex.mixin;

import com.bibireden.playerex.components.PlayerEXComponents;
import com.bibireden.playerex.components.player.PlayerDataComponent;
import com.bibireden.playerex.factory.NetworkFactory;
import com.bibireden.playerex.util.PlayerEXUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ALL") // todo: remove when intellij updates and fixes cast
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method = "addExperienceLevels", at = @At("TAIL"))
    private void addExperienceLevels(int levels, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerDataComponent component = (PlayerDataComponent) PlayerEXComponents.PLAYER_DATA.get(player);

        int current = player.experienceLevel;
        int required = PlayerEXUtil.getRequiredXpForNextLevel(player);

        if (current >= required) {
            if (!component.isLevelUpNotified()) {
                component.setLevelUpNotified(true);
                NetworkFactory.sendLevelUpNotification(player);
            }
        }
        else {
            component.setLevelUpNotified(false);
        }
    }
}
