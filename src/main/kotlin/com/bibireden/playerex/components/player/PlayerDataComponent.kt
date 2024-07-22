package com.bibireden.playerex.components.player

import com.bibireden.data_attributes.api.DataAttributesAPI
import com.bibireden.data_attributes.api.attribute.IEntityAttribute
import com.bibireden.data_attributes.api.attribute.IEntityAttributeInstance
import com.bibireden.data_attributes.endec.Endecs
import com.bibireden.data_attributes.endec.nbt.NbtDeserializer
import com.bibireden.data_attributes.endec.nbt.NbtSerializer
import com.bibireden.playerex.PlayerEX.CONFIG
import com.bibireden.playerex.api.PlayerEXModifiers
import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.registry.RefundConditionRegistry
import com.bibireden.playerex.util.PlayerEXUtil
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.sync.ComponentPacketWriter
import io.wispforest.endec.Endec
import io.wispforest.endec.impl.StructEndecBuilder
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeInstance
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import kotlin.math.exp
import kotlin.math.round

/**
 * Implementation of player-data that is meant to be synchronized. Contains [EntityAttribute] data and provided modifiers,
 * skill points and refundable points.
 */
class PlayerDataComponent(
    private val player: PlayerEntity,
    private var _refundablePoints: Int = 0,
    private var _skillPoints: Int = 0,
    private var _modifiers: MutableMap<Identifier, Double> = mutableMapOf(),
    var isLevelUpNotified: Boolean = false,
) : IPlayerDataComponent, AutoSyncedComponent {
    data class Packet(val modifiers: Map<Identifier, Double>, val refundablePoints: Int, val skillPoints: Int, val isLevelUpNotified: Boolean)

    private fun updateFromPacket(tag: NbtCompound) {
        ENDEC.decodeFully(NbtDeserializer::of, tag.get("DART")).also {
            this._modifiers = it.modifiers.toMutableMap()
            this._refundablePoints = it.refundablePoints
            this._skillPoints = it.skillPoints
            this.isLevelUpNotified = it.isLevelUpNotified
        }
    }

    private fun toPacketNbt(): NbtCompound = NbtCompound().also(::writeToNbt)

    companion object {
        val ENDEC = StructEndecBuilder.of(
            Endec.map(Endecs.IDENTIFIER, Endec.DOUBLE).fieldOf("modifiers") { it.modifiers },
            Endec.INT.fieldOf("refundablePoints") { it.refundablePoints },
            Endec.INT.fieldOf("skillPoints") { it.skillPoints },
            Endec.BOOLEAN.fieldOf("isLevelUpNotified") { it.isLevelUpNotified },
            ::Packet
        )
    }

    /**
     * Attempts to fetch an [EntityAttributeInstance] based on the provided [Identifier].
     *
     * @return a [Pair] of the instance, and whether that instance contains an existing modifier that has the UUID [PlayerEXModifiers.UUID].
     */
    private fun getInstance(key: Identifier): Pair<EntityAttributeInstance, Boolean>? {
        val attribute = Registries.ATTRIBUTE[key] ?: return null
        val instance = this.player.attributes.getCustomInstance(attribute) ?: return null
        return Pair(instance, instance.getModifier(PlayerEXModifiers.UUID) != null)
    }

    private fun sync(packet: ComponentPacketWriter) {
        this.player.world.takeUnless { it.isClient }?.let { world ->
            PlayerEXComponents.PLAYER_DATA.sync(this.player, packet)
        }
    }

    /**
     * Attempts to set a special PlayerEX-specific modifier to an [EntityAttributeInstance] based on the [Identifier] key provided.
     */
    private fun trySet(key: Identifier, value: Double): Boolean {
        val (instance, isPlayerEXModifierPresent) = this.getInstance(key) ?: return false
        if (isPlayerEXModifierPresent) {
            (instance as IEntityAttributeInstance).updateModifier(PlayerEXModifiers.UUID, value)
        }
        else {
            instance.addPersistentModifier(EntityAttributeModifier(PlayerEXModifiers.UUID, "PlayerEX Attribute", value, EntityAttributeModifier.Operation.ADDITION))
        }
        this._modifiers[key] = value
        return true
    }

    // todo: this function angers me!!!! 😡 remove should be suitable enough instead of a try.
    /**
     * Attempts to remove a special PlayerEX modifier if present on the [EntityAttributeInstance] linked by the provided [Identifier] key.
     *
     * @return [Boolean] Whether there was an existing [EntityAttributeInstance], whether an [EntityAttributeModifier] was removed or not.
     * */
    private fun tryRemove(key: Identifier): Boolean {
        return this.getInstance(key)?.let { (instance, isModifierPresent) ->
            if (isModifierPresent) {
                instance.removeModifier(PlayerEXModifiers.UUID)
            }
        } != null
    }

    override fun get(attribute: EntityAttribute): Double {
        return this._modifiers.getOrDefault(attribute.id, 0.0)
    }

    override fun set(attribute: EntityAttribute, value: Double) {
        val identifier = attribute.id
        val attributeValue = attribute.clamp(value)

        if (!this.trySet(identifier, attributeValue)) return

        this.sync { buf, player -> buf.writeNbt(toPacketNbt())}
    }

    override fun add(attribute: EntityAttribute, value: Double) {
        this.set(attribute, value + this.get(attribute))
    }

    override fun remove(attribute: EntityAttribute) {
        val identifier = attribute.id
        if (!this.tryRemove(identifier).also { if (it) this._modifiers.remove(identifier) }) return
        this.sync { buf, player -> buf.writeNbt(toPacketNbt())}
    }

    override fun reset(percent: Int) {
        if (percent >= 100) return

        val partition = if (percent == 0) 0.0 else percent / 100.0

        for ((id, value) in this._modifiers) {
            if (partition == 0.0) {
                this.tryRemove(id)
                this._modifiers.remove(id)
            }
            else {
                val retained = value * partition
                if (!this.trySet(id, retained)) continue
            }
        }

        this._refundablePoints = round(this._refundablePoints * partition).toInt()
        this._skillPoints = round(this._skillPoints * partition).toInt()

        this.sync { buf, _ -> buf.writeNbt(toPacketNbt())}
    }

    override fun addSkillPoints(points: Int) {
        this._skillPoints += points
        this.sync { buf, player -> buf.writeNbt(toPacketNbt())}
    }

    override fun addRefundablePoints(points: Int): Int {
        val previous = this.refundablePoints
        var maxRefundPoints = 0.0

        for (condition in RefundConditionRegistry.get()) {
            maxRefundPoints += condition(this, this.player)
        }

        this._refundablePoints = round(MathHelper.clamp((this.refundablePoints + points).toDouble(), 0.0, maxRefundPoints)).toInt()

        this.sync { buf, player -> buf.writeNbt(toPacketNbt())}

        return this.refundablePoints - previous
    }

    override fun levelUp(amount: Int, override: Boolean): Boolean {
        if (amount <= 0) return false

        return DataAttributesAPI.getValue(PlayerEXAttributes.LEVEL, player).map { level ->
            val expectedLevel = level + amount
            // get the expected level, but do not go beyond the bounds of the maximum!
            if (expectedLevel > PlayerEXAttributes.LEVEL.maxValue) return@map false

            val required = PlayerEXUtil.getRequiredExperienceForLevel(expectedLevel)

            val isEnoughExperience = player.experienceLevel >= required || override
            if (isEnoughExperience) {
                val skillPoints = CONFIG.skillPointsPerLevelUp * amount
                val component = PlayerEXComponents.PLAYER_DATA.get(player)

                player.addExperienceLevels(-required)
                component.addSkillPoints(skillPoints)
                component.set(PlayerEXAttributes.LEVEL, expectedLevel)
            }
            return@map isEnoughExperience
        }.orElse(false)
    }

    override fun skillUp(skill: EntityAttribute, amount: Int, override: Boolean): Boolean {
        if (amount <= 0) return false

        return DataAttributesAPI.getValue(skill, player).map { current ->
            val expected = current + amount
            // too high
            if (expected > (skill as IEntityAttribute).`data_attributes$max`()) return@map false
            if (!override) {
                // not enough skill points
                if (skillPoints < amount) return@map false
                this._skillPoints -= amount
            }
            this.set(skill, expected)
            return@map true
        }.orElse(false)
    }

    override fun refund(skill: EntityAttribute, amount: Int): Boolean {
        if (amount <= 0 || this.refundablePoints < amount) return false
        this.addRefundablePoints(-1)
        this.addSkillPoints(1)
        return true
    }

    override val skillPoints: Int
        get() = this._skillPoints

    override val refundablePoints: Int
        get() = this._refundablePoints

    override fun readFromNbt(tag: NbtCompound) {
        ENDEC.decodeFully(NbtDeserializer::of, tag.get("DART")).also {
            this._modifiers = it.modifiers.toMutableMap()
            this._refundablePoints = it.refundablePoints
            this._skillPoints = it.skillPoints
            this.isLevelUpNotified = it.isLevelUpNotified
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.put("DART", ENDEC.encodeFully(NbtSerializer::of, Packet(this._modifiers, this.refundablePoints, this.skillPoints, this.isLevelUpNotified)))
    }

    override fun shouldSyncWith(player: ServerPlayerEntity): Boolean = player == this.player

    override fun applySyncPacket(buf: PacketByteBuf) {
        updateFromPacket(buf.readNbt() ?: return)
    }
}