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
package cn.nukkit.item.enchantment

import cn.nukkit.block.BlockID

/**
 * @author joserobjr
 * @since 2021-02-15
 */
@ExtendWith(PowerNukkitExtension::class)
internal class EnchantmentTest {
    @ParameterizedTest
    @MethodSource("getEnchantmentData")
    fun testInternationalizationNames(data: EnchantmentData, enchantment: Enchantment) {
        assertEquals("%" + data.getI18n(), enchantment.getName())
    }

    @SuppressWarnings("deprecation")
    @ParameterizedTest
    @MethodSource("getEnchantmentData")
    fun testWeights(data: EnchantmentData, enchantment: Enchantment) {
        assertEquals(data.getWeight(), enchantment.getRarity().getWeight())
        assertEquals(data.getWeight(), enchantment.getWeight())
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentData")
    fun testMaxLevels(data: EnchantmentData, enchantment: Enchantment) {
        assertEquals(data.getLevels().length, enchantment.getMaxLevel())
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentData")
    fun testMinLevels(data: EnchantmentData?, enchantment: Enchantment) {
        assertNotNull(data)
        assertEquals(1, enchantment.getMinLevel())
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentDataWithLevels")
    fun testMinEnchantability(data: EnchantmentData, enchantment: Enchantment, level: Int) {
        val minEnchantAbility: Int = enchantment.getMinEnchantAbility(level)
        val levelData = data.getLevelData(level)
        assertEquals(levelData[0], minEnchantAbility)
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentDataWithLevels")
    fun testMaxEnchantability(data: EnchantmentData, enchantment: Enchantment, level: Int) {
        val maxEnchantAbility: Int = enchantment.getMaxEnchantAbility(level)
        val levelData = data.getLevelData(level)
        assertEquals(levelData[1], maxEnchantAbility)
    }

    @ParameterizedTest
    @MethodSource("getEnchantmentDataWithPrimaryItems")
    fun testPrimaryItem(enchantment: Enchantment, item: Item?) {
        assertTrue(enchantment.canEnchant(item))
    }

    @SuppressWarnings("deprecation")
    @ParameterizedTest
    @MethodSource("getEnchantmentDataWithSecondaryItems")
    fun testSecondaryItem(enchantment: Enchantment, item: Item?) {
        assertTrue(enchantment.canEnchant(item))
        assertTrue(enchantment.isItemAcceptable(item))
    }

    @SuppressWarnings("deprecation")
    @ParameterizedTest
    @MethodSource("getNotAcceptedItems")
    @Execution(ExecutionMode.CONCURRENT)
    fun testNotAcceptedItems(enchantment: Enchantment, item: Item?) {
        assertFalse(enchantment.canEnchant(item))
        assertFalse(enchantment.isItemAcceptable(item))
    }

    @Data
    internal class EnchantmentData {
        var id: String? = null
        var nid = 0
        var i18n: String? = null
        var incompatible: List<String>? = null
        var weight = 0
        var primary: List<ItemType>? = null
        var secondary: List<ItemType>? = null
        var levels: Array<IntArray>
        fun getLevelData(level: Int): IntArray {
            return levels[level - 1]
        }
    }

    internal enum class ItemType {
        helmet(LEATHER_CAP, IRON_HELMET, GOLD_HELMET, CHAIN_HELMET, DIAMOND_HELMET, NETHERITE_HELMET, TURTLE_SHELL), chestplate(LEATHER_TUNIC, IRON_CHESTPLATE, GOLD_CHESTPLATE, CHAIN_CHESTPLATE, DIAMOND_CHESTPLATE, NETHERITE_CHESTPLATE), leggings(LEATHER_PANTS, IRON_LEGGINGS, GOLD_LEGGINGS, CHAIN_LEGGINGS, DIAMOND_LEGGINGS, NETHERITE_LEGGINGS), boots(LEATHER_BOOTS, IRON_BOOTS, GOLD_BOOTS, CHAIN_BOOTS, DIAMOND_BOOTS, NETHERITE_BOOTS), elytra(ELYTRA), pumpkin(intArrayOf(BlockID.PUMPKIN), intArrayOf(255 - BlockID.CARVED_PUMPKIN)), skull(SKULL), sword(WOODEN_SWORD, STONE_SWORD, GOLD_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD), axe(WOODEN_AXE, STONE_AXE, GOLD_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE), hoe(WOODEN_HOE, STONE_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE), shovel(WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLD_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL), pickaxe(WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLD_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE), shears(SHEARS), fishing_rod(FISHING_ROD), carrot_on_stick(CARROT_ON_A_STICK), warped_fungus_on_stick(WARPED_FUNGUS_ON_A_STICK), bow(BOW), crossbow(CROSSBOW), trident(TRIDENT), flint_and_steel(FLINT_AND_STEEL), shield(SHIELD), compass(COMPASS);

        private val itemIds: IntArray
        private val notAccepted: IntArray

        constructor(vararg ids: Int) {
            itemIds = ids
            notAccepted = EmptyArrays.EMPTY_INTS
        }

        constructor(notAccepted: IntArray, ids: IntArray) {
            itemIds = ids
            this.notAccepted = notAccepted
        }
    }

    internal class EnchantmentTestList : ArrayList<EnchantmentData?>()

    @FunctionalInterface
    internal interface CheckedIntSupplier {
        @get:Throws(Exception::class)
        val asInt: Int
    }

    companion object {
        var allIds: IntArray = Stream.of(allItemIds,
                IntStream.of(255 - BlockID.CARVED_PUMPKIN, BlockID.PUMPKIN, BlockID.JACK_O_LANTERN)
        ).flatMapToInt(Function.identity()).toArray()

        @Getter
        var enchantmentDataList: List<EnchantmentData>? = null
        @SneakyThrows
        fun unchecked(supplier: CheckedIntSupplier): Int {
            return supplier.asInt
        }

        val allItemIds: IntStream
            get() = Arrays.stream(ItemID::class.java.getDeclaredFields())
                    .filter { field -> field.getType().equals(Int::class.javaPrimitiveType) }
                    .filter { field -> field.getModifiers() === Modifier.PUBLIC or Modifier.STATIC or Modifier.FINAL }
                    .mapToInt { field -> unchecked(CheckedIntSupplier { field.getInt(null) }) }
        val notAcceptedItems: Stream<Arguments>
            get() = enchantmentDataList.parallelStream().flatMap { data ->
                val acceptedItems: IntSet = IntOpenHashSet()
                val notAccepted: IntSet = IntOpenHashSet(0)
                Stream.of(data.primary.stream(), data.secondary.stream()).flatMap(Function.identity()).distinct()
                        .forEachOrdered { type ->
                            acceptedItems.addAll(IntArrayList(type.itemIds))
                            if (type.notAccepted.length > 0) {
                                notAccepted.addAll(IntArrayList(type.notAccepted))
                            }
                        }
                val enchantment: Enchantment = Enchantment.getEnchantment(data.nid)
                val allIdsStream: IntStream = IntStream.of(allIds)
                val idStream: IntStream = if (notAccepted.isEmpty()) allIdsStream else Stream.of(IntStream.of(allIds), IntStream.of(notAccepted.toIntArray()))
                        .flatMapToInt(Function.identity())
                idStream.parallel()
                        .filter { id -> !acceptedItems.contains(id) }
                        .mapToObj { id -> Arguments.of(enchantment, Item.get(id)) }
            }
        val enchantmentDataWithSecondaryItems: Stream<Arguments>
            get() = enchantmentDataList.stream()
                    .flatMap { data ->
                        data.secondary.stream().flatMap { type ->
                            Arrays.stream(type.itemIds).mapToObj(Item::get)
                                    .map { item -> Arguments.of(Enchantment.getEnchantment(data.nid), item) }
                        }
                    }
        val enchantmentDataWithPrimaryItems: Stream<Arguments>
            get() = enchantmentDataList.stream()
                    .flatMap { data ->
                        data.primary.stream().flatMap { type ->
                            Arrays.stream(type.itemIds).mapToObj(Item::get)
                                    .map { item -> Arguments.of(Enchantment.getEnchantment(data.nid), item) }
                        }
                    }
        val enchantmentDataWithLevels: Stream<Arguments>
            get() = enchantmentDataList.stream()
                    .flatMap { data ->
                        IntStream.range(0, data.levels.length)
                                .mapToObj { index -> Arguments.of(data, Enchantment.getEnchantment(data.nid), index + 1) }
                    }
        val enchantmentData: Stream<Arguments>
            get() = enchantmentDataList.stream().map { data -> Arguments.of(data, Enchantment.getEnchantment(data.getNid())) }

        @BeforeAll
        @Throws(IOException::class)
        fun beforeAll() {
            EnchantmentTest::class.java.getResourceAsStream("enchantments.json").use { `is` ->
                InputStreamReader(`is`, StandardCharsets.UTF_8).use { reader ->
                    enchantmentDataList = Gson().fromJson(reader, EnchantmentTestList::class.java)
                    for (enchantmentData in enchantmentDataList!!) {
                        try {
                            assertTrue(enchantmentData.primary.stream().noneMatch(Objects::isNull))
                            assertTrue(enchantmentData.secondary.stream().noneMatch(Objects::isNull))
                        } catch (e: AssertionFailedError) {
                            throw AssertionFailedError(
                                    "One of the primary or secondary items in enchantment data of the " +
                                            enchantmentData.getId().toString() + " enchantment was null", e)
                        }
                    }
                }
            }
        }
    }
}