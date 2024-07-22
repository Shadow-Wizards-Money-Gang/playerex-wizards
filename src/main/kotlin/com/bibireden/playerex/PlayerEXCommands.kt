package com.bibireden.playerex

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.api.attribute.TradeSkillAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.networking.NetworkingChannels
import com.bibireden.playerex.networking.NetworkingPackets
import com.bibireden.playerex.networking.types.NotificationType
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.CommandSource
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

private typealias Context = CommandContext<ServerCommandSource>

object PlayerEXCommands {
    private val MODIFIABLE_ATTRIBUTES_SUGGESTIONS = SuggestionProvider<ServerCommandSource> { _, builder ->
        CommandSource.suggestIdentifiers(PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS, builder)
        CommandSource.suggestIdentifiers(TradeSkillAttributes.IDS, builder)
    }

    private val playerArgument: RequiredArgumentBuilder<ServerCommandSource, EntitySelector>
        get() = CommandManager.argument("player", EntityArgumentType.player())

    private val amountArgument: RequiredArgumentBuilder<ServerCommandSource, Int>
        get() = CommandManager.argument("amount", IntegerArgumentType.integer())

    private val identifierArgument: RequiredArgumentBuilder<ServerCommandSource, Identifier>
        get() = CommandManager.argument("id", IdentifierArgumentType.identifier())

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, access: CommandRegistryAccess, environment: RegistrationEnvironment) {
        dispatcher.register(CommandManager.literal("playerex")
            .requires(::isOp)
            .then(CommandManager.literal("level")
                .then(CommandManager.literal("get").then(playerArgument.executes(::executeLevelGetCommand)))
                .then(CommandManager.literal("add").then(
                    playerArgument.executes(::executeLevelUpCommand)
                        .then(amountArgument.executes { executeLevelUpCommand(it, IntegerArgumentType.getInteger(it, "amount")) })
                    )
                )
            )
            .then(CommandManager.literal("reset")
                .then(playerArgument.executes(::executeResetCommand)
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 100)).executes { executeResetCommand(it, IntegerArgumentType.getInteger(it, "amount")) })
                )
                .then(CommandManager.literal("@all").executes(::executeResetAllCommand)
                    .then(amountArgument.executes { executeResetAllCommand(it, IntegerArgumentType.getInteger(it, "amount")) })
                )
            )
            .then(CommandManager.literal("skill")
                .then(identifierArgument.suggests(MODIFIABLE_ATTRIBUTES_SUGGESTIONS)
                    .then(CommandManager.literal("get").then(playerArgument.executes(::executeSkillGetCommand)))
                    .then(CommandManager.literal("add")
                        .then(playerArgument
                            .executes(::executeSkillUpCommand)
                            .then(amountArgument.executes { executeSkillUpCommand(it, IntegerArgumentType.getInteger(it, "amount")) })
                        )
                    ).then(CommandManager.literal("refund")
                        .then(playerArgument
                            .executes(::executeRefundCommand)
                            .then(amountArgument.executes { executeRefundCommand(it, IntegerArgumentType.getInteger(it, "amount")) })
                        )
                    )
                )
            )
        )
    }

    private fun isOp(source: ServerCommandSource) = source.hasPermissionLevel(2)

    private fun executeLevelGetCommand(ctx: Context): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")
        return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, player).map {
            ctx.source.sendFeedback({ Text.translatable("playerex.command.level_get", player.name, it.toInt()).append(Text.of("/${PlayerEXAttributes.LEVEL.maxValue.toInt()}")) }, false)
            1
        }.orElse(-1)
    }
    private fun executeSkillGetCommand(ctx: Context): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")
        val component = PlayerEXComponents.PLAYER_DATA.get(player)
        val supplier = EntityAttributeSupplier(IdentifierArgumentType.getIdentifier(ctx, "id"))

        return DataAttributesAPI.getValue(supplier, player).map { value ->
            ctx.source.sendFeedback({ Text.translatable("playerex.command.skill_get", Text.translatable(supplier.get()!!.translationKey), value.toInt(), player.name) }, false)
            1
        }.orElse(-1)
    }

    private fun executeRefundCommand(ctx: Context, amount: Int = 1): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")
        val component = PlayerEXComponents.PLAYER_DATA.get(player)
        val supplier = EntityAttributeSupplier(IdentifierArgumentType.getIdentifier(ctx, "id"))

        return DataAttributesAPI.getValue(supplier, player).map {
            val attribute = supplier.get()!!
            if (it < amount) {
                ctx.source.sendFeedback({ Text.translatable("playerex.command.refund_error", player.name) }, false)
                -1
            }
            else {
                val result = it - amount
                component.addSkillPoints(amount)
                component.set(attribute, result)
                ctx.source.sendFeedback({ Text.translatable("playerex.command.refunded", amount, Text.translatable(attribute.translationKey), player.name) }, false)
                ctx.source.sendFeedback(updatedValueText(attribute, result), false)
                1
            }
        }.orElse(-1)
    }

    private fun executeSkillUpCommand(ctx: Context, amount: Int = 1): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")
        val component = PlayerEXComponents.PLAYER_DATA.get(player)
        val supplier = EntityAttributeSupplier(IdentifierArgumentType.getIdentifier(ctx, "id"))

        return DataAttributesAPI.getValue(supplier, player).map {
            val attribute = supplier.get()!!
            if (component.skillUp(attribute, amount, true)) {
                ctx.source.sendFeedback({ Text.translatable("playerex.command.skill_up", amount, Text.translatable(attribute.translationKey), player.name) }, false)
                ctx.source.sendFeedback(updatedValueText(attribute, it + amount), false)
                1
            }
            else {
                ctx.source.sendFeedback(maxErrorMessage(player, attribute), false)
                -1
            }
        }.orElse(-1)
    }

    private fun executeLevelUpCommand(ctx: Context, amount: Int = 1): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")
        val component = PlayerEXComponents.PLAYER_DATA.get(player)

        return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, player).map { value ->
            val attribute = PlayerEXAttributes.LEVEL

            // todo: will be useless after new error message... maybe...
            if ((attribute as IEntityAttribute).`data_attributes$max`() <= value) {
                ctx.source.sendFeedback(maxErrorMessage(player, attribute),false)
                return@map -1
            }

            if (!PlayerEXComponents.PLAYER_DATA.get(player).levelUp(amount, true)) {
                // todo: err message, for now just -1
                return@map -1
            }

            ctx.source.sendFeedback({ Text.translatable("playerex.command.level_up", amount, player.name) }, false)
            ctx.source.sendFeedback(updatedValueText(attribute, value + amount), false)

            return@map 1
        }.orElse(-1)
    }

    private fun executeResetCommand(ctx: Context, retained: Int = 0): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")

        PlayerEXComponents.PLAYER_DATA.get(player).reset(retained)

        ctx.source.sendFeedback(
            {
                Text.translatable("playerex.command.reset", player.name).also { if (retained > 0) it.append(" [${retained}%]") }
            },
            false
        )

        return 1
    }

    private fun executeResetAllCommand(ctx: Context, retained: Int = 0): Int {
        PlayerLookup.all(ctx.source.server).forEach { PlayerEXComponents.PLAYER_DATA.get(it).reset(retained) }

        ctx.source.sendFeedback(
            {
                Text.translatable("playerex.command.reset", "(*)")
                    .also { if (retained > 0) it.append(", ${retained}%") }
            },
            false
        )

        return 1
    }

    private fun maxErrorMessage(player: PlayerEntity, attribute: EntityAttribute): () -> MutableText = {
        Text.translatable("playerex.command.max_error", Text.translatable(attribute.translationKey), player.name)
    }

    private fun updatedValueText(attribute: EntityAttribute, value: Double): () -> MutableText = {
        Text.translatable("playerex.command.updated_result", value.toInt()).append(Text.of("/${(attribute as IEntityAttribute).`data_attributes$max`().toInt()}"))
    }
}