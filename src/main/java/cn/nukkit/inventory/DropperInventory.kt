package cn.nukkit.inventory

import cn.nukkit.api.PowerNukkitDifference

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends EjectableInventory only in PowerNukkit")
class DropperInventory(blockEntity: BlockEntityDropper?) : EjectableInventory(blockEntity, InventoryType.DROPPER) {
    @Override
    override fun getHolder(): BlockEntityDropper {
        return super.getHolder() as BlockEntityDropper
    }
}