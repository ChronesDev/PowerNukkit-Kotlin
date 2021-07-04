package cn.nukkit.event.block

import cn.nukkit.Player

class LecternPageChangeEvent(player: Player, lectern: BlockEntityLectern, newPage: Int) : BlockEvent(lectern.getBlock()), Cancellable {
    private val player: Player
    private val lectern: BlockEntityLectern
    var newRawPage: Int
    fun getLectern(): BlockEntityLectern {
        return lectern
    }

    var leftPage: Int
        get() = newRawPage * 2 + 1
        set(newLeftPage) {
            newRawPage = (newLeftPage - 1) / 2
        }
    var rightPage: Int
        get() = leftPage + 1
        set(newRightPage) {
            leftPage = newRightPage - 1
        }
    val maxPage: Int
        get() = lectern.getTotalPages()

    fun getPlayer(): Player {
        return player
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
        this.lectern = lectern
        newRawPage = newPage
    }
}