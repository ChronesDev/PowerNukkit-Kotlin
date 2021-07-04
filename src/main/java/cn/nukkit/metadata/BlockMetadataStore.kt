package cn.nukkit.metadata

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BlockMetadataStore(owningLevel: Level) : MetadataStore() {
    private val owningLevel: Level
    @Override
    protected fun disambiguate(block: Metadatable, metadataKey: String): String {
        if (block !is Block) {
            throw IllegalArgumentException("Argument must be a Block instance")
        }
        return (block as Block).x.toString() + ":" + (block as Block).y + ":" + (block as Block).z + ":" + metadataKey
    }

    @Override
    override fun getMetadata(block: Object, metadataKey: String?): List<MetadataValue> {
        if (block !is Block) {
            throw IllegalArgumentException("Object must be a Block")
        }
        return if ((block as Block).getLevel() === owningLevel) {
            super.getMetadata(block, metadataKey)
        } else {
            throw IllegalStateException("Block does not belong to world " + owningLevel.getName())
        }
    }

    @Override
    override fun hasMetadata(block: Object, metadataKey: String?): Boolean {
        if (block !is Block) {
            throw IllegalArgumentException("Object must be a Block")
        }
        return if ((block as Block).getLevel() === owningLevel) {
            super.hasMetadata(block, metadataKey)
        } else {
            throw IllegalStateException("Block does not belong to world " + owningLevel.getName())
        }
    }

    @Override
    override fun removeMetadata(block: Object, metadataKey: String?, owningPlugin: Plugin?) {
        if (block !is Block) {
            throw IllegalArgumentException("Object must be a Block")
        }
        if ((block as Block).getLevel() === owningLevel) {
            super.removeMetadata(block, metadataKey, owningPlugin)
        } else {
            throw IllegalStateException("Block does not belong to world " + owningLevel.getName())
        }
    }

    @Override
    override fun setMetadata(block: Object, metadataKey: String?, newMetadataValue: MetadataValue?) {
        if (block !is Block) {
            throw IllegalArgumentException("Object must be a Block")
        }
        if ((block as Block).getLevel() === owningLevel) {
            super.setMetadata(block, metadataKey, newMetadataValue)
        } else {
            throw IllegalStateException("Block does not belong to world " + owningLevel.getName())
        }
    }

    init {
        this.owningLevel = owningLevel
    }
}