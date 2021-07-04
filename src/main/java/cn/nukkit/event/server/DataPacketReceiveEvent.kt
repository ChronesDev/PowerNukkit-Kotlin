package cn.nukkit.event.server

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class DataPacketReceiveEvent(player: Player, packet: DataPacket) : ServerEvent(), Cancellable {
    private val packet: DataPacket
    private val player: Player
    fun getPacket(): DataPacket {
        return packet
    }

    fun getPlayer(): Player {
        return player
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.packet = packet
        this.player = player
    }
}