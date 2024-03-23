package com.edelweiss.playerex

import com.edelweiss.playerex.cache.PlayerEXCacheAPI
import com.edelweiss.playerex.commands.PlayerEXCacheCommands
import com.edelweiss.playerex.commands.PlayerEXCommands
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.MinecraftServer
import org.slf4j.LoggerFactory

object PlayerEXDirectorsCut : ModInitializer {
	const val MODID: String = "playerex"
	val LOGGER = LoggerFactory.getLogger(MODID)!!

	private fun registerCommands() = CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
		val head = PlayerEXCommands.registerHead(dispatcher)
		PlayerEXCacheCommands.register(head)
	}

	override fun onInitialize() {
//		ServerLifecycleEvents.SERVER_STARTING.register(ServerEventListeners::serverStarting)
//		ServerPlayerEvents.COPY_FROM.register(ServerEventListeners::reset)

//		// Register LivingEntity events
//		LivingEntityEvents.ON_HEAL.register(EventFactory::healed);
//		LivingEntityEvents.EVERY_SECOND.register(EventFactory::healthRegeneration);
//		LivingEntityEvents.ON_DAMAGE.register(EventFactory::onDamage);
//		LivingEntityEvents.SHOULD_DAMAGE.register(EventFactory::shouldDamage);
//
//		// Register PlayerEntity events
//		PlayerEntityEvents.ON_CRIT.register(EventFactory::onCritAttack);
//		PlayerEntityEvents.SHOULD_CRIT.register(EventFactory::attackIsCrit);
//
//		// Register attribute modification events
//		EntityAttributeModifiedEvents.CLAMPED.register(EventFactory::clamped);

		this.registerCommands()
	}
}