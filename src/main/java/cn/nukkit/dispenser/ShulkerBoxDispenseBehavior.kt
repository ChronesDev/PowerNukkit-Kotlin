package cn.nukkit.dispenser

import cn.nukkit.block.Block

class ShulkerBoxDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item): Item? {
        val target: Block = block.getSide(face)
        if (!target.canBeReplaced()) {
            success = false
            return null
        }
        val shulkerBox: BlockUndyedShulkerBox = item.getBlock().clone() as BlockUndyedShulkerBox
        shulkerBox.level = block.level
        shulkerBox.layer = 0
        shulkerBox.x = target.x
        shulkerBox.y = target.y
        shulkerBox.z = target.z
        val shulkerBoxFace: BlockFace = if (shulkerBox.down().isTransparent()) face else BlockFace.UP
        if (shulkerBox.place(item, target, target.getSide(shulkerBoxFace.getOpposite()), shulkerBoxFace, 0, 0, 0, null).also { success = it }) {
            block.level.updateComparatorOutputLevel(target)
        }
        return null
    }
}