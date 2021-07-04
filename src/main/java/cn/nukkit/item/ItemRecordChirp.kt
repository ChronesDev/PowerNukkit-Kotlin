package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author CreeperFace
 */
class ItemRecordChirp @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemRecord(RECORD_CHIRP, meta, count) {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getSoundId(): String {
        return "record.chirp"
    }
}