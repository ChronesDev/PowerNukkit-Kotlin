package cn.nukkit.inventory

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author CreeperFace
 */
class BigCraftingGrid internal constructor(playerUI: PlayerUIInventory) : CraftingGrid(playerUI, 32, 9) {
    @Override
    override fun getType(): InventoryType {
        return InventoryType.WORKBENCH
    }
}