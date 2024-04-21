package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class PropertiesJSON(private val values: MutableMap<String, MutableMap<String, String>>) {
    fun merge(propertiesIn: MutableMap<String, MutableMap<String, String>>) {
        this.values.keys.forEach { key ->
            val properties = propertiesIn.getOrDefault(key, mutableMapOf())
            this.values[key]?.forEach(properties::put)
            propertiesIn[key] = properties
        }
    }
}