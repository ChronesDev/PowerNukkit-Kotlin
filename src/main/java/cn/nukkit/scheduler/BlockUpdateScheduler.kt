package cn.nukkit.scheduler

import cn.nukkit.block.Block

class BlockUpdateScheduler(level: Level, currentTick: Long) {
    private val level: Level
    private var lastTick: Long
    private val queuedUpdates: Map<Long, LinkedHashSet<BlockUpdateEntry>>
    private var pendingUpdates: Set<BlockUpdateEntry>? = null
    @Synchronized
    fun tick(currentTick: Long) {
        // Should only perform once, unless ticks were skipped
        if (currentTick - lastTick < Short.MAX_VALUE) { // Arbitrary
            for (tick in lastTick + 1..currentTick) {
                perform(tick)
            }
        } else {
            val times: ArrayList<Long> = ArrayList(queuedUpdates.keySet())
            Collections.sort(times)
            for (tick in times) {
                if (tick <= currentTick) {
                    perform(tick)
                } else {
                    break
                }
            }
        }
        lastTick = currentTick
    }

    private fun perform(tick: Long) {
        try {
            lastTick = tick
            pendingUpdates = queuedUpdates.remove(tick)
            val updates: Set<BlockUpdateEntry>? = pendingUpdates
            if (updates != null) {
                val updateIterator: Iterator<BlockUpdateEntry> = updates.iterator()
                while (updateIterator.hasNext()) {
                    val entry: BlockUpdateEntry = updateIterator.next()
                    val pos: Vector3 = entry.pos
                    if (level.isChunkLoaded(NukkitMath.floorDouble(pos.x) shr 4, NukkitMath.floorDouble(pos.z) shr 4)) {
                        val block: Block = level.getBlock(entry.pos, entry.block.layer)
                        updateIterator.remove()
                        if (Block.equals(block, entry.block, false)) {
                            block.onUpdate(Level.BLOCK_UPDATE_SCHEDULED)
                        }
                    } else {
                        level.scheduleUpdate(entry.block, entry.pos, 0)
                    }
                }
            }
        } finally {
            pendingUpdates = null
        }
    }

    fun getPendingBlockUpdates(boundingBox: AxisAlignedBB): Set<BlockUpdateEntry>? {
        var set: Set<BlockUpdateEntry>? = null
        for (tickEntries in queuedUpdates.entrySet()) {
            val tickSet: LinkedHashSet<BlockUpdateEntry> = tickEntries.getValue()
            for (update in tickSet) {
                val pos: Vector3 = update.pos
                if (pos.getX() >= boundingBox.getMinX() && pos.getX() < boundingBox.getMaxX() && pos.getZ() >= boundingBox.getMinZ() && pos.getZ() < boundingBox.getMaxZ()) {
                    if (set == null) {
                        set = LinkedHashSet()
                    }
                    set.add(update)
                }
            }
        }
        return set
    }

    fun isBlockTickPending(pos: Vector3?, block: Block?): Boolean {
        val tmpUpdates: Set<BlockUpdateEntry>? = pendingUpdates
        return if (tmpUpdates == null || tmpUpdates.isEmpty()) false else tmpUpdates.contains(BlockUpdateEntry(pos, block))
    }

    private fun getMinTime(entry: BlockUpdateEntry): Long {
        return Math.max(entry.delay, lastTick + 1)
    }

    fun add(entry: BlockUpdateEntry) {
        val time = getMinTime(entry)
        var updateSet: LinkedHashSet<BlockUpdateEntry>? = queuedUpdates[time]
        if (updateSet == null) {
            val tmp: LinkedHashSet<BlockUpdateEntry> = queuedUpdates.putIfAbsent(time, LinkedHashSet().also { updateSet = it })
            if (tmp != null) updateSet = tmp
        }
        updateSet.add(entry)
    }

    operator fun contains(entry: BlockUpdateEntry?): Boolean {
        for (tickUpdateSet in queuedUpdates.entrySet()) {
            if (tickUpdateSet.getValue().contains(entry)) {
                return true
            }
        }
        return false
    }

    fun remove(entry: BlockUpdateEntry?): Boolean {
        for (tickUpdateSet in queuedUpdates.entrySet()) {
            if (tickUpdateSet.getValue().remove(entry)) {
                return true
            }
        }
        return false
    }

    fun remove(pos: Vector3?): Boolean {
        for (tickUpdateSet in queuedUpdates.entrySet()) {
            if (tickUpdateSet.getValue().remove(pos)) {
                return true
            }
        }
        return false
    }

    init {
        queuedUpdates = Maps.newHashMap() // Change to ConcurrentHashMap if this needs to be concurrent
        lastTick = currentTick
        this.level = level
    }
}