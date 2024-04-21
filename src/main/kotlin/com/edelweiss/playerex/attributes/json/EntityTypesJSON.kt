package com.edelweiss.playerex.attributes.json

import kotlinx.serialization.Serializable

@Serializable
data class EntityTypesJSON(val entityTypes: MutableMap<String, MutableMap<String, Double>>) : DataMerger<Double>(entityTypes);
