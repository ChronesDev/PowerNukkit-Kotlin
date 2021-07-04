package cn.nukkit.inventory

import cn.nukkit.entity.item.EntityMinecartHopper

class MinecartHopperInventory(minecart: EntityMinecartHopper?) : ContainerInventory(minecart, InventoryType.MINECART_HOPPER) {
    @Override
    override fun getHolder(): EntityMinecartHopper {
        return super.getHolder() as EntityMinecartHopper
    }
}