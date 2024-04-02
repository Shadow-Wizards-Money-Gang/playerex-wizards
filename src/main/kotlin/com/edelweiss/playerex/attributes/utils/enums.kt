package com.edelweiss.skillattributes.utils

inline infix fun <reified E : Enum<E>, V> ((E) -> V).from(id: V): E? {
    return enumValues<E>().firstOrNull { this(it) == id }
}