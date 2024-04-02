package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
abstract class DataMerger<K, V, V2>(private val map: MutableMap<K, MutableMap<V, V2>>) {
    infix fun merge(rhs: MutableMap<K, MutableMap<V, V2>>) {
        map.keys.forEach { key ->
            val entries = rhs.getOrDefault(key, mutableMapOf())
            map[key]?.forEach(entries::put)
            rhs[key] = entries
        }
    }
}