package cn.nukkit.command

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class ConsoleCommandSender : CommandSender {
    private val perm: PermissibleBase
    @Override
    fun isPermissionSet(name: String?): Boolean {
        return perm.isPermissionSet(name)
    }

    @Override
    fun isPermissionSet(permission: Permission?): Boolean {
        return perm.isPermissionSet(permission)
    }

    @Override
    fun hasPermission(name: String?): Boolean {
        return perm.hasPermission(name)
    }

    @Override
    fun hasPermission(permission: Permission?): Boolean {
        return perm.hasPermission(permission)
    }

    @Override
    fun addAttachment(plugin: Plugin?): PermissionAttachment {
        return perm.addAttachment(plugin)
    }

    @Override
    fun addAttachment(plugin: Plugin?, name: String?): PermissionAttachment {
        return perm.addAttachment(plugin, name)
    }

    @Override
    fun addAttachment(plugin: Plugin?, name: String?, value: Boolean?): PermissionAttachment {
        return perm.addAttachment(plugin, name, value)
    }

    @Override
    fun removeAttachment(attachment: PermissionAttachment?) {
        perm.removeAttachment(attachment)
    }

    @Override
    fun recalculatePermissions() {
        perm.recalculatePermissions()
    }

    @get:Override
    val effectivePermissions: Map<String, Any>
        get() = perm.getEffectivePermissions()
    override val isPlayer: Boolean
        get() = false

    @get:Override
    override val server: Server
        get() = Server.getInstance()

    @Override
    override fun sendMessage(message: String) {
        var message = message
        message = server.getLanguage().translateString(message)
        for (line in message.trim().split("\n")) {
            log.info(line)
        }
    }

    @Override
    override fun sendMessage(message: TextContainer?) {
        this.sendMessage(server.getLanguage().translate(message))
    }

    @get:Override
    override val name: String
        get() = "CONSOLE"

    @get:Override
    @set:Override
    var isOp: Boolean
        get() = true
        set(value) {}

    init {
        perm = PermissibleBase(this)
    }
}