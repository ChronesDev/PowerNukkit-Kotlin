package cn.nukkit.event

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Volatile
import kotlin.jvm.Synchronized

/**
 * @author Nukkit Team.
 */
interface Cancellable {
    var isCancelled: Boolean
    fun setCancelled()
}