package cn.nukkit.event.block

import cn.nukkit.Player

@PowerNukkitDifference(info = "Player is null when is filled by a hopper pushing the item", since = "1.4.0.0-PN")
class ComposterFillEvent(block: Block, player: Player, item: Item, chance: Int, success: Boolean) : BlockEvent(block), Cancellable {
    private val player: Player
    private val item: Item
    val chance: Int
    var isSuccess: Boolean
    fun getPlayer(): Player {
        return player
    }

    fun getItem(): Item {
        return item
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
        this.item = item
        this.chance = chance
        isSuccess = success
    }
}