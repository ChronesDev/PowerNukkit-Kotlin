package cn.nukkit.inventory

import cn.nukkit.entity.item.EntityMinecartChest

class MinecartChestInventory(minecart: EntityMinecartChest?) : ContainerInventory(minecart, InventoryType.MINECART_CHEST) {
    @Override
    override fun getHolder(): EntityMinecartChest {
        return this.holder as EntityMinecartChest
    }
}