package cn.nukkit.permission

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PermissibleBase(opable: ServerOperator?) : Permissible {
    var opable: ServerOperator? = null
    private var parent: Permissible? = null
    private val attachments: Set<PermissionAttachment> = HashSet()
    private val permissions: Map<String?, PermissionAttachmentInfo> = HashMap()

    @get:Override
    @set:Override
    override var isOp: Boolean
        get() = opable != null && opable.isOp()
        set(value) {
            if (opable == null) {
                throw ServerException("Cannot change op value as no ServerOperator is set")
            } else {
                opable.setOp(value)
            }
        }

    @Override
    override fun isPermissionSet(name: String?): Boolean {
        return permissions.containsKey(name)
    }

    @Override
    fun isPermissionSet(permission: Permission): Boolean {
        return this.isPermissionSet(permission.getName())
    }

    @Override
    override fun hasPermission(name: String?): Boolean {
        if (this.isPermissionSet(name)) {
            return permissions[name].getValue()
        }
        val perm: Permission = Server.getInstance().getPluginManager().getPermission(name)
        return if (perm != null) {
            val permission: String = perm.getDefault()
            Permission.DEFAULT_TRUE.equals(permission) || isOp && Permission.DEFAULT_OP.equals(permission) || !isOp && Permission.DEFAULT_NOT_OP.equals(permission)
        } else {
            Permission.DEFAULT_TRUE.equals(Permission.DEFAULT_PERMISSION) || isOp && Permission.DEFAULT_OP.equals(Permission.DEFAULT_PERMISSION) || !isOp && Permission.DEFAULT_NOT_OP.equals(Permission.DEFAULT_PERMISSION)
        }
    }

    @Override
    fun hasPermission(permission: Permission): Boolean {
        return this.hasPermission(permission.getName())
    }

    @Override
    override fun addAttachment(plugin: Plugin): PermissionAttachment {
        return this.addAttachment(plugin, null, null)
    }

    @Override
    override fun addAttachment(plugin: Plugin, name: String?): PermissionAttachment {
        return this.addAttachment(plugin, name, null)
    }

    @Override
    override fun addAttachment(plugin: Plugin, name: String?, value: Boolean?): PermissionAttachment {
        if (!plugin.isEnabled()) {
            throw PluginException("Plugin " + plugin.getDescription().getName().toString() + " is disabled")
        }
        val result = PermissionAttachment(plugin, if (parent != null) parent else this)
        attachments.add(result)
        if (name != null && value != null) {
            result.setPermission(name, value)
        }
        recalculatePermissions()
        return result
    }

    @Override
    fun removeAttachment(attachment: PermissionAttachment) {
        if (attachments.contains(attachment)) {
            attachments.remove(attachment)
            val ex: PermissionRemovedExecutor = attachment.getRemovalCallback()
            if (ex != null) {
                ex.attachmentRemoved(attachment)
            }
            recalculatePermissions()
        }
    }

    @Override
    override fun recalculatePermissions() {
        Timings.permissibleCalculationTimer.startTiming()
        clearPermissions()
        val defaults: Map<String, Permission> = Server.getInstance().getPluginManager().getDefaultPermissions(isOp)
        Server.getInstance().getPluginManager().subscribeToDefaultPerms(isOp, if (parent != null) parent else this)
        for (perm in defaults.values()) {
            val name: String = perm.getName()
            permissions.put(name, PermissionAttachmentInfo(if (parent != null) parent else this, name, null, true))
            Server.getInstance().getPluginManager().subscribeToPermission(name, if (parent != null) parent else this)
            calculateChildPermissions(perm.getChildren(), false, null)
        }
        for (attachment in attachments) {
            calculateChildPermissions(attachment.getPermissions(), false, attachment)
        }
        Timings.permissibleCalculationTimer.stopTiming()
    }

    fun clearPermissions() {
        for (name in permissions.keySet()) {
            Server.getInstance().getPluginManager().unsubscribeFromPermission(name, if (parent != null) parent else this)
        }
        Server.getInstance().getPluginManager().unsubscribeFromDefaultPerms(false, if (parent != null) parent else this)
        Server.getInstance().getPluginManager().unsubscribeFromDefaultPerms(true, if (parent != null) parent else this)
        permissions.clear()
    }

    private fun calculateChildPermissions(children: Map<String?, Boolean>, invert: Boolean, attachment: PermissionAttachment?) {
        for (entry in children.entrySet()) {
            val name: String = entry.getKey()
            val perm: Permission = Server.getInstance().getPluginManager().getPermission(name)
            val v: Boolean = entry.getValue()
            val value = v xor invert
            permissions.put(name, PermissionAttachmentInfo(if (parent != null) parent else this, name, attachment, value))
            Server.getInstance().getPluginManager().subscribeToPermission(name, if (parent != null) parent else this)
            if (perm != null) {
                calculateChildPermissions(perm.getChildren(), !value, attachment)
            }
        }
    }

    @get:Override
    override val effectivePermissions: Map<String?, cn.nukkit.permission.PermissionAttachmentInfo?>?
        get() = permissions

    init {
        this.opable = opable
        if (opable is Permissible) {
            parent = opable
        }
    }
}