package cn.nukkit.event.player

import cn.nukkit.Player

class PlayerChunkRequestEvent(player: Player?, chunkX: Int, chunkZ: Int) : PlayerEvent(), Cancellable {
    val chunkX: Int
    val chunkZ: Int

    companion object {
        val handlers: HandlerList = HandlerList()
    }

    init {
        player = player
        this.chunkX = chunkX
        this.chunkZ = chunkZ
    }
}