package cn.nukkit.inventory

import cn.nukkit.level.Position

/**
 * @author MagicDroidX (Nukkit Project)
 */
class FakeBlockMenu(inventory: Inventory, pos: Position) : Position(pos.x, pos.y, pos.z, pos.level), InventoryHolder {
    private val inventory: Inventory

    @Override
    override fun getInventory(): Inventory {
        return inventory
    }

    init {
        this.inventory = inventory
    }
}