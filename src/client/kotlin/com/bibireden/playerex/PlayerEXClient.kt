package com.bibireden.playerex

import com.bibireden.data_attributes.api.event.EntityAttributeModifiedEvents
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.api.event.PlayerEXSoundEvents
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import com.bibireden.playerex.networking.registerClientbound
import com.bibireden.playerex.networking.types.NotificationType
import com.bibireden.playerex.ui.PlayerEXScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundCategory
import org.lwjgl.glfw.GLFW

object PlayerEXClient : ClientModInitializer {
	val MAIN_UI_SCREEN_ID = PlayerEX.id("main_ui_model")

	private val KEYBINDING_MAIN_SCREEN: KeyBinding = KeyBindingHelper.registerKeyBinding(KeyBinding("${PlayerEX.MOD_ID}.key.main_screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_MINUS, "key.categories.${PlayerEX.MOD_ID}"))

	override fun onInitializeClient() {
		NetworkingChannels.NOTIFICATIONS.registerClientbound(NetworkingPackets.Notify::class) { (type), ctx ->
			when (type) {
				NotificationType.LevelUp -> ctx.player().playSound(PlayerEXSoundEvents.LEVEL_UP_SOUND, SoundCategory.NEUTRAL, PlayerEX.CONFIG.levelUpVolume.toFloat(), 1.5F)
			}
		}

		EntityAttributeModifiedEvents.MODIFIED.register { attribute, entity, _, _, _ ->
			if (entity is PlayerEntity && entity.world.isClient) {
				val screen = MinecraftClient.getInstance().currentScreen
				if (screen is PlayerEXScreen) {
					if (attribute == PlayerEXAttributes.LEVEL) {
						screen.onLevelUpdated()
					}
				}
			}
		}

		ClientTickEvents.END_CLIENT_TICK.register { client ->
			if (PlayerEX.CONFIG.disableAttributesGui) return@register
			while (KEYBINDING_MAIN_SCREEN.wasPressed()) {
				if (client.currentScreen == null) client.setScreen(PlayerEXScreen())
			}
		}
	}
}