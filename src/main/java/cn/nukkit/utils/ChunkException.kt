package cn.nukkit.utils

import kotlin.jvm.Synchronized
import kotlin.Throws
import kotlin.jvm.Volatile
import kotlin.jvm.JvmOverloads

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ChunkException : RuntimeException {
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
    constructor(cause: Throwable?) : super(cause) {}
}