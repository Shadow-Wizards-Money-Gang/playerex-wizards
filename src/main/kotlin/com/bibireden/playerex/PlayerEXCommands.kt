package com.bibireden.playerex

import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.ext.id
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier

// todo: overlords will complete this
object PlayerEXCommands {
    val primaries: Set<Identifier> = setOf(
        PlayerEXAttributes.CONSTITUTION.id,
        PlayerEXAttributes.STRENGTH.id,
        PlayerEXAttributes.DEXTERITY.id,
        PlayerEXAttributes.INTELLIGENCE.id,
        PlayerEXAttributes.LUCKINESS.id
    )
    val suggestionProvider = SuggestionProvider<ServerCommandSource> { ctx, builder -> CommandSource.suggestIdentifiers(primaries, builder) }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, registryAccess: CommandRegistryAccess, environment: RegistrationEnvironment) {
        TODO()
    }
}