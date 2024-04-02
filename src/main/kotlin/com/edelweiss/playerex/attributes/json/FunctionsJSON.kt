package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class FunctionsJSON(val values: MutableMap<String, MutableMap<String, AttributeFunctionJSON>>) : DataMerger<String, String, AttributeFunctionJSON>(values);