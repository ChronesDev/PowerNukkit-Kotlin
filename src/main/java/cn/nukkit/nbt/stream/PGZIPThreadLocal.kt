package cn.nukkit.nbt.stream

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.level.Position

class PGZIPThreadLocal(parent: PGZIPOutputStream) : ThreadLocal<PGZIPState?>() {
    private val parent: PGZIPOutputStream
    @Override
    protected fun initialValue(): PGZIPState {
        return PGZIPState(parent)
    }

    init {
        this.parent = parent
    }
}