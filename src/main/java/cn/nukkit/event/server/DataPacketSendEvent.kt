package cn.nukkit.event.server

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class DataPacketSendEvent(player: Player, packet: DataPacket) : ServerEvent(), Cancellable {
    private val packet: DataPacket
    private val player: Player
    fun getPlayer(): Player {
        return player
    }

    fun getPacket(): DataPacket {
        return packet
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.packet = packet
        this.player = player
    }
}