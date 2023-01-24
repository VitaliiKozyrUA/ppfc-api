package org.ppfc.api.common.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class ExpiringCache<K, V>(private val expiryTime: Long) : Cache<K, V> {
    private val clearTime = 300_000L
    private val hashMap = ConcurrentHashMap<K, ExpiringCacheValue<V>>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(clearTime)
                hashMap.clear()
            }
        }
    }

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