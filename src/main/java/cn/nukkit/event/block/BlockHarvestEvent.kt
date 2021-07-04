package cn.nukkit.event.block

import cn.nukkit.block.Block

class BlockHarvestEvent(block: Block, newState: Block, drops: Array<Item>) : BlockEvent(block), Cancellable {
    private var newState: Block
    private var drops: Array<Item>
    fun getNewState(): Block {
        return newState
    }

    fun setNewState(newState: Block) {
        this.newState = newState
    }

    fun getDrops(): Array<Item> {
        return drops
    }

    fun setDrops(drops: Array<Item>) {
        this.drops = drops
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.newState = newState
        this.drops = drops
    }
}