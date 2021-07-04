package cn.nukkit.dispenser

import cn.nukkit.api.PowerNukkitDifference

@PowerNukkitDifference(info = "Spend items in container, the dropper faces to (if there is one).", since = "1.4.0.0-PN")
@PowerNukkitOnly
class DropperDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item): Item? {
        val target: Block = block.getSide(face)
        if (block.level.getBlockEntityIfLoaded(target) is InventoryHolder) {
            val invHolder: InventoryHolder = block.level.getBlockEntityIfLoaded(target) as InventoryHolder
            val inv: Inventory = invHolder.getInventory()
            val clone: Item = item.clone()
            clone.count = 1
            if (inv.canAddItem(clone)) {
                inv.addItem(clone)
            } else {
                return clone
            }
        } else {
            block.level.addSound(block, Sound.RANDOM_CLICK)
            return super.dispense(block, face, item)
        }
        return null
    }
}