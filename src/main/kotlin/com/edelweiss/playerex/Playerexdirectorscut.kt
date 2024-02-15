package com.edelweiss.playerex

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object PlayerEXDirectorsCut : ModInitializer {
    private val logger = LoggerFactory.getLogger("playerex-directors-cut")

	override fun onInitialize() {
		logger.info("Hello Fabric world!")
	}
}