package cn.nukkit.event.block

import cn.nukkit.Player

class LecternPlaceBookEvent(player: Player, lectern: BlockEntityLectern, book: Item) : BlockEvent(lectern.getBlock()), Cancellable {
    private val player: Player
    private val lectern: BlockEntityLectern
    private var book: Item
    fun getLectern(): BlockEntityLectern {
        return lectern
    }

    fun getPlayer(): Player {
        return player
    }

    fun getBook(): Item {
        return book.clone()
    }

    fun setBook(book: Item) {
        this.book = book
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
        this.lectern = lectern
        this.book = book
    }
}