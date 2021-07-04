package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author CreeperFace
 */
class ItemRecordBlocks @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemRecord(RECORD_BLOCKS, meta, count) {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getSoundId(): String {
        return "record.blocks"
    }
}