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
class PermissionAttachmentInfo(permissible: Permissible?, permission: String?, attachment: PermissionAttachment?, value: Boolean) {
    private val permissible: Permissible?
    val permission: String
    private val attachment: PermissionAttachment?
    val value: Boolean
    fun getPermissible(): Permissible? {
        return permissible
    }

    fun getAttachment(): PermissionAttachment? {
        return attachment
    }

    init {
        if (permission == null) {
            throw IllegalStateException("Permission may not be null")
        }
        this.permissible = permissible
        this.permission = permission
        this.attachment = attachment
        this.value = value
    }
}