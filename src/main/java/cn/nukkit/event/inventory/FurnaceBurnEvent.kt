package cn.nukkit.event.inventory

import cn.nukkit.blockentity.BlockEntityFurnace

/**
 * @author MagicDroidX (Nukkit Project)
 */
class FurnaceBurnEvent(furnace: BlockEntityFurnace, fuel: Item, burnTime: Short) : BlockEvent(furnace.getBlock()), Cancellable {
    private val furnace: BlockEntityFurnace
    private val fuel: Item
    var burnTime: Short
    var isBurning = true
    fun getFurnace(): BlockEntityFurnace {
        return furnace
    }

    fun getFuel(): Item {
        return fuel
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.fuel = fuel
        this.burnTime = burnTime
        this.furnace = furnace
    }
}