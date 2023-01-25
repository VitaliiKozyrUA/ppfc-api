package org.ppfc.api.common.cache

import java.util.concurrent.ConcurrentHashMap

class ExpiringCache<K, V>(private val expiryTime: Long) : Cache<K, V> {
    private val hashMap = ConcurrentHashMap<K, ExpiringCacheValue<V>>()

    override suspend fun getValue(key: K, block: suspend () -> V?): V? {
        val cachedValue = hashMap[key]

        return if (cachedValue == null || System.currentTimeMillis() - cachedValue.time > expiryTime) {
            val newValue = block() ?: return null
            hashMap[key] = ExpiringCacheValue(
                value = newValue,
                time = System.currentTimeMillis()
            )
            newValue
        } else {
            cachedValue.value
        }
    }

    override fun clear() {
        hashMap.clear()
    }
}

private data class ExpiringCacheValue<T>(
    val value: T,
    val time: Long
)