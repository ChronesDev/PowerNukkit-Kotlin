package cn.nukkit.event.server

import cn.nukkit.Player

class BatchPacketsEvent(players: Array<Player>, packets: Array<DataPacket>, forceSync: Boolean) : ServerEvent(), Cancellable {
    private val players: Array<Player>
    private val packets: Array<DataPacket>
    val isForceSync: Boolean
    fun getPlayers(): Array<Player> {
        return players
    }

    fun getPackets(): Array<DataPacket> {
        return packets
    }

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.players = players
        this.packets = packets
        isForceSync = forceSync
    }
}