package cn.nukkit.permission

import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.network.protocol.types.CommandOriginData.Origin
import CommandOriginData.Origin
import cn.nukkit.network.protocol.ItemStackRequestPacket.Request
import kotlin.jvm.Synchronized
import kotlin.jvm.JvmOverloads

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface PermissionRemovedExecutor {
    fun attachmentRemoved(attachment: PermissionAttachment?)
}