package com.edelweiss.playerex.cache

import net.minecraft.server.MinecraftServer
import java.util.*

class PlayerEXCacheProvider(
    private val server: MinecraftServer,
    private val internal: PlayerEXCacheInternal? = PlayerEXCacheInternal.ifCachePresent(server, null) { cache -> cache }
) : PlayerEXCache {
    /** Checks if the cache is "empty", as in being `null`. */
    fun isEmpty(): Boolean = this.internal == null

    override fun playerIDs(): Set<UUID> {
        TODO("Not yet implemented")
    }

    override fun playerNames(): Set<String> {
        TODO("Not yet implemented")
    }

    override fun isPlayerCached(uuid: UUID): Boolean {
        TODO("Not yet implemented")
    }

    override fun isPlayerCached(playerName: String) {
        TODO("Not yet implemented")
    }
}