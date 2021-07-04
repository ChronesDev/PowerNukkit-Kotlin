package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author CreeperFace
 */
abstract class ItemRecord(id: Int, meta: Integer?, count: Int) : Item(id, meta, count, "Music Disc") {
    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }

    abstract fun getSoundId(): String?
}