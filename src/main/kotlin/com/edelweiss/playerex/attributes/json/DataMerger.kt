package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
open class DataMerger<T>(private val values: Map<String, Map<String, T>>) {
    fun merge(functionsIn: MutableMap<String, MutableMap<String, T>>) {
        this.values.keys.forEach { key ->
            val functions = functionsIn.getOrDefault(key, mutableMapOf())
            this.values[key]?.forEach(functions::put)
            functionsIn[key] = functions
        }
    }
}