package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class EntityTypes(val values: MutableMap<String, MutableMap<String, Double>>) : DataMerger<String, String, Double>(values);
