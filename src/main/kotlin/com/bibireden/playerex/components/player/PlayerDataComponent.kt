package com.bibireden.playerex.components.player

import com.bibireden.data_attributes.api.attribute.IEntityAttributeInstance
import com.bibireden.data_attributes.endec.Endecs
import com.bibireden.data_attributes.endec.nbt.NbtDeserializer
import com.bibireden.data_attributes.endec.nbt.NbtSerializer
import com.bibireden.playerex.PlayerEX
import com.bibireden.playerex.api.PlayerEXModifiers
import com.bibireden.playerex.components.PlayerEXComponents
import com.bibireden.playerex.ext.id
import com.bibireden.playerex.registry.RefundConditionRegistry
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.sync.ComponentPacketWriter
import io.wispforest.endec.Endec
import io.wispforest.endec.impl.StructEndecBuilder
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeInstance
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.network.PacketByteBuf
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
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
    var isLevelUpNotified: Boolean = false
) : IPlayerDataComponent, AutoSyncedComponent {
    object Keys {
        const val SET = "set"
        const val REMOVE = "remove"
        const val RESET = "reset"
        const val MODIFIERS = "modifiers"
        const val REFUNDABLE_POINTS = "refundable_points"
        const val SKILL_POINTS = "skill_points"
    }

    data class Packet(val modifiers: Map<Identifier, Double>, val refundablePoints: Int, val skillPoints: Int, val isLevelUpNotified: Boolean)

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

    /**
     * Attempts to remove a special PlayerEX modifier if present on the [EntityAttributeInstance] linked by the provided [Identifier] key.
     *
     * @return [Boolean] Whether there was an existing [EntityAttributeInstance], whether an [EntityAttributeModifier] was removed or not.
     * */
    private fun tryRemove(key: Identifier): Boolean {
        return this.getInstance(key)?.let { (instance, isModifierPresent) ->
            if (isModifierPresent) instance.removeModifier(PlayerEXModifiers.UUID)
        } != null
    }

    private fun readModifiersFromNbt(tag: NbtCompound, fn: (Identifier, Double) -> Double?) {
        val modifiers = tag.getList(Keys.MODIFIERS, NbtElement.COMPOUND_TYPE.toInt())

        for (modifier in modifiers) {
            if (modifier is NbtCompound) {
                fn(Identifier.tryParse(modifier.getString("key"))!!, modifier.getDouble("value"))
            }
        }
    }

    override fun get(attribute: EntityAttribute): Double {
        return this._modifiers.getOrDefault(attribute.id, 0.0)
    }

    override fun set(attribute: EntityAttribute, value: Double) {
        val identifier = attribute.id
        val attributeValue = attribute.clamp(value)

        if (!this.trySet(identifier, value)) return

        this.sync { buf, player ->
            val tag = NbtCompound()
            val entry = NbtCompound()
            entry.putString("key", identifier.toString())
            entry.putDouble("value", value)
            tag.put(Keys.SET, entry)
            buf.writeNbt(tag)
        }
    }

    override fun add(attribute: EntityAttribute, value: Double) {
        this.set(attribute, value + this.get(attribute))
    }

    override fun remove(attribute: EntityAttribute) {
        val identifier = attribute.id
        if (!this.tryRemove(identifier).also { if (it == true) this._modifiers.remove(identifier) }) return
        this.sync { buf, player ->
            val tag = NbtCompound()
            tag.putString(Keys.REMOVE, identifier.toString())
            buf.writeNbt(tag)
        }
    }

    override fun reset(percent: Int) {
        if (percent == 100) return

        val list = NbtList()

        for ((id, value) in this._modifiers) {
            if (percent == 0) {
                if (!this.tryRemove(id)) continue
                val entry = NbtCompound()
                entry.putString("key", id.toString())
                entry.putDouble("value", 0.0)
                entry.putBoolean(Keys.REMOVE, true)
                list.add(entry)
            }
            else {
                val retained = value * 0.1 * percent
                if (!this.trySet(id, retained)) continue

                val entry = NbtCompound()
                entry.putString("key", id.toString())
                entry.putDouble("value", retained)
                entry.putBoolean(Keys.REMOVE, false)
                list.add(entry)
            }
        }

        this._refundablePoints = round(this._refundablePoints * 0.01F * percent).toInt()
        this._skillPoints = round(this._skillPoints * 0.01F * percent).toInt()

        this.sync { buf, player ->
            val tag = NbtCompound()
            tag.put(Keys.RESET, list)
            tag.putInt(Keys.SKILL_POINTS, this.skillPoints())
            tag.putInt(Keys.REFUNDABLE_POINTS, this.refundablePoints())
            buf.writeNbt(tag)
        }
    }

    override fun addSkillPoints(points: Int) {
        this._skillPoints += points
        this.sync { buf, player ->
            val tag = NbtCompound()
            tag.putInt(Keys.SKILL_POINTS, this.skillPoints())
            buf.writeNbt(tag)
        }
    }

    override fun addRefundablePoints(points: Int) {
        val previous = this.refundablePoints()
        var maxRefundPoints = 0.0

        for (condition in RefundConditionRegistry.get()) {
            maxRefundPoints += condition(this, this.player)
        }

        this._refundablePoints = round(MathHelper.clamp((this.refundablePoints() + points).toDouble(), 0.0, maxRefundPoints)).toInt()

        this.sync { buf, player ->
            val tag = NbtCompound()
            tag.putInt(Keys.REFUNDABLE_POINTS, this.refundablePoints())
            buf.writeNbt(tag)
        }
    }

    override fun skillPoints(): Int = this._skillPoints

    override fun refundablePoints(): Int = this._refundablePoints

    override fun readFromNbt(tag: NbtCompound) {
        ENDEC.decode(NbtDeserializer.of(tag)).also {
            this._modifiers = it.modifiers.toMutableMap()
            this._refundablePoints = it.refundablePoints
            this._skillPoints = it.skillPoints
            this.isLevelUpNotified = it.isLevelUpNotified
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        ENDEC.encode(NbtSerializer.of(tag), Packet(this._modifiers, this.refundablePoints(), this.skillPoints(), this.isLevelUpNotified))
    }

    override fun shouldSyncWith(player: ServerPlayerEntity?): Boolean {
        return player == this.player
    }

    override fun applySyncPacket(buf: PacketByteBuf?) {
        val tag = buf?.readNbt() ?: return

        if (tag.contains(Keys.SET)) {
            val entry = tag.getCompound(Keys.SET)
            val identifier = Identifier.tryParse(entry.getString("key"))
            if (identifier == null) {
                PlayerEX.LOGGER.warn("Could not validate identifier @PlayerDataComponent, attempted to parse ${entry.getString("key")}")
                return
            }
            this._modifiers[identifier] = entry.getDouble("value")
        }

        if (tag.contains(Keys.REMOVE)) {
            this._modifiers.remove(Identifier.tryParse(tag.getString(Keys.REMOVE)))
        }

        if (tag.contains(Keys.RESET)) {
            val list = tag.getList(Keys.RESET, NbtElement.COMPOUND_TYPE.toInt())

            list.forEach { entry ->
                if (entry is NbtCompound) {
                    val id = Identifier.tryParse(entry.getString("key"))!!
                    val remove = entry.getBoolean(Keys.REMOVE)

                    if (remove) {
                        this._modifiers.remove(id)
                    }
                    else {
                        val value = entry.getDouble("value")
                        this._modifiers[id] = value
                    }
                }
            }

            this.isLevelUpNotified = true
        }

        if (tag.contains(Keys.MODIFIERS)) this.readModifiersFromNbt(tag, this._modifiers::put)
        if (tag.contains(Keys.REFUNDABLE_POINTS)) this._refundablePoints = tag.getInt(Keys.REFUNDABLE_POINTS)
        if (tag.contains(Keys.SKILL_POINTS)) this._skillPoints = tag.getInt(Keys.SKILL_POINTS)
    }
}