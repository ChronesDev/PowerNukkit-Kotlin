package cn.nukkit.event.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockPlaceEvent(player: Player, blockPlace: Block, blockReplace: Block, blockAgainst: Block, item: Item) : BlockEvent(blockPlace), Cancellable {
    protected val player: Player
    protected val item: Item
    protected val blockReplace: Block
    protected val blockAgainst: Block
    fun getPlayer(): Player {
        return player
    }

    fun getItem(): Item {
        return item
    }

    fun getBlockReplace(): Block {
        return blockReplace
    }

    fun getBlockAgainst(): Block {
        return blockAgainst
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.blockReplace = blockReplace
        this.blockAgainst = blockAgainst
        this.item = item
        this.player = player
    }
}