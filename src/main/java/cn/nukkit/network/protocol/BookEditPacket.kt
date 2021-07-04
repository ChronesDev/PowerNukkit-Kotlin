package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class BookEditPacket : DataPacket() {
    var action: Action? = null
    var inventorySlot = 0
    var pageNumber = 0
    var secondaryPageNumber = 0
    var text: String? = null
    var photoName: String? = null
    var title: String? = null
    var author: String? = null
    var xuid: String? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        action = Action.values()[this.getByte()]
        inventorySlot = this.getByte()
        when (action) {
            Action.REPLACE_PAGE, Action.ADD_PAGE -> {
                pageNumber = this.getByte()
                text = this.getString()
                photoName = this.getString()
            }
            Action.DELETE_PAGE -> pageNumber = this.getByte()
            Action.SWAP_PAGES -> {
                pageNumber = this.getByte()
                secondaryPageNumber = this.getByte()
            }
            Action.SIGN_BOOK -> {
                title = this.getString()
                author = this.getString()
                xuid = this.getString()
            }
        }
    }

    @Override
    override fun encode() {
    }

    enum class Action {
        REPLACE_PAGE, ADD_PAGE, DELETE_PAGE, SWAP_PAGES, SIGN_BOOK
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.BOOK_EDIT_PACKET
    }
}