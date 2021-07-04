package cn.nukkit.inventory

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface InventoryHolder {
    fun getInventory(): Inventory?
}