package cn.nukkit.event.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class SignChangeEvent(block: Block, player: Player, lines: Array<String?>) : BlockEvent(block), Cancellable {
    private val player: Player
    val lines = arrayOfNulls<String>(4)
    fun getPlayer(): Player {
        return player
    }

    fun getLine(index: Int): String? {
        return lines[index]
    }

    fun setLine(index: Int, line: String?) {
        lines[index] = line
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.player = player
        this.lines = lines
    }
}