package cn.nukkit.event.block

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class BlockEvent(block: Block) : Event() {
    protected val block: Block
    fun getBlock(): Block {
        return block
    }

    init {
        this.block = block
    }
}