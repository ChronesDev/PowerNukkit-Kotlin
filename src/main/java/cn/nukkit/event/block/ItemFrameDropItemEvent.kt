package cn.nukkit.event.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 03.07.2016
 */
class ItemFrameDropItemEvent(@Nullable player: Player, @Nonnull block: Block, @Nonnull itemFrame: BlockEntityItemFrame, @Nonnull item: Item) : BlockEvent(block), Cancellable {
    private val player: Player
    private val item: Item
    private val itemFrame: BlockEntityItemFrame
    @Nullable
    fun getPlayer(): Player {
        return player
    }

    @Nonnull
    fun getItemFrame(): BlockEntityItemFrame {
        return itemFrame
    }

    @Nonnull
    fun getItem(): Item {
        return item
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
        this.itemFrame = itemFrame
        this.item = item
    }
}