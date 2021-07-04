package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class ItemBlock(block: Block, meta: Integer?, count: Int) : Item(block.getItemId(), meta, count, block.getName()) {
    constructor(block: Block) : this(block, 0, 1) {}
    constructor(block: Block, meta: Integer?) : this(block, meta, 1) {}

    override fun setDamage(meta: Integer?) {
        val blockMeta: Int
        if (meta != null) {
            meta = meta
            blockMeta = meta
        } else {
            this.hasMeta = false
            blockMeta = 0
        }
        val blockId: Int = block.getId()
        try {
            if (block is BlockUnknown) {
                block = BlockState.of(blockId, blockMeta).getBlock()
                log.info("An invalid ItemBlock for {} was set to a valid meta {} and it is now safe again", block.getPersistenceName(), meta)
            } else {
                block.setDataStorageFromItemBlockMeta(blockMeta)
                name = block.getName()
            }
        } catch (e: InvalidBlockStateException) {
            log.warn("An ItemBlock for {} was set to have meta {}" +
                    " but this value is not valid. The item stack is now unsafe.", block.getPersistenceName(), meta, e)
            block = BlockUnknown(blockId, blockMeta)
            name = block.getName()
            return
        }
        val expected: Int = block.asItemBlock().getDamage()
        if (expected != blockMeta) {
            log.warn("""
    An invalid ItemBlock for {} was set to an valid meta {} for item blocks, it was expected to have meta {} the stack is now unsafe.
    Properties: {}
    """.trimIndent(),
                    block.getPersistenceName(), meta, expected, block.getProperties())
        }
    }

    @Override
    override fun clone(): ItemBlock? {
        val block = super.clone() as ItemBlock?
        block.block = block.clone()
        return block
    }

    override fun getBlock(): Block {
        return this.block
    }

    @Override
    override fun isLavaResistant(): Boolean {
        return block.isLavaResistant()
    }

    init {
        block = block
    }
}