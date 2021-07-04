package cn.nukkit.inventory

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author MagicDroidX (Nukkit Project)
 */
class CraftingGrid internal constructor(playerUI: PlayerUIInventory, offset: Int, size: Int) : PlayerUIComponent(playerUI, offset, size) {
    internal constructor(playerUI: PlayerUIInventory) : this(playerUI, 28, 4) {}
}