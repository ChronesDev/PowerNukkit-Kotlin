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
internal class ConvertingMapWrapperTest {
    private val proxy: Map<Long, String> = HashMap()
    private val map: ConvertingMapWrapper<Long, Integer, String> = ConvertingMapWrapper(proxy, Objects::toString, Integer::parseInt)
    @BeforeEach
    fun setUp() {
        proxy.clear()
    }

    @Test
    fun entrySet() {
        proxy.put(1L, "2")
        proxy.put(3L, "4")
        proxy.put(5L, "6")
        val entries: Set<Map.Entry<Long, Integer>> = map.entrySet()
        val expected: Set<Map.Entry<Long, Integer>> = HashSet()
        expected.add(SimpleEntry(1L, 2))
        expected.add(SimpleEntry(3L, 4))
        expected.add(SimpleEntry(5L, 6))
        assertEquals(expected, entries)
    }

    @Test
    fun size() {
        assertEquals(0, map.size())
        proxy.put(1L, "2")
        assertEquals(1, map.size())
        proxy.put(3L, "4")
        assertEquals(2, map.size())
    }

    @get:Test
    val isEmpty: Unit
        get() {
            assertTrue(map.isEmpty())
            proxy.put(1L, "2")
            assertFalse(map.isEmpty())
            proxy.put(3L, "4")
            assertFalse(map.isEmpty())
        }

    @Test
    fun containsValue() {
        assertFalse(map.containsValue(1))
        proxy.put(0L, "1")
        assertTrue(map.containsValue(1))
        proxy.remove(0L)
        assertFalse(map.containsValue(1))
    }

    @Test
    fun containsKey() {
        assertFalse(map.containsKey(0L))
        proxy.put(0L, "1")
        assertTrue(map.containsKey(0L))
        proxy.remove(0L)
        assertFalse(map.containsKey(0L))
    }

    @Test
    fun get() {
        assertNull(map.get(1L))
        proxy.put(1L, "2")
        assertEquals(2, map.get(1L))
        proxy.remove(1L)
        assertNull(map.get(1L))
    }

    @Test
    fun put() {
        assertNull(map.put(1L, 2))
        assertEquals("2", proxy[1L])
        assertEquals(2, map.put(1L, 3))
        assertEquals("3", proxy[1L])
    }

    @Test
    fun remove() {
        assertNull(map.remove(1L))
        proxy.put(1L, "2")
        assertEquals(2, map.remove(1L))
        assertFalse(proxy.containsKey(1L))
        assertNull(map.remove(1L))
        proxy.put(1L, "3")
        assertFalse(map.remove(1L, 2))
        assertEquals("3", proxy[1L])
        assertTrue(map.remove(1L, 3))
        assertNull(proxy[1L])
    }

    @Test
    fun clear() {
        proxy.put(1L, "0")
        assertFalse(map.isEmpty())
        proxy.clear()
        assertTrue(map.isEmpty())
    }

    @Test
    fun keySet() {
        proxy.put(1L, "-3")
        assertEquals(proxy.keySet(), map.keySet())
    }
}