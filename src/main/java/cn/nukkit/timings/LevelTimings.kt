package cn.nukkit.timings

import cn.nukkit.level.Level

/**
 * @author Pub4Game
 * @author Tee7even
 */
class LevelTimings(level: Level) {
    val doChunkUnload: Timing
    val doTickPending: Timing
    val doChunkGC: Timing
    val doTick: Timing
    val tickChunks: Timing
    val entityTick: Timing
    val blockEntityTick: Timing
    val syncChunkSendTimer: Timing
    val syncChunkSendPrepareTimer: Timing
    val syncChunkLoadTimer: Timing
    val syncChunkLoadDataTimer: Timing
    val syncChunkLoadEntitiesTimer: Timing
    val syncChunkLoadBlockEntitiesTimer: Timing

    init {
        val name: String = level.getFolderName().toString() + " - "
        doChunkUnload = TimingsManager.getTiming(name + "doChunkUnload")
        doTickPending = TimingsManager.getTiming(name + "doTickPending")
        doChunkGC = TimingsManager.getTiming(name + "doChunkGC")
        doTick = TimingsManager.getTiming(name + "doTick")
        tickChunks = TimingsManager.getTiming(name + "tickChunks")
        entityTick = TimingsManager.getTiming(name + "entityTick")
        blockEntityTick = TimingsManager.getTiming(name + "blockEntityTick")
        syncChunkSendTimer = TimingsManager.getTiming(name + "syncChunkSend")
        syncChunkSendPrepareTimer = TimingsManager.getTiming(name + "syncChunkSendPrepare")
        syncChunkLoadTimer = TimingsManager.getTiming(name + "syncChunkLoad")
        syncChunkLoadDataTimer = TimingsManager.getTiming(name + "syncChunkLoad - Data")
        syncChunkLoadEntitiesTimer = TimingsManager.getTiming(name + "syncChunkLoad - Entities")
        syncChunkLoadBlockEntitiesTimer = TimingsManager.getTiming(name + "syncChunkLoad - BlockEntities")
    }
}