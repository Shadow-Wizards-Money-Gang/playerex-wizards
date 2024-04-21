package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class FunctionsJSON(private val values: MutableMap<String, MutableMap<String, AttributeFunctionJSON>>) {
    fun merge(functionsIn: MutableMap<String, MutableMap<String, AttributeFunctionJSON>>) {
        this.values.keys.forEach { key ->
            val functions = functionsIn.getOrDefault(key, mutableMapOf())
            this.values[key]?.forEach(functions::put)
            functionsIn[key] = functions
        }
    }
}