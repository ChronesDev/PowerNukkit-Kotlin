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
class ConvertingSetWrapper<V1, V2> @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(private val proxied: Set<V2>, converter: Function<V1, V2>, reverseConverter: Function<V2, V1>) : AbstractSet<V1>() {
    private val converter: Function<V1, V2>
    private val reverseConverter: Function<V2, V1>

    @Override
    @Nonnull
    operator fun iterator(): Iterator<V1> {
        return ConvertingIterator()
    }

    @Override
    fun size(): Int {
        return proxied.size()
    }

    @get:Override
    val isEmpty: Boolean
        get() = proxied.isEmpty()

    @Override
    operator fun contains(o: Object?): Boolean {
        val uncheckedConverter: Function = converter
        val converted: Object = uncheckedConverter.apply(o)
        return proxied.contains(converted)
    }

    @Override
    fun add(v1: V1): Boolean {
        return proxied.add(converter.apply(v1))
    }

    @Override
    fun remove(o: Object?): Boolean {
        val uncheckedConverter: Function = converter
        val converted: Object = uncheckedConverter.apply(o)
        return proxied.remove(converted)
    }

    @Override
    fun clear() {
        proxied.clear()
    }

    private inner class ConvertingIterator : Iterator<V1> {
        private val proxiedIterator = proxied.iterator()
        @Override
        fun remove() {
            proxiedIterator.remove()
        }

        @Override
        override fun hasNext(): Boolean {
            return proxiedIterator.hasNext()
        }

        @Override
        override fun next(): V1 {
            return reverseConverter.apply(proxiedIterator.next())
        }
    }

    init {
        this.converter = converter
        this.reverseConverter = reverseConverter
    }
}