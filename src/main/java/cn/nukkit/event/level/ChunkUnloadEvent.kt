package cn.nukkit.event.level

import cn.nukkit.event.Cancellable

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ChunkUnloadEvent(chunk: FullChunk) : ChunkEvent(chunk), Cancellable {
    companion object {
        val handlers: HandlerList = HandlerList()
    }
}