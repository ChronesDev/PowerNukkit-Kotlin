package cn.nukkit.plugin.service

import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.network.protocol.types.CommandOriginData.Origin
import CommandOriginData.Origin
import cn.nukkit.network.protocol.ItemStackRequestPacket.Request
import kotlin.jvm.Synchronized
import kotlin.jvm.JvmOverloads

/**
 * @since 16-11-20
 */
enum class ServicePriority {
    LOWEST, LOWER, NORMAL, HIGHER, HIGHEST
}