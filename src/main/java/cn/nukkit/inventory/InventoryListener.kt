package cn.nukkit.inventory

import cn.nukkit.item.Item

interface InventoryListener {
    fun onInventoryChanged(inventory: Inventory?, oldItem: Item?, slot: Int)
}