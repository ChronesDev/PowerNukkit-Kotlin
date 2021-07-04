package cn.nukkit.event.level

import cn.nukkit.api.Since

/**
 * @author KCodeYT (Nukkit Project)
 */
@Since("1.4.0.0-PN")
class StructureGrowEvent @Since("1.4.0.0-PN") constructor(block: Block, blocks: List<Block>) : LevelEvent(Objects.requireNonNull(block.getLevel())), Cancellable {
    private val block: Block
    private val blocks: List<Block>
    @Since("1.4.0.0-PN")
    fun getBlock(): Block {
        return block
    }

    @get:Since("1.4.0.0-PN")
    @set:Since("1.4.0.0-PN")
    var blockList: List<Any?>?
        get() = blocks
        set(blocks) {
            this.blocks.clear()
            if (blocks != null) this.blocks.addAll(blocks)
        }

    companion object {
        @get:Since("1.4.0.0-PN")
        val handlers: HandlerList = HandlerList()
    }

    init {
        this.block = block
        this.blocks = blocks
    }
}