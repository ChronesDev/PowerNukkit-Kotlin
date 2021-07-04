package cn.nukkit.event.block

import cn.nukkit.block.Block

class BlockFromToEvent(block: Block, to: Block) : BlockEvent(block), Cancellable {
    private var to: Block
    val from: Block
        get() = getBlock()

    fun getTo(): Block {
        return to
    }

    fun setTo(newTo: Block) {
        to = newTo
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.to = to
    }
}