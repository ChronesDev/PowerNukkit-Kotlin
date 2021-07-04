package cn.nukkit.inventory

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface Inventory {
    val size: Int
    var maxStackSize: Int
    val name: String?
    val title: String?
    fun getItem(index: Int): Item
    fun setItem(index: Int, item: Item?): Boolean {
        return setItem(index, item, true)
    }

    fun setItem(index: Int, item: Item?, send: Boolean): Boolean
    fun addItem(vararg slots: Item?): Array<Item?>?
    fun canAddItem(item: Item?): Boolean
    fun removeItem(vararg slots: Item?): Array<Item?>?
    var contents: Map<Any?, Any?>?
    fun sendContents(player: Player?)
    fun sendContents(vararg players: Player?)
    fun sendContents(players: Collection<Player?>?)
    fun sendSlot(index: Int, player: Player?)
    fun sendSlot(index: Int, vararg players: Player?)
    fun sendSlot(index: Int, players: Collection<Player?>?)
    operator fun contains(item: Item?): Boolean
    fun all(item: Item?): Map<Integer?, Item?>?
    fun first(item: Item?): Int {
        return first(item, false)
    }

    fun first(item: Item?, exact: Boolean): Int
    fun firstEmpty(item: Item?): Int
    fun decreaseCount(slot: Int)
    fun remove(item: Item?)

    @JvmOverloads
    fun clear(index: Int, send: Boolean = true): Boolean
    fun clearAll()
    val isFull: Boolean
    val isEmpty: Boolean
    val viewers: Set<Any?>?
    val type: cn.nukkit.inventory.InventoryType?
    val holder: cn.nukkit.inventory.InventoryHolder?
    fun onOpen(who: Player?)
    fun open(who: Player?): Boolean
    fun close(who: Player?)
    fun onClose(who: Player?)
    fun onSlotChange(index: Int, before: Item?, send: Boolean)
    fun addListener(listener: InventoryListener?)
    fun removeListener(listener: InventoryListener?)

    companion object {
        const val MAX_STACK = 64
    }
}