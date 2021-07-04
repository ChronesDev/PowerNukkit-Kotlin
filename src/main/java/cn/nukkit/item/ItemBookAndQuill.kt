package cn.nukkit.item

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

class ItemBookAndQuill @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemBookWritable(Item.BOOK_AND_QUILL, 0, count, "Book & Quill") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}