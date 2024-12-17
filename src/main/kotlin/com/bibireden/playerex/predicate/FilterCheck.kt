package com.bibireden.playerex.predicate

import com.bibireden.playerex.api.PlayerEXTags
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.Serializer
import net.minecraft.world.level.storage.loot.functions.LootItemFunction
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import kotlin.random.Random

class FilterCheck : LootItemFunction {
    override fun getType(): LootItemFunctionType? {
        return type()
    }

    override fun apply(
        t: ItemStack?,
        u: LootContext?
    ): ItemStack? {

        val num = when (u?.level?.dimension()) {
            Level.NETHER -> Random.nextInt(36, 71)
            Level.END -> Random.nextInt(71, 105)
            else -> Random.nextInt(1, 36)
        }

        var tag = CompoundTag()
        tag.putInt("Level", num)

        if(t?.`is`(PlayerEXTags.WEAPONS)!! || t.`is`(PlayerEXTags.ARMOR)) {
            t.orCreateTag.merge(tag)
        }

        return t
    }

    class FilterSerializer : Serializer<FilterCheck> {
        override fun serialize(
            json: JsonObject,
            value: FilterCheck,
            serializationContext: JsonSerializationContext
        ) {
        }

        override fun deserialize(
            json: JsonObject,
            serializationContext: JsonDeserializationContext
        ): FilterCheck? {
            return FilterCheck()
        }
    }

    companion object {
        fun type(): LootItemFunctionType {
            return LootItemFunctionType(FilterSerializer())
        }
    }
}