package cn.nukkit.permission

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit Project)
 */
class Permission(val name: String?, description: String?, defualtValue: String?, children: Map<String?, Boolean>) {
    var description: String
    val children: Map<String?, Boolean> = HashMap()
    private var defaultValue: String

    constructor(name: String?) : this(name, null, null, HashMap()) {}
    constructor(name: String?, description: String?) : this(name, description, null, HashMap()) {}
    constructor(name: String?, description: String?, defualtValue: String?) : this(name, description, defualtValue, HashMap()) {}

    var default: String
        get() = defaultValue
        set(value) {
            if (!value.equals(defaultValue)) {
                defaultValue = value
                recalculatePermissibles()
            }
        }
    val permissibles: Set<cn.nukkit.permission.Permissible>
        get() = Server.getInstance().getPluginManager().getPermissionSubscriptions(name)

    fun recalculatePermissibles() {
        val perms: Set<Permissible> = permissibles
        Server.getInstance().getPluginManager().recalculatePermissionDefaults(this)
        for (p in perms) {
            p.recalculatePermissions()
        }
    }

    fun addParent(permission: Permission, value: Boolean) {
        children.put(name, value)
        permission.recalculatePermissibles()
    }

    fun addParent(name: String?, value: Boolean): Permission {
        var perm: Permission = Server.getInstance().getPluginManager().getPermission(name)
        if (perm == null) {
            perm = Permission(name)
            Server.getInstance().getPluginManager().addPermission(perm)
        }
        this.addParent(perm, value)
        return perm
    }

    companion object {
        const val DEFAULT_OP = "op"
        const val DEFAULT_NOT_OP = "notop"
        const val DEFAULT_TRUE = "true"
        const val DEFAULT_FALSE = "false"
        const val DEFAULT_PERMISSION = DEFAULT_OP
        fun getByName(value: String): String {
            return when (value.toLowerCase()) {
                "op", "isop", "operator", "isoperator", "admin", "isadmin" -> DEFAULT_OP
                "!op", "notop", "!operator", "notoperator", "!admin", "notadmin" -> DEFAULT_NOT_OP
                "true" -> DEFAULT_TRUE
                else -> DEFAULT_FALSE
            }
        }

        fun loadPermissions(data: Map<String?, Object?>?): List<Permission> {
            return loadPermissions(data, DEFAULT_OP)
        }

        fun loadPermissions(data: Map<String?, Object?>?, defaultValue: String?): List<Permission> {
            val result: List<Permission> = ArrayList()
            if (data != null) {
                for (e in data.entrySet()) {
                    val key = e.getKey() as String
                    result.add(loadPermission(key, e.getValue(), defaultValue, result))
                }
            }
            return result
        }

        fun loadPermission(name: String?, data: Map<String, Object?>): Permission {
            return loadPermission(name, data, DEFAULT_OP, ArrayList())
        }

        fun loadPermission(name: String?, data: Map<String, Object?>, defaultValue: String?): Permission {
            return loadPermission(name, data, defaultValue, ArrayList())
        }

        fun loadPermission(name: String?, data: Map<String, Object?>, defaultValue: String?, output: List<Permission?>): Permission {
            var defaultValue = defaultValue
            var desc: String? = null
            val children: Map<String?, Boolean> = HashMap()
            if (data.containsKey("default")) {
                val value = getByName(String.valueOf(data["default"]))
                defaultValue = value ?: throw IllegalStateException("'default' key contained unknown value")
            }
            if (data.containsKey("children")) {
                if (data["children"] is Map) {
                    for (entry in (data["children"] as Map<String?, Object?>?).entrySet()) {
                        val k = entry.getKey() as String
                        val v: Object = entry.getValue()
                        if (v is Map) {
                            val permission = loadPermission(k, v as Map<String, Object?>, defaultValue, output)
                            if (permission != null) {
                                output.add(permission)
                            }
                        }
                        children.put(k, true)
                    }
                } else {
                    throw IllegalStateException("'children' key is of wrong type")
                }
            }
            if (data.containsKey("description")) {
                desc = data["description"]
            }
            return Permission(name, desc, defaultValue, children)
        }
    }

    init {
        this.description = description ?: ""
        defaultValue = defualtValue ?: DEFAULT_PERMISSION
        this.children = children
        recalculatePermissibles()
    }
}