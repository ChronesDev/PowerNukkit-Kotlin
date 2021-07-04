package cn.nukkit.event.inventory

import cn.nukkit.blockentity.BlockEntityCampfire

/**
 * @author MagicDroidX (Nukkit Project)
 */
class CampfireSmeltEvent(campfire: BlockEntityCampfire, source: Item, result: Item) : BlockEvent(campfire.getBlock()), Cancellable {
    private val campfire: BlockEntityCampfire
    private val source: Item
    private var result: Item
    var keepItem = false
    fun getCampfire(): BlockEntityCampfire {
        return campfire
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
        this.campfire = campfire
    }
}