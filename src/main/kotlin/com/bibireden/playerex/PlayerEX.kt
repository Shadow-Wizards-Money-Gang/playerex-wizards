package com.bibireden.playerex

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.factory.DefaultAttributeFactory
import com.bibireden.data_attributes.data.EntityTypeData
import com.bibireden.playerex.api.PlayerEXAPI
import com.bibireden.playerex.api.attribute.DefaultAttributeImpl
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.api.attribute.TradeSkillAttributes
import com.bibireden.playerex.api.event.LivingEntityEvents
import com.bibireden.playerex.api.event.PlayerEXSoundEvents
import com.bibireden.playerex.api.event.PlayerEntityEvents
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.config.PlayerEXConfig
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.factory.*
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import eu.pb4.placeholders.api.Placeholders
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object PlayerEX : ModInitializer {
	const val MOD_ID: String = "playerex"

	@JvmField
	val LOGGER = LoggerFactory.getLogger(MOD_ID)

	@JvmField
	val CONFIG = PlayerEXConfig.createAndLoad()

	fun id(path: String) = Identifier.of(MOD_ID, path)!!

	override fun onInitialize() {
		NetworkingChannels.MODIFY.registerServerbound(NetworkingPackets.Update::class.java) { packet, ctx ->
			val player = ctx.player
			val server = ctx.player.server

			val component = PlayerEXComponents.PLAYER_DATA.get(player)

			if (packet.type.applyIfValid(server, player, component)) {
				for ((id, value) in packet.refs) {
					Registries.ATTRIBUTE[id]?.let { attr ->
						DataAttributesAPI.getValue(attr, player).ifPresent { component.add(attr, it) }
					}
				}
			}
		}

		CommandRegistrationCallback.EVENT.register(PlayerEXCommands::register)

		ServerLoginConnectionEvents.QUERY_START.register(NetworkFactory::onLoginQueryStart)
		ServerPlayerEvents.COPY_FROM.register(EventFactory::reset)

		LivingEntityEvents.ON_HEAL.register(EventFactory::healed)
		LivingEntityEvents.ON_TICK.register(EventFactory::healthRegeneration)
		LivingEntityEvents.ON_DAMAGE.register(EventFactory::onDamage)
		LivingEntityEvents.SHOULD_DAMAGE.register(EventFactory::shouldDamage)

		PlayerEntityEvents.ON_CRITICAL.register(EventFactory::onCritAttack)
		PlayerEntityEvents.SHOULD_CRITICAL.register(EventFactory::attackIsCrit)

		DamageFactory.forEach(PlayerEXAPI::registerDamageModification)
		RefundFactory.forEach(PlayerEXAPI::registerRefundCondition)

		PlaceholderFactory.STORE.forEach(Placeholders::register)

		Registry.register(Registries.SOUND_EVENT, PlayerEXSoundEvents.LEVEL_UP_SOUND.id, PlayerEXSoundEvents.LEVEL_UP_SOUND)
		Registry.register(Registries.SOUND_EVENT, PlayerEXSoundEvents.SPEND_SOUND.id, PlayerEXSoundEvents.SPEND_SOUND)

		DefaultAttributeFactory.registerEntityTypes(DefaultAttributeImpl.ENTITY_TYPES)
		DefaultAttributeFactory.registerFunctions(DefaultAttributeImpl.FUNCTIONS)
	}
}