package com.edelweiss.playerex

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.world.WorldEvents
import org.slf4j.LoggerFactory

object PlayerEXDirectorsCut : ModInitializer {
	const val MODID: String = "playerex-directors-cut"

    private val logger = LoggerFactory.getLogger(MODID)

	override fun onInitialize() {

	}
}