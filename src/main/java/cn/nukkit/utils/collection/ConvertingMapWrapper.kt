/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.nukkit.utils.collection

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 * @since 2020-10-05
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ConvertingMapWrapper<K, V1, V2> @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(private val proxied: Map<K, V2>, converter: Function<V1, V2>, reverseConverter: Function<V2, V1>, convertReturnedNulls: Boolean) : AbstractMap<K, V1>() {
    private val converter: Function<V1, V2>
    private val reverseConverter: Function<V2, V1>
    private val entrySet: ConvertingSetWrapper<Entry<K, V1>, Entry<K, V2>>
    private val convertReturnedNulls: Boolean

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(proxied: Map<K, V2>, converter: Function<V1, V2>, reverseConverter: Function<V2, V1>) : this(proxied, converter, reverseConverter, false) {
    }

    @Override
    fun entrySet(): Set<Entry<K, V1>> {
        return entrySet
    }

    @Override
    fun size(): Int {
        return proxied.size()
    }

    @get:Override
    val isEmpty: Boolean
        get() = proxied.isEmpty()

    @Override
    fun containsValue(value: Object?): Boolean {
        val uncheckedConverter: Function = converter
        val converted: Object = uncheckedConverter.apply(value)
        return proxied.containsValue(converted)
    }

    @Override
    fun containsKey(key: Object): Boolean {
        return proxied.containsKey(key)
    }

    @Override
    operator fun get(key: Object): V1? {
        val found = proxied[key]
        return if (found == null && !convertReturnedNulls) {
            null
        } else reverseConverter.apply(found)
    }

    @Override
    fun put(key: K, value: V1): V1? {
        val removed: V2 = proxied.put(key, converter.apply(value))
        return if (removed == null && !convertReturnedNulls) {
            null
        } else reverseConverter.apply(removed)
    }

    @Override
    fun remove(key: Object?): V1? {
        val removed: V2 = proxied.remove(key)
        return if (removed == null && !convertReturnedNulls) {
            null
        } else reverseConverter.apply(removed)
    }

    @Override
    fun remove(key: Object?, value: Object?): Boolean {
        val uncheckedConverter: Function = converter
        val converted: Object = uncheckedConverter.apply(value)
        return proxied.remove(key, converted)
    }

    @Override
    fun clear() {
        proxied.clear()
    }

    @Override
    fun keySet(): Set<K> {
        return proxied.keySet()
    }

    private inner class EntryWrapper<E1, E2>(entryProxied: Entry<K, E2>, entryConverter: Function<E1, E2>, entryReverseConverter: Function<E2, E1>) : Map.Entry<K, E1> {
        private val entryConverter: Function<E1, E2>
        private val entryReverseConverter: Function<E2, E1>
        private val entryProxied: Map.Entry<K, E2>

        @get:Override
        override val key: K
            get() = entryProxied.getKey()

        @get:Override
        override val value: E1?
            get() {
                val value: E2 = entryProxied.getValue()
                return if (value == null && !convertReturnedNulls) {
                    null
                } else entryReverseConverter.apply(value)
            }

        @Override
        fun setValue(value: E1): E1? {
            val newValue: E2 = entryConverter.apply(value)
            val oldValue: E2 = entryProxied.setValue(newValue)
            return if (oldValue == null && !convertReturnedNulls) {
                null
            } else entryReverseConverter.apply(oldValue)
        }

        @Override
        override fun toString(): String {
            return entryProxied.getKey().toString() + "=" + value
        }

        @Override
        override fun equals(o: Object): Boolean {
            if (o === this) {
                return true
            }
            if (o is Map.Entry) {
                val e = o as Map.Entry<*, *>
                return Objects.equals(entryProxied.getKey(), e.getKey()) && Objects.equals(value, e.getValue())
            }
            return false
        }

        @Override
        override fun hashCode(): Int {
            return Objects.hashCode(entryProxied.getKey()) xor Objects.hashCode(value)
        }

        init {
            this.entryConverter = entryConverter
            this.entryReverseConverter = entryReverseConverter
            this.entryProxied = entryProxied
        }
    }

    init {
        this.converter = converter
        this.reverseConverter = reverseConverter
        this.convertReturnedNulls = convertReturnedNulls
        entrySet = ConvertingSetWrapper(
                proxied.entrySet(),
                { entry -> EntryWrapper<E1, E2>(entry, reverseConverter, converter) }
        ) { entry -> EntryWrapper<E1, E2>(entry, converter, reverseConverter) }
    }
}