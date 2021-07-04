package cn.nukkit.plugin

import cn.nukkit.Server

/**
 * @author MagicDroidX
 */
@Log4j2
class PluginManager(server: Server, commandMap: SimpleCommandMap) {
    private val server: Server
    private val commandMap: SimpleCommandMap
    protected val plugins: Map<String?, Plugin> = LinkedHashMap()
    protected val permissions: Map<String, Permission> = HashMap()
    protected val defaultPerms: Map<String, Permission> = HashMap()
    protected val defaultPermsOp: Map<String, Permission> = HashMap()
    protected val permSubs: Map<String, Set<Permissible>> = HashMap()
    protected val defSubs: Set<Permissible> = Collections.newSetFromMap(WeakHashMap())
    protected val defSubsOp: Set<Permissible> = Collections.newSetFromMap(WeakHashMap())
    protected val fileAssociations: Map<String, PluginLoader> = HashMap()
    fun getPlugin(name: String?): Plugin? {
        return if (plugins.containsKey(name)) {
            plugins[name]
        } else null
    }

    fun registerInterface(loaderClass: Class<out PluginLoader?>?): Boolean {
        return if (loaderClass != null) {
            try {
                val constructor: Constructor = loaderClass.getDeclaredConstructor(Server::class.java)
                constructor.setAccessible(true)
                fileAssociations.put(loaderClass.getName(), constructor.newInstance(server) as PluginLoader)
                true
            } catch (e: Exception) {
                false
            }
        } else false
    }

    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    fun loadPowerNukkitPlugins() {
        val pluginLoader: PluginLoader? = fileAssociations[JavaPluginLoader::class.java.getName()]
        val plugin: PowerNukkitPlugin = PowerNukkitPlugin.getInstance()
        val info: Map<String?, Object> = HashMap()
        info.put("name", "PowerNukkit")
        info.put("version", server.getNukkitVersion())
        info.put("website", "https://github.com/PowerNukkit/PowerNukkit")
        info.put("main", PowerNukkitPlugin::class.java.getName())
        var file: File?
        try {
            file = File(Server::class.java.getProtectionDomain().getCodeSource().getLocation().toURI())
        } catch (e: Exception) {
            file = File(".")
        }
        val description = PluginDescription(info)
        plugin.init(pluginLoader, server, description, File("PowerNukkit"), file)
        plugins.put(description.getName(), plugin)
        enablePlugin(plugin)
    }

    fun getPlugins(): Map<String?, Plugin> {
        return plugins
    }

    fun loadPlugin(path: String?): Plugin {
        return this.loadPlugin(path, null)
    }

    fun loadPlugin(file: File?): Plugin {
        return this.loadPlugin(file, null)
    }

    fun loadPlugin(path: String?, loaders: Map<String?, PluginLoader?>?): Plugin {
        return this.loadPlugin(File(path), loaders)
    }

    fun loadPlugin(file: File, loaders: Map<String?, PluginLoader?>?): Plugin? {
        for (loader in (loaders ?: fileAssociations).values()) {
            for (pattern in loader.getPluginFilters()) {
                if (pattern.matcher(file.getName()).matches()) {
                    val description: PluginDescription = loader.getPluginDescription(file)
                    if (description != null) {
                        try {
                            val plugin: Plugin = loader.loadPlugin(file)
                            if (plugin != null) {
                                plugins.put(plugin.getDescription().getName(), plugin)
                                val pluginCommands: List<PluginCommand> = parseYamlCommands(plugin)
                                if (!pluginCommands.isEmpty()) {
                                    commandMap.registerAll(plugin.getDescription().getName(), pluginCommands)
                                }
                                return plugin
                            }
                        } catch (e: Exception) {
                            log.fatal("Could not load plugin", e)
                            return null
                        }
                    }
                }
            }
        }
        return null
    }

    fun loadPlugins(dictionary: String?): Map<String, Plugin> {
        return this.loadPlugins(File(dictionary))
    }

    fun loadPlugins(dictionary: File?): Map<String, Plugin> {
        return this.loadPlugins(dictionary, null)
    }

    fun loadPlugins(dictionary: String?, newLoaders: List<String?>?): Map<String, Plugin> {
        return this.loadPlugins(File(dictionary), newLoaders)
    }

    fun loadPlugins(dictionary: File, newLoaders: List<String>?): Map<String, Plugin> {
        return this.loadPlugins(dictionary, newLoaders, false)
    }

