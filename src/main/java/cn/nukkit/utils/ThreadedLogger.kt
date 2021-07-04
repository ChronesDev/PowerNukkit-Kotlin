package cn.nukkit.utils

import kotlin.jvm.Synchronized
import kotlin.Throws
import kotlin.jvm.Volatile
import kotlin.jvm.JvmOverloads

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class ThreadedLogger : Thread(), Logger