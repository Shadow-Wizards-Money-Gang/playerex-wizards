package com.bibireden.playerex

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.api.attribute.TradeSkillAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.ext.id
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
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

    val tradeSkills: Set<Identifier> = setOf(
        TradeSkillAttributes.MINING.id,
        TradeSkillAttributes.ALCHEMY.id,
        TradeSkillAttributes.FISHING.id,
        TradeSkillAttributes.FARMING.id,
        TradeSkillAttributes.LOGGING.id,
        TradeSkillAttributes.ENCHANTING.id,
        TradeSkillAttributes.ENCHANTING.id
    )

    val primarySuggestionProvider = SuggestionProvider<ServerCommandSource> { ctx, builder -> CommandSource.suggestIdentifiers(primaries, builder) }

    fun executeLevelUpCommand(ctx: CommandContext<ServerCommandSource>): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")
        val component = PlayerEXComponents.PLAYER_DATA.get(player)

        return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, player).map { value ->
            val attribute = PlayerEXAttributes.LEVEL

            if ((attribute as IEntityAttribute).`data_attributes$max`() - value < 1) {
                ctx.source.sendFeedback(maxErrorMessage(player, attribute),false)
                return@map -1
            }

            component.add(PlayerEXAttributes.LEVEL, 1.0)
            component.addSkillPoints(PlayerEX.CONFIG.skillPointsPerLevelUp)
            
            ctx.source.sendFeedback({ Text.translatable("playerex.command.levelup_alt", player.getName()) }, false)

            return@map 1
        }.orElse(-1)
    }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, registryAccess: CommandRegistryAccess, environment: RegistrationEnvironment) {
        dispatcher.register(CommandManager.literal("playerex")
            .requires { it.hasPermissionLevel(2) }
            .then(
                CommandManager.literal("level")
                    .then(
                        // todo: add a secondary argument named "amount". we might make it generic for multiple uses, but it should be tied here.
                        CommandManager.argument("player", EntityArgumentType.player()).executes(::executeLevelUpCommand)
                    )
            )
        )
    }


    fun maxErrorMessage(player: PlayerEntity, attribute: EntityAttribute): () -> MutableText = {
        Text.translatable("playerex.command.attribute_max_error", Text.translatable(attribute.translationKey), player.name)
            .formatted(Formatting.RED)
    }
}