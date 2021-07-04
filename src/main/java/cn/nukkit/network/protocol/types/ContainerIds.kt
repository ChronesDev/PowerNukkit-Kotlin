package cn.nukkit.network.protocol.types

import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.network.protocol.types.CommandOriginData.Origin
import CommandOriginData.Origin
import cn.nukkit.network.protocol.ItemStackRequestPacket.Request
import kotlin.jvm.Synchronized
import kotlin.jvm.JvmOverloads

/**
 * @author CreeperFace
 */
interface ContainerIds {
    companion object {
        const val NONE = -1
        const val INVENTORY = 0
        const val FIRST = 1
        const val LAST = 100
        const val OFFHAND = 119
        const val ARMOR = 120
        const val CREATIVE = 121
        const val HOTBAR = 122
        const val FIXED_INVENTORY = 123
        const val UI = 124
    }
}