    fun loadPlugins(dictionary: File, newLoaders: List<String>?, includeDir: Boolean): Map<String, Plugin> {
        return if (dictionary.isDirectory()) {
            val plugins: Map<String?, File> = LinkedHashMap()
            val loadedPlugins: Map<String, Plugin> = LinkedHashMap()
            val dependencies: Map<String, List<String>> = LinkedHashMap()
            val softDependencies: Map<String, List<String>> = LinkedHashMap()
            var loaders: Map<String, PluginLoader?> = LinkedHashMap()
            if (newLoaders != null) {
                for (key in newLoaders) {
                    if (fileAssociations.containsKey(key)) {
                        loaders.put(key, fileAssociations[key])
                    }
                }
            } else {
                loaders = fileAssociations
            }
            for (loader in loaders.values()) {
                for (file in dictionary.listFiles { dir, name ->
                    for (pattern in loader.getPluginFilters()) {
                        if (pattern.matcher(name).matches()) {
                            return@listFiles true
                        }
                    }
                    false
                }) {
                    if (file.isDirectory() && !includeDir) {
                        continue
                    }
                    try {
                        val description: PluginDescription = loader.getPluginDescription(file)
                        if (description != null) {
                            val name: String = description.getName()
                            if (plugins.containsKey(name) || getPlugin(name) != null) {
                                log.error(server.getLanguage().translateString("nukkit.plugin.duplicateError", name))
                                continue
                            }
                            var compatible = false
                            for (version in description.getCompatibleAPIs()) {
                                try {
                                    //Check the format: majorVersion.minorVersion.patch
                                    if (!Pattern.matches("^[0-9]+\\.[0-9]+\\.[0-9]+$", version)) {
                                        throw IllegalArgumentException("The getCompatibleAPI version don't match the format majorVersion.minorVersion.patch")
                                    }
                                } catch (e: NullPointerException) {
                                    log.error(server.getLanguage().translateString("nukkit.plugin.loadError", arrayOf<String?>(name, "Wrong API format")), e)
                                    continue
                                } catch (e: IllegalArgumentException) {
                                    log.error(server.getLanguage().translateString("nukkit.plugin.loadError", arrayOf<String?>(name, "Wrong API format")), e)
                                    continue
                                }
                                val versionArray: Array<String> = version.split("\\.")
                                val apiVersion: Array<String> = server.getApiVersion().split("\\.")

                                //Completely different API version
                                if (!Objects.equals(Integer.valueOf(versionArray[0]), Integer.valueOf(apiVersion[0]))) {
                                    continue
                                }

                                //If the plugin requires new API features, being backwards compatible
                                if (Integer.parseInt(versionArray[1]) > Integer.parseInt(apiVersion[1])) {
                                    continue
                                }
                                compatible = true
                                break
                            }
                            if (!compatible) {
                                log.error(server.getLanguage().translateString("nukkit.plugin.loadError", arrayOf<String?>(name, "%nukkit.plugin.incompatibleAPI")))
                            }
                            plugins.put(name, file)
                            softDependencies.put(name, description.getSoftDepend())
                            dependencies.put(name, description.getDepend())
                            for (before in description.getLoadBefore()) {
                                if (softDependencies.containsKey(before)) {
                                    softDependencies[before].add(name)
                                } else {
                                    val list: List<String> = ArrayList()
                                    list.add(name)
                                    softDependencies.put(before, list)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        log.error(server.getLanguage().translateString("nukkit.plugin" +
                                ".fileError", file.getName(), dictionary.toString(), Utils
                                .getExceptionMessage(e)), e)
                    }
                }
            }
            while (!plugins.isEmpty()) {
                var missingDependency = true
                for (name in ArrayList(plugins.keySet())) {
                    val file: File? = plugins[name]
                    if (dependencies.containsKey(name)) {
                        for (dependency in ArrayList(dependencies[name])) {
                            if (loadedPlugins.containsKey(dependency) || getPlugin(dependency) != null) {
                                dependencies[name].remove(dependency)
                            } else if (!plugins.containsKey(dependency)) {
                                log.fatal(server.getLanguage().translateString("nukkit" +
                                        ".plugin.loadError", name, "%nukkit.plugin.unknownDependency", dependency))
                                break
                            }
                        }
                        if (dependencies[name]!!.isEmpty()) {
                            dependencies.remove(name)
                        }
                    }
                    if (softDependencies.containsKey(name)) {
                        softDependencies[name].removeIf { dependency -> loadedPlugins.containsKey(dependency) || getPlugin(dependency) != null }
                        if (softDependencies[name]!!.isEmpty()) {
                            softDependencies.remove(name)
                        }
                    }
                    if (!dependencies.containsKey(name) && !softDependencies.containsKey(name)) {
                        plugins.remove(name)
                        missingDependency = false
                        val plugin: Plugin = this.loadPlugin(file, loaders)
                        if (plugin != null) {
                            loadedPlugins.put(name, plugin)
                        } else {
                            log.fatal(server.getLanguage().translateString("nukkit.plugin.genericLoadError", name))
                        }
                    }
                }
                if (missingDependency) {
                    for (name in ArrayList(plugins.keySet())) {
                        val file: File? = plugins[name]
                        if (!dependencies.containsKey(name)) {
                            softDependencies.remove(name)
                            plugins.remove(name)
                            missingDependency = false
                            val plugin: Plugin = this.loadPlugin(file, loaders)
                            if (plugin != null) {
                                loadedPlugins.put(name, plugin)
                            } else {
                                log.fatal(server.getLanguage().translateString("nukkit.plugin.genericLoadError", name))
                            }
                        }
                    }
                    if (missingDependency) {
                        for (name in plugins.keySet()) {
                            log.fatal(server.getLanguage().translateString("nukkit.plugin.loadError", arrayOf(name, "%nukkit.plugin.circularDependency")))
                        }
                        plugins.clear()
                    }
                }
            }
            loadedPlugins
        } else {
            HashMap()
        }
    }

    fun getPermission(name: String): Permission? {
        return if (permissions.containsKey(name)) {
            permissions[name]
        } else null
    }

    fun addPermission(permission: Permission): Boolean {
        if (!permissions.containsKey(permission.getName())) {
            permissions.put(permission.getName(), permission)
            calculatePermissionDefault(permission)
            return true
        }
        return false
    }

    fun removePermission(name: String?) {
        permissions.remove(name)
    }

    fun removePermission(permission: Permission) {
        this.removePermission(permission.getName())
    }

    fun getDefaultPermissions(op: Boolean): Map<String, Permission> {
        return if (op) {
            defaultPermsOp
        } else {
            defaultPerms
        }
    }

    fun recalculatePermissionDefaults(permission: Permission) {
        if (permissions.containsKey(permission.getName())) {
            defaultPermsOp.remove(permission.getName())
            defaultPerms.remove(permission.getName())
            calculatePermissionDefault(permission)
        }
    }

    private fun calculatePermissionDefault(permission: Permission) {
        Timings.permissionDefaultTimer.startTiming()
        if (permission.getDefault().equals(Permission.DEFAULT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            defaultPermsOp.put(permission.getName(), permission)
            dirtyPermissibles(true)
        }
        if (permission.getDefault().equals(Permission.DEFAULT_NOT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            defaultPerms.put(permission.getName(), permission)
            dirtyPermissibles(false)
        }
        Timings.permissionDefaultTimer.startTiming()
    }

    private fun dirtyPermissibles(op: Boolean) {
        for (p in getDefaultPermSubscriptions(op)) {
            p.recalculatePermissions()
        }
    }

    fun subscribeToPermission(permission: String, permissible: Permissible?) {
        if (!permSubs.containsKey(permission)) {
            permSubs.put(permission, Collections.newSetFromMap(WeakHashMap()))
        }
        permSubs[permission].add(permissible)
    }

    fun unsubscribeFromPermission(permission: String, permissible: Permissible?) {
        if (permSubs.containsKey(permission)) {
            permSubs[permission].remove(permissible)
            if (permSubs[permission]!!.size() === 0) {
                permSubs.remove(permission)
            }
        }
    }

    fun getPermissionSubscriptions(permission: String): Set<Permissible> {
        return if (permSubs.containsKey(permission)) {
            HashSet(permSubs[permission])
        } else HashSet()
    }

    fun subscribeToDefaultPerms(op: Boolean, permissible: Permissible?) {
        if (op) {
            defSubsOp.add(permissible)
        } else {
            defSubs.add(permissible)
        }
    }

    fun unsubscribeFromDefaultPerms(op: Boolean, permissible: Permissible?) {
        if (op) {
            defSubsOp.remove(permissible)
        } else {
            defSubs.remove(permissible)
        }
    }

    fun getDefaultPermSubscriptions(op: Boolean): Set<Permissible> {
        return if (op) {
            HashSet(defSubsOp)
        } else {
            HashSet(defSubs)
        }
    }

    fun getPermissions(): Map<String, Permission> {
        return permissions
    }

    fun isPluginEnabled(plugin: Plugin?): Boolean {
        return if (plugin != null && plugins.containsKey(plugin.getDescription().getName())) {
            plugin.isEnabled()
        } else {
            false
        }
    }

    fun enablePlugin(plugin: Plugin) {
        if (!plugin.isEnabled()) {
            try {
                for (permission in plugin.getDescription().getPermissions()) {
                    addPermission(permission)
                }
                plugin.getPluginLoader().enablePlugin(plugin)
            } catch (e: Throwable) {
                log.fatal("An error occurred while enabling the plugin {}, {}, {}",
                        plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getMain(), e)
                disablePlugin(plugin)
            }
        }
    }

    protected fun parseYamlCommands(plugin: Plugin): List<PluginCommand> {
        val pluginCmds: List<PluginCommand> = ArrayList()
        for (entry in plugin.getDescription().getCommands().entrySet()) {
            val key = entry.getKey() as String
            val data: Object = entry.getValue()
            if (key.contains(":")) {
                log.fatal(server.getLanguage().translateString("nukkit.plugin.commandError", arrayOf(key, plugin.getDescription().getFullName())))
                continue
            }
            if (data is Map) {
                val newCmd = PluginCommand(key, plugin)
                if ((data as Map).containsKey("description")) {
                    newCmd.setDescription((data as Map).get("description") as String?)
                }
                if ((data as Map).containsKey("usage")) {
                    newCmd.setUsage((data as Map).get("usage") as String?)
                }
                if ((data as Map).containsKey("aliases")) {
                    val aliases: Object = (data as Map).get("aliases")
                    if (aliases is List) {
                        val aliasList: List<String> = ArrayList()
                        for (alias in aliases) {
                            if (alias.contains(":")) {
                                log.fatal(server.getLanguage().translateString("nukkit.plugin.aliasError", arrayOf(alias, plugin.getDescription().getFullName())))
                                continue
                            }
                            aliasList.add(alias)
                        }
                        newCmd.setAliases(aliasList.toArray(EmptyArrays.EMPTY_STRINGS))
                    }
                }
                if ((data as Map).containsKey("permission")) {
                    newCmd.setPermission((data as Map).get("permission") as String?)
                }
                if ((data as Map).containsKey("permission-message")) {
                    newCmd.setPermissionMessage((data as Map).get("permission-message") as String?)
                }
                pluginCmds.add(newCmd)
            }
        }
        return pluginCmds
    }

    @PowerNukkitDifference(info = "Makes sure the PowerNukkitPlugin is never disabled", since = "1.3.0.0-PN")
    fun disablePlugins() {
        val plugins: ListIterator<Plugin> = ArrayList(getPlugins().values()).listIterator(getPlugins().size())
        while (plugins.hasPrevious()) {
            val previous: Plugin = plugins.previous()
            if (previous !== PowerNukkitPlugin.getInstance()) {
                disablePlugin(previous)
            }
        }
    }

    fun disablePlugin(plugin: Plugin) {
        if (PowerNukkitPlugin.getInstance() === plugin) {
            throw UnsupportedOperationException("The PowerNukkit plugin can't be disabled.")
        }
        if (plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin)
            } catch (e: Exception) {
                log.fatal("An error occurred while disabling the plugin {}, {}, {}",
                        plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getMain(), e)
            }
            server.getScheduler().cancelTask(plugin)
            HandlerList.unregisterAll(plugin)
            for (permission in plugin.getDescription().getPermissions()) {
                this.removePermission(permission)
            }
        }
    }

    fun clearPlugins() {
        disablePlugins()
        plugins.clear()
        fileAssociations.clear()
        permissions.clear()
        defaultPerms.clear()
        defaultPermsOp.clear()
    }

    fun callEvent(event: Event) {
        try {
            for (registration in getEventListeners(event.getClass()).getRegisteredListeners()) {
                if (!registration.getPlugin().isEnabled()) {
                    continue
                }
                try {
                    registration.callEvent(event)
                } catch (e: Exception) {
                    log.error(server.getLanguage().translateString("nukkit.plugin.eventError", event.getEventName(), registration.getPlugin().getDescription().getFullName(), e.getMessage(), registration.getListener().getClass().getName()), e)
                }
            }
        } catch (e: IllegalAccessException) {
            log.error("An error has occurred while calling the event {}", event, e)
        }
    }

    fun registerEvents(listener: Listener, plugin: Plugin) {
        if (!plugin.isEnabled()) {
            throw PluginException("Plugin attempted to register " + listener.getClass().getName().toString() + " while not enabled")
        }
        val ret: Map<Class<out Event?>, Set<RegisteredListener>> = HashMap()
        val methods: Set<Method>
        try {
            val publicMethods: Array<Method> = listener.getClass().getMethods()
            val privateMethods: Array<Method> = listener.getClass().getDeclaredMethods()
            methods = HashSet(publicMethods.size + privateMethods.size, 1.0f)
            Collections.addAll(methods, publicMethods)
            Collections.addAll(methods, privateMethods)
        } catch (e: NoClassDefFoundError) {
            plugin.getLogger().error("Plugin " + plugin.getDescription().getFullName().toString() + " has failed to register events for " + listener.getClass().toString() + " because " + e.getMessage().toString() + " does not exist.")
            return
        }
        for (method in methods) {
            val eh: EventHandler = method.getAnnotation(EventHandler::class.java) ?: continue
            if (method.isBridge() || method.isSynthetic()) {
                continue
            }
            var checkClass: Class<*>
            if (method.getParameterTypes().length !== 1 || !Event::class.java.isAssignableFrom(method.getParameterTypes().get(0).also { checkClass = it })) {
                plugin.getLogger().error(plugin.getDescription().getFullName().toString() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass())
                continue
            }
            val eventClass: Class<out Event?> = checkClass.asSubclass(Event::class.java)
            method.setAccessible(true)
            var clazz: Class<*> = eventClass
            while (Event::class.java.isAssignableFrom(clazz)) {

                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated::class.java) != null) {
                    if (Boolean.parseBoolean(String.valueOf(server.getConfig("settings.deprecated-verbpse", true)))) {
                        log.warn(server.getLanguage().translateString("nukkit.plugin.deprecatedEvent", plugin.getName(), clazz.getName(), listener.getClass().getName().toString() + "." + method.getName() + "()"))
                    }
                    break
                }
                clazz = clazz.getSuperclass()
            }
            this.registerEvent(eventClass, listener, eh.priority(), MethodEventExecutor(method), plugin, eh.ignoreCancelled())
        }
    }

    @Throws(PluginException::class)
    fun registerEvent(event: Class<out Event?>, listener: Listener, priority: EventPriority?, executor: EventExecutor, plugin: Plugin) {
        this.registerEvent(event, listener, priority, executor, plugin, false)
    }

    @Throws(PluginException::class)
    fun registerEvent(event: Class<out Event?>, listener: Listener, priority: EventPriority?, executor: EventExecutor, plugin: Plugin, ignoreCancelled: Boolean) {
        if (!plugin.isEnabled()) {
            throw PluginException("Plugin attempted to register $event while not enabled")
        }
        try {
            val timing: Timing = Timings.getPluginEventTiming(event, listener, executor, plugin)
            getEventListeners(event).register(RegisteredListener(listener, executor, priority, plugin, ignoreCancelled, timing))
        } catch (e: IllegalAccessException) {
            log.error("An error occurred while registering the event listener event:{}, listener:{} for plugin:{} version:{}",
                    event, listener, plugin.getDescription().getName(), plugin.getDescription().getVersion(), e)
        }
    }

    @Throws(IllegalAccessException::class)
    private fun getEventListeners(type: Class<out Event?>): HandlerList {
        return try {
            val method: Method = getRegistrationClass(type).getDeclaredMethod("getHandlers")
            method.setAccessible(true)
            method.invoke(null) as HandlerList
        } catch (e: NullPointerException) {
            throw IllegalArgumentException("getHandlers method in " + type.getName().toString() + " was not static!", e)
        } catch (e: Exception) {
            val illegalAccessException = IllegalAccessException(Utils.getExceptionMessage(e))
            illegalAccessException.addSuppressed(e)
            throw illegalAccessException
        }
    }

    @Throws(IllegalAccessException::class)
    private fun getRegistrationClass(clazz: Class<out Event?>): Class<out Event?> {
        return try {
            clazz.getDeclaredMethod("getHandlers")
            clazz
        } catch (e: NoSuchMethodException) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Event::class.java)
                    && Event::class.java.isAssignableFrom(clazz.getSuperclass())) {
                getRegistrationClass(clazz.getSuperclass().asSubclass(Event::class.java))
            } else {
                throw IllegalAccessException("Unable to find handler list for event " + clazz.getName().toString() + ". Static getHandlers method required!")
            }
        }
    }

    init {
        this.server = server
        this.commandMap = commandMap
    }
}