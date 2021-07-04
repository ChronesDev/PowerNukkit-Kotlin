package cn.nukkit.event.player

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Volatile
import kotlin.jvm.Synchronized

/**
 * @author xtypr
 * @since 2015/12/23
 */
abstract class PlayerMessageEvent : PlayerEvent() {
    var message: String? = null
}