package cn.nukkit.dispenser

import cn.nukkit.block.BlockDispenser

/**
 * @author CreeperFace
 */
interface DispenseBehavior {
    fun dispense(block: BlockDispenser?, face: BlockFace?, item: Item?): Item?
}