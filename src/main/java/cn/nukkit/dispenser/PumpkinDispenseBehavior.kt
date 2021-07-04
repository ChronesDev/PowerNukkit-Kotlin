package cn.nukkit.dispenser

import cn.nukkit.block.Block

class PumpkinDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace?, item: Item?): Item? {
        val target: Block = block.getSide(face)

        //TODO: snowman / golem
        return null
    }
}