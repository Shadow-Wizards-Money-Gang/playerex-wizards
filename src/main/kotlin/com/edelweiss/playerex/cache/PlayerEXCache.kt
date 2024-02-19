package com.edelweiss.playerex.cache

import com.edelweiss.playerex.PlayerEXDirectorsCut
import net.minecraft.server.MinecraftServer
import java.util.UUID

interface PlayerEXCache {
    companion object {
        /** Registers the provided cached value to the server. */
        fun <V>register(key: CachedPlayerValue<V>): CachedPlayerValue<V> {
            PlayerEXDirectorsCut.LOGGER.debug("Cached Value has been registered: <{} :: #id: {}>", key, key.id())
            return PlayerEXCacheInternal.register(key)
        }

        /** Get access to the cache object. Should only be used on the server. */
        fun <T>getCache(server: MinecraftServer, fallback: T, function: (PlayerEXCache) -> T): T {
            val provider = PlayerEXCacheProvider(server)
            if (provider.isEmpty()) return fallback
            return function(provider)
        }
    }

    /** Fetches the last cached value if offline, otherwise will fetch the current value from the player. */
    fun <V>get(uuid: UUID, key: CachedPlayerValue<V>) {}

    /** Fetches the last cached value if offline, otherwise will fetch the current value from the player. */
    fun <V>get(playerName: String, key: CachedPlayerValue<V>) {}

    /** Returns all offline & online player UUIDs. */
    fun playerIDs(): Collection<UUID>

    /** Returns all offline & online player names. */
    fun playerNames(): Collection<String>

    /** Checks if the player with the UUID is in the cache or not. */
    fun isPlayerCached(uuid: UUID): Boolean

    /** Checks if the player with the name is in the cache or not. */
    fun isPlayerCached(playerName: String): Boolean
}