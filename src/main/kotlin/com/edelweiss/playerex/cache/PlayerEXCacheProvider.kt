package com.edelweiss.playerex.cache

import net.minecraft.server.MinecraftServer
import java.util.*

class PlayerEXCacheProvider(
    private val server: MinecraftServer,
    private val internal: PlayerEXCacheInternal? = PlayerEXCacheInternal.ifCachePresent(server, null) { cache -> cache }
) : PlayerEXCache {
    /** Checks if the cache is "empty", as in being `null`. */
    fun isEmpty() = this.internal == null

    override fun playerIDs() = this.internal!!.playerIDs(this.server)

    override fun playerNames() = this.internal!!.playerNames(this.server)

    override fun isPlayerCached(uuid: UUID) = this.internal!!.isPlayerCached(uuid)

    override fun isPlayerCached(playerName: String) = this.internal!!.isPlayerCached(playerName)
}