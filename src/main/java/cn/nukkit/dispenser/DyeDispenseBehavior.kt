package cn.nukkit.dispenser

import cn.nukkit.block.*

class DyeDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item): Item? {
        val target: Block = block.getSide(face)
        if (item.isFertilizer()) {
            if (target is BlockCrops || target is BlockSapling || target is BlockTallGrass
                    || target is BlockDoublePlant || target is BlockMushroom) {
                target.onActivate(item)
            } else {
                this.success = false
            }
            return null
        }
        return super.dispense(block, face, item)
    }
}