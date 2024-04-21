package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class EntityTypesJSON(val values: MutableMap<String, MutableMap<String, Double>>)  {
    fun merge(typesIn: MutableMap<String, MutableMap<String, Double>>) {
        this.values.keys.forEach { key ->
            val types = typesIn.getOrDefault(key, mutableMapOf())
            this.values[key]?.forEach(types::put)
            typesIn[key] = types
        }
    }
}