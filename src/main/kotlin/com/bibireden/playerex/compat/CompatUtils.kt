package com.bibireden.playerex.compat

import net.fabricmc.loader.api.FabricLoader

object CompatUtils {
    fun isModLoaded(id: String): Boolean = FabricLoader.getInstance().isModLoaded(id)
}