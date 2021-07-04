package cn.nukkit.permission

import cn.nukkit.plugin.Plugin

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PermissionAttachment(plugin: Plugin, permissible: Permissible?) {
    private var removed: PermissionRemovedExecutor? = null
    private val permissions: Map<String?, Boolean> = HashMap()
    private val permissible: Permissible?
    private val plugin: Plugin
    fun getPlugin(): Plugin {
        return plugin
    }

    var removalCallback: cn.nukkit.permission.PermissionRemovedExecutor?
        get() = removed
        set(executor) {
            removed = executor
        }

    fun getPermissions(): Map<String?, Boolean> {
        return permissions
    }

    fun clearPermissions() {
        permissions.clear()
        permissible!!.recalculatePermissions()
    }

    fun setPermissions(permissions: Map<String?, Boolean?>) {
        for (entry in permissions.entrySet()) {
            val key: String = entry.getKey()
            val value: Boolean = entry.getValue()
            this.permissions.put(key, value)
        }
        permissible!!.recalculatePermissions()
    }

    fun unsetPermissions(permissions: List<String?>) {
        for (node in permissions) {
            this.permissions.remove(node)
        }
        permissible!!.recalculatePermissions()
    }

    fun setPermission(permission: Permission, value: Boolean) {
        this.setPermission(permission.getName(), value)
    }

    fun setPermission(name: String?, value: Boolean) {
        if (permissions.containsKey(name)) {
            if (permissions[name]!!.equals(value)) {
                return
            }
            permissions.remove(name)
        }
        permissions.put(name, value)
        permissible!!.recalculatePermissions()
    }

    fun unsetPermission(permission: Permission, value: Boolean) {
        this.unsetPermission(permission.getName(), value)
    }

    fun unsetPermission(name: String?, value: Boolean) {
        if (permissions.containsKey(name)) {
            permissions.remove(name)
            permissible!!.recalculatePermissions()
        }
    }

    fun remove() {
        permissible!!.removeAttachment(this)
    }

    init {
        if (!plugin.isEnabled()) {
            throw PluginException("Plugin " + plugin.getDescription().getName().toString() + " is disabled")
        }
        this.permissible = permissible
        this.plugin = plugin
    }
}