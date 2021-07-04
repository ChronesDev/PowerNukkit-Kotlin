package cn.nukkit.utils

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockUpdateEntry : Comparable<BlockUpdateEntry?> {
    var priority = 0
    var delay: Long = 0
    val pos: Vector3
    val block: Block
    val id: Long

    constructor(pos: Vector3, block: Block) {
        this.pos = pos
        this.block = block
        id = entryID++
    }

    constructor(pos: Vector3, block: Block, delay: Long, priority: Int) {
        id = entryID++
        this.pos = pos
        this.priority = priority
        this.delay = delay
        this.block = block
    }

    @Override
    operator fun compareTo(entry: BlockUpdateEntry): Int {
        return if (delay < entry.delay) -1 else if (delay > entry.delay) 1 else if (priority != entry.priority) priority - entry.priority else Long.compare(id, entry.id)
    }

    @Override
    override fun equals(`object`: Object): Boolean {
        return if (`object` !is BlockUpdateEntry) {
            if (`object` is Block) {
                return (`object` as Block).layer === block.layer && pos.equals(`object`)
            }
            if (`object` is Vector3) {
                block.layer === 0 && pos.equals(`object`)
            } else false
        } else {
            val entry = `object` as BlockUpdateEntry
            block.layer === entry.block.layer && pos.equals(entry.pos) && Block.equals(block, entry.block, false)
        }
    }

    @Override
    override fun hashCode(): Int {
        return pos.hashCode()
    }

    companion object {
        private var entryID: Long = 0
    }
}