package cn.nukkit.event.block

import cn.nukkit.block.Block

class TurtleEggHatchEvent(turtleEgg: BlockTurtleEgg, var eggsHatching: Int, newState: Block) : BlockEvent(turtleEgg), Cancellable {
    private var newState: Block
    var isRecalculateOnFailure = true
    fun recalculateNewState() {
        var turtleEgg: BlockTurtleEgg = getBlock()
        val eggCount: Int = turtleEgg.getEggCount()
        val eggsHatching = eggsHatching
        if (eggCount <= eggsHatching) {
            newState = BlockAir()
        } else {
            turtleEgg = turtleEgg.clone()
            turtleEgg.setEggCount(eggCount - eggsHatching)
            newState = turtleEgg
        }
    }

    fun getNewState(): Block {
        return newState
    }

    fun setNewState(newState: Block) {
        this.newState = newState
    }

    @Override
    override fun getBlock(): BlockTurtleEgg {
        return super.getBlock() as BlockTurtleEgg
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.newState = newState
    }
}