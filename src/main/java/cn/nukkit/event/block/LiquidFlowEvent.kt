package cn.nukkit.event.block

import cn.nukkit.block.Block

class LiquidFlowEvent(to: Block, source: BlockLiquid, newFlowDecay: Int) : BlockEvent(to), Cancellable {
    private val to: Block
    private val source: BlockLiquid
    val newFlowDecay: Int
    fun getSource(): BlockLiquid {
        return source
    }

    fun getTo(): Block {
        return to
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.to = to
        this.source = source
        this.newFlowDecay = newFlowDecay
    }
}