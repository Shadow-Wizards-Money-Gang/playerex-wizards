package com.bibireden.playerex

import com.bibireden.playerex.api.PlayerEXAPI
import com.bibireden.playerex.api.event.LivingEntityEvents
import com.bibireden.playerex.api.event.PlayerEXSoundEvents
import com.bibireden.playerex.api.event.PlayerEntityEvents
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.factory.DamageFactory
import com.bibireden.playerex.factory.EventFactory
import com.bibireden.playerex.factory.NetworkFactory
import com.bibireden.playerex.factory.PlaceholderFactory
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import eu.pb4.placeholders.api.Placeholders
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object PlayerEX : ModInitializer {
	const val MOD_ID: String = "playerex"

	val LOGGER = LoggerFactory.getLogger(MOD_ID)

	fun id(path: String) = Identifier.of(MOD_ID, path)!!

	override fun onInitialize() {
		NetworkingChannels.MODIFY.registerServerbound(NetworkingPackets.Attributes::class.java) { (ty, attributes), access ->
			val playerData = PlayerEXComponents.PLAYER_DATA.get(access.player)
			// todo: think a bit more about how you want to deliver and recieve packets, and handle custom behavior. PacketType in the previous was more involved than it seemed.
		}

		CommandRegistrationCallback.EVENT.register(PlayerEXCommands::register)

		ServerLoginConnectionEvents.QUERY_START.register(NetworkFactory::onLoginQueryStart)
		ServerLifecycleEvents.SERVER_STARTING.register(EventFactory::serverStarting)
		ServerPlayerEvents.COPY_FROM.register(EventFactory::reset)

		LivingEntityEvents.ON_HEAL.register(EventFactory::healed)
		LivingEntityEvents.ON_TICK.register(EventFactory::healthRegeneration)
		LivingEntityEvents.ON_DAMAGE.register(EventFactory::onDamage)
		LivingEntityEvents.SHOULD_DAMAGE.register(EventFactory::shouldDamage)

		PlayerEntityEvents.ON_CRITICAL.register(EventFactory::onCritAttack)
		PlayerEntityEvents.SHOULD_CRITICAL.register(EventFactory::attackIsCrit)

		DamageFactory.forEach(PlayerEXAPI::registerDamageModification)
//		RefundFactory.forEach(PlayerEXAPI::registerRefundCondition)
		PlaceholderFactory.STORE.forEach(Placeholders::register)

		Registry.register(Registries.SOUND_EVENT, PlayerEXSoundEvents.LEVEL_UP_SOUND.id, PlayerEXSoundEvents.LEVEL_UP_SOUND)
		Registry.register(Registries.SOUND_EVENT, PlayerEXSoundEvents.SPEND_SOUND.id, PlayerEXSoundEvents.SPEND_SOUND)
	}
}