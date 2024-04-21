package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class PropertiesJSON(private val properties: MutableMap<String, MutableMap<String, String>>) : DataMerger<String>(properties)
