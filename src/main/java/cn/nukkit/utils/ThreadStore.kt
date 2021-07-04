package cn.nukkit.utils

import java.util.Map

/**
 * @author MagicDroidX (Nukkit Project)
 */
object ThreadStore {
    val store: Map<String, Object> = ConcurrentHashMap()
}