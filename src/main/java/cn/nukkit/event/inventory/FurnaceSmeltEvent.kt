package cn.nukkit.event.inventory

import cn.nukkit.blockentity.BlockEntityFurnace

/**
 * @author MagicDroidX (Nukkit Project)
 */
class FurnaceSmeltEvent(furnace: BlockEntityFurnace, source: Item, result: Item) : BlockEvent(furnace.getBlock()), Cancellable {
    private val furnace: BlockEntityFurnace
    private val source: Item
    private var result: Item
    fun getFurnace(): BlockEntityFurnace {
        return furnace
    }

    fun getSource(): Item {
        return source
    }

    fun getResult(): Item {
        return result
    }

    fun setResult(result: Item) {
        this.result = result
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.source = source.clone()
        this.source.setCount(1)
        this.result = result
        this.furnace = furnace
    }
}