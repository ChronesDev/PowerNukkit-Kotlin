package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author joserobjr
 */
@ExtendWith(PowerNukkitExtension::class)
internal class EnchantInventoryTest {
    @MockPlayer
    var player: Player? = null
    var playerInventory: PlayerInventory? = null
    var playerUIInventory: PlayerUIInventory? = null
    var enchantInventory: EnchantInventory? = null
    @Test
    fun close() {
        doCallRealMethod().`when`(player).resetCraftingGridType()
        enchantInventory.setItem(0, Item.get(ItemID.IRON_SWORD))
        enchantInventory.close(player)
        var count = 0
        for (i in 0 until playerInventory.getSize()) {
            if (playerInventory.getItem(i).getId() === ItemID.IRON_SWORD) {
                count++
            }
        }
        for (i in 0 until playerUIInventory.getSize()) {
            if (playerUIInventory.getItem(i).getId() === ItemID.IRON_SWORD) {
                count++
            }
        }
        assertEquals(1, count, "The sword was duplicated!")
    }

    @BeforeEach
    fun setUp() {
        playerInventory = player.getInventory()
        playerUIInventory = player.getUIInventory()
        enchantInventory = EnchantInventory(playerUIInventory, Position(1, 2, 3, player.getLevel()))
    }
}