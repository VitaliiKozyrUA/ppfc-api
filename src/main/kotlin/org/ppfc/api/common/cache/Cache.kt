package org.ppfc.api.common.cache

interface Cache<K, V> {
    suspend fun getValue(key: K, block: suspend () -> V?): V?

    fun clear()
}