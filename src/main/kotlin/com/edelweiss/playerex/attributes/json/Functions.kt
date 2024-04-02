package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class Functions(val values: MutableMap<String, MutableMap<String, AttributeFunction>>) : DataMerger<String, String, AttributeFunction>(values);