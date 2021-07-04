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

import org.junit.jupiter.api.BeforeEach

/**
 * @author joserobjr
 * @since 2020-10-05
 */
internal class ConvertingSetWrapperTest {
    private val proxy: Set<String> = HashSet()
    private val set: ConvertingSetWrapper<Integer, String> = ConvertingSetWrapper(proxy, Object::toString, Integer::parseInt)
    @BeforeEach
    fun setUp() {
        proxy.clear()
    }

    @Test
    operator fun iterator() {
        proxy.add("1")
        proxy.add("2")
        proxy.add("3")
        val iterator: Iterator<Integer> = set.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(2, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(3, iterator.next())
        assertFalse(iterator.hasNext())
    }

    @Test
    fun size() {
        assertEquals(0, set.size())
        proxy.add("5")
        assertEquals(1, set.size())
    }

    @get:Test
    val isEmpty: Unit
        get() {
            assertTrue(set.isEmpty())
            proxy.add("5")
            assertFalse(set.isEmpty())
        }

    @Test
    fun contains() {
        assertFalse(set.contains(5))
        proxy.add("5")
        assertTrue(set.contains(5))
        assertThrows(ClassCastException::class.java) { set.contains("5") }
    }

    @Test
    fun add() {
        assertTrue(set.add(3))
        assertTrue(proxy.contains("3"))
        assertFalse(set.add(3))
    }

    @Test
    fun remove() {
        assertFalse(set.remove(4))
        proxy.add("4")
        assertTrue(set.remove(4))
        assertFalse(proxy.contains("4"))
        assertFalse(set.remove(4))
        assertThrows(ClassCastException::class.java) { set.remove("4") }
    }

    @Test
    fun clear() {
        proxy.add("8")
        set.clear()
        assertTrue(proxy.isEmpty())
    }
}