package com.bibireden.playerex

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object PlayerEX : ModInitializer {
	const val MOD_ID: String = "playerex"

	val LOGGER = LoggerFactory.getLogger(MOD_ID)

	fun id(path: String) = Identifier.of(MOD_ID, path)!!

	override fun onInitialize() {}
}