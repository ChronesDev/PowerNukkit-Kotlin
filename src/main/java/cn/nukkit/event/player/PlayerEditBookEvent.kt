package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerEditBookEvent(player: Player?, oldBook: Item, newBook: Item, action: BookEditPacket.Action) : PlayerEvent(), Cancellable {
    private val oldBook: Item
    private val action: BookEditPacket.Action
    private var newBook: Item
    fun getAction(): BookEditPacket.Action {
        return action
    }

    fun getOldBook(): Item {
        return oldBook
    }

    fun getNewBook(): Item {
        return newBook
    }

    fun setNewBook(book: Item) {
        newBook = book
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.oldBook = oldBook
        this.newBook = newBook
        this.action = action
    }
}