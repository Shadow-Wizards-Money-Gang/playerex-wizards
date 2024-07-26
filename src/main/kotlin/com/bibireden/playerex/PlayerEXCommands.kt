package com.bibireden.playerex

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.EntityAttributeSupplier
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.api.attribute.TradeSkillAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.ext.data
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
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

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
                    .then(CommandManager.argument("retain", IntegerArgumentType.integer(0, 100)).executes { executeResetCommand(it, IntegerArgumentType.getInteger(it, "retain")) })
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
                    )
                )
            ).then(CommandManager.literal("refund")
                .then(CommandManager.literal("get").then(playerArgument.executes(::executeRefundGetCommand)))
                .then(
                    CommandManager.literal("add").then(
                        playerArgument.executes(::executeRefundAddCommand).then(
                            amountArgument.executes { executeRefundAddCommand(it, IntegerArgumentType.getInteger(it, "amount")) }
                        )
                    )
                )
                .then(
                    CommandManager.literal("skill").then(
                        identifierArgument.suggests(MODIFIABLE_ATTRIBUTES_SUGGESTIONS)
                            .then(playerArgument.then(
                                amountArgument.executes { executeRefundAttributeCommand(it, IntegerArgumentType.getInteger(it, "amount")) }
                            )
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

    private fun executeRefundGetCommand(ctx: Context): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")
        ctx.source.sendFeedback({ Text.translatable("playerex.command.refund.get", player.name, player.data.refundablePoints) }, false)
        return 1
    }

    private fun executeRefundAttributeCommand(ctx: Context, amount: Int = 1): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")

        val supplier = EntityAttributeSupplier(IdentifierArgumentType.getIdentifier(ctx, "id"))

        return DataAttributesAPI.getValue(supplier, player).map {
            val attribute = supplier.get()!!
            val computed = MathHelper.clamp(amount, 0, it.toInt())

            if (player.data.refund(attribute, computed)) {
                ctx.source.sendFeedback({ Text.translatable("playerex.command.refunded", amount, Text.translatable(attribute.translationKey), player.name) }, false)
                ctx.source.sendFeedback(updatedValueText(attribute, it - amount), false)
                1
            }
            else {
                -1
            }
        }.orElse(-1)
    }

    private fun executeRefundAddCommand(ctx: Context, amount: Int = 1): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")

        player.data.addRefundablePoints(amount)

        ctx.source.sendFeedback({ Text.translatable("playerex.command.refund.add", amount, player.name) }, false)

        return 1
    }

    private fun executeSkillUpCommand(ctx: Context, amount: Int = 1): Int {
        val player = EntityArgumentType.getPlayer(ctx, "player")
        val supplier = EntityAttributeSupplier(IdentifierArgumentType.getIdentifier(ctx, "id"))

        return DataAttributesAPI.getValue(supplier, player).map {
            val attribute = supplier.get()!!
            val computed = MathHelper.clamp(amount, 0, (attribute as IEntityAttribute).`data_attributes$max`().toInt() - it.toInt())

            if (player.data.skillUp(attribute, computed, true)) {
                ctx.source.sendFeedback({ Text.translatable("playerex.command.skill_up", computed, Text.translatable(attribute.translationKey), player.name) }, false)
                ctx.source.sendFeedback(updatedValueText(attribute, it + computed), false)
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

        return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, player).map { value ->
            val attribute = PlayerEXAttributes.LEVEL
            val computed = MathHelper.clamp(amount, 0, (attribute as IEntityAttribute).`data_attributes$max`().toInt() - value.toInt())

            if (!player.data.levelUp(computed, true)) {
                // todo: err message, for now just -1
                return@map -1
            }

            ctx.source.sendFeedback({ Text.translatable("playerex.command.level_up", computed, player.name) }, false)
            ctx.source.sendFeedback(updatedValueText(attribute, value + computed), false)

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