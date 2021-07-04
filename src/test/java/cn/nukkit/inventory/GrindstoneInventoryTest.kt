package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension::class)
internal class GrindstoneInventoryTest {
    @MockPlayer
    var player: Player? = null
    var playerUIInventory: PlayerUIInventory? = null
    var grindstoneInventory: GrindstoneInventory? = null
    @Test
    fun enchantedBook() {
        val enchantedBook: Item = Item.get(ItemID.ENCHANTED_BOOK, 0, 4)
        enchantedBook.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_FORTUNE_FISHING).setLevel(2))
        grindstoneInventory.setFirstItem(enchantedBook.clone(), false)
        assertEquals(Item.get(ItemID.BOOK, 0, 4), grindstoneInventory.getResult())
        for (i in 0..19) {
            assertThat(grindstoneInventory.getResultExperience()).isBetween(12 * 4, 24 * 4)
            grindstoneInventory.recalculateResultExperience()
        }
        grindstoneInventory.setSecondItem(enchantedBook.clone(), false)
        val air: Item = Item.get(0)
        assertEquals(air, grindstoneInventory.getResult())
        assertEquals(0, grindstoneInventory.getResultExperience())
        grindstoneInventory.setFirstItem(air, false)
        assertEquals(Item.get(ItemID.BOOK, 0, 4), grindstoneInventory.getResult())
        for (i in 0..19) {
            assertThat(grindstoneInventory.getResultExperience()).isBetween(12 * 4, 24 * 4)
            grindstoneInventory.recalculateResultExperience()
        }
        enchantedBook.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY))
        enchantedBook.setCount(1)
        grindstoneInventory.setSecondItem(enchantedBook.clone())
        assertEquals(Item.get(ItemID.BOOK, 0, 4), grindstoneInventory.getResult())
        for (i in 0..99) {
            assertThat(grindstoneInventory.getResultExperience()).isBetween(12 + 3, 24 + 5)
            grindstoneInventory.recalculateResultExperience()
        }
    }

    @BeforeEach
    fun setUp() {
        playerUIInventory = player.getUIInventory()
        grindstoneInventory = GrindstoneInventory(playerUIInventory, Position())
    }
}