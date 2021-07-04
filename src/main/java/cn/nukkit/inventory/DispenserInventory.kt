package cn.nukkit.inventory

import cn.nukkit.api.PowerNukkitDifference

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends EjectableInventory only in PowerNukkit")
class DispenserInventory(blockEntity: BlockEntityDispenser?) : EjectableInventory(blockEntity, InventoryType.DISPENSER) {
    @Override
    override fun getHolder(): BlockEntityDispenser {
        return super.getHolder() as BlockEntityDispenser
    }
}