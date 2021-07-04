package cn.nukkit.command

import cn.nukkit.Server

/**
 * @since 1.2.1.0-PN
 */
@AllArgsConstructor
class CapturingCommandSender : CommandSender {
    private val captured: StringBuilder = StringBuilder()

    @NonNull
    @Getter
    @Setter
    private override var name: String

    @Getter
    @Setter
    private var isOp = false

    @NonNull
    private val perms: Permissible

    @JvmOverloads
    constructor(@NonNull name: String = "System") {
        this.name = name
        perms = PermissibleBase(this)
    }

    constructor(@NonNull name: String, isOp: Boolean) {
        this.name = name
        this.isOp = true
        perms = PermissibleBase(this)
    }

    constructor(@NonNull name: String, isOp: Boolean, @NonNull permissibleFactory: Function<ServerOperator?, Permissible?>) {
        this.name = name
        this.isOp = true
        perms = permissibleFactory.apply(this)
    }

    fun resetCapture() {
        captured.setLength(0)
    }

    @get:Synchronized
    val rawCapture: String
        get() = captured.toString()

    @get:Synchronized
    val cleanCapture: String
        get() = TextFormat.clean(captured.toString())

    @Override
    override fun toString(): String {
        return "CapturingCommandSender{" +
                "name='" + name + '\'' +
                '}'
    }

    @Override
    @Synchronized
    override fun sendMessage(message: String?) {
        captured.append(message).append('\n')
    }

    @Override
    override fun sendMessage(message: TextContainer) {
        sendMessage(message.toString())
    }

    @get:Override
    override val server: Server
        get() = Server.getInstance()

    @get:Override
    override val isPlayer: Boolean
        get() = false

    @Override
    fun isPermissionSet(name: String?): Boolean {
        return perms.isPermissionSet(name)
    }

    @Override
    fun isPermissionSet(permission: Permission?): Boolean {
        return perms.isPermissionSet(permission)
    }

    @Override
    fun hasPermission(name: String?): Boolean {
        return perms.hasPermission(name)
    }

    @Override
    fun hasPermission(permission: Permission?): Boolean {
        return perms.hasPermission(permission)
    }

    @Override
    fun addAttachment(plugin: Plugin?): PermissionAttachment {
        return perms.addAttachment(plugin)
    }

    @Override
    fun addAttachment(plugin: Plugin?, name: String?): PermissionAttachment {
        return perms.addAttachment(plugin, name)
    }

    @Override
    fun addAttachment(plugin: Plugin?, name: String?, value: Boolean?): PermissionAttachment {
        return perms.addAttachment(plugin, name, value)
    }

    @Override
    fun removeAttachment(attachment: PermissionAttachment?) {
        perms.removeAttachment(attachment)
    }

    @Override
    fun recalculatePermissions() {
        perms.recalculatePermissions()
    }

    @get:Override
    val effectivePermissions: Map<String, Any>
        get() = perms.getEffectivePermissions()
}