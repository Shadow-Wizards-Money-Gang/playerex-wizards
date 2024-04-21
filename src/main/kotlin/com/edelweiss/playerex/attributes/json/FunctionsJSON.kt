package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class FunctionsJSON(private val functions: MutableMap<String, MutableMap<String, AttributeFunctionJSON>>) : DataMerger<AttributeFunctionJSON>(functions)