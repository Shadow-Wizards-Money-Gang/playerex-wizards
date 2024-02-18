package com.edelweiss.playerex.mixin;

import com.edelweiss.playerex.cache.PlayerEXCacheInternal;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin {

    @Final
    @Shadow
    private MinecraftServer server;

    @Final
    @Shadow
    private List<ServerPlayerEntity> players;

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void playerex_onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerEXCacheInternal.Companion.ifCachePresent(this.server, null, playerCache -> {
            playerCache.uncache(player);
            return null;
        });
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void playerex_remove(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerEXCacheInternal.Companion.ifCachePresent(this.server, null, playerCache -> {
            playerCache.cache(player);
            return null;
        });
    }

    @Inject(method = "disconnectAllPlayers", at = @At("HEAD"))
    private void playerex_disconnectAllPlayers(CallbackInfo ci) {
        PlayerEXCacheInternal.Companion.ifCachePresent(this.server, null, playerCache -> {
            for (ServerPlayerEntity player : this.players) { playerCache.cache(player); }
            return null;
        });
    }
}