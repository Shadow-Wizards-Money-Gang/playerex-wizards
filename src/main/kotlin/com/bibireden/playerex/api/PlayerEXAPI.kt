package com.bibireden.playerex.api

import com.bibireden.opc.api.OfflinePlayerCacheAPI
import com.bibireden.playerex.keys.LevelKey

object PlayerEXAPI {
    @JvmField
    val LEVEL_KEY = OfflinePlayerCacheAPI.register(LevelKey())
}