package cn.nukkit.plugin

import cn.nukkit.Server

/**
 * @author Nukkit Team.
 */
@Log4j2
class JavaPluginLoader(server: Server) : PluginLoader {
    private val server: Server
    private val classes: Map<String, Class> = HashMap()
    private val classLoaders: Map<String, PluginClassLoader> = HashMap()

    @Override
    @Throws(Exception::class)
    override fun loadPlugin(file: File): Plugin? {
        val description: PluginDescription = this.getPluginDescription(file)
        if (description != null) {
            log.info(server.getLanguage().translateString("nukkit.plugin.load", description.getFullName()))
            val dataFolder = File(file.getParentFile(), description.getName())
            if (dataFolder.exists() && !dataFolder.isDirectory()) {
                throw IllegalStateException("Projected dataFolder '" + dataFolder.toString().toString() + "' for " + description.getName().toString() + " exists and is not a directory")
            }
            val className: String = description.getMain()
            val classLoader = PluginClassLoader(this, this.getClass().getClassLoader(), file)
            classLoaders.put(description.getName(), classLoader)
            val plugin: PluginBase
            try {
                val javaClass: Class = classLoader.loadClass(className)
                if (!PluginBase::class.java.isAssignableFrom(javaClass)) {
                    throw PluginException("Main class `" + description.getMain().toString() + "' does not extend PluginBase")
                }
                try {
                    val pluginClass: Class<PluginBase> = javaClass.asSubclass(PluginBase::class.java) as Class<PluginBase>
                    plugin = pluginClass.newInstance()
                    initPlugin(plugin, description, dataFolder, file)
                    return plugin
                } catch (e: ClassCastException) {
                    throw PluginException("Error whilst initializing main class `" + description.getMain().toString() + "'", e)
                } catch (e: InstantiationException) {
                    log.error("An exception happened while initializing the plgin {}, {}, {}, {}", file, className, description.getName(), description.getVersion(), e)
                } catch (e: IllegalAccessException) {
                    log.error("An exception happened while initializing the plgin {}, {}, {}, {}", file, className, description.getName(), description.getVersion(), e)
                }
            } catch (e: ClassNotFoundException) {
                throw PluginException("Couldn't load plugin " + description.getName().toString() + ": main class not found")
            }
        }
        return null
    }

    @Override
    @Throws(Exception::class)
    override fun loadPlugin(filename: String?): Plugin {
        return this.loadPlugin(File(filename))
    }

    @Override
    override fun getPluginDescription(file: File?): PluginDescription? {
        try {
            JarFile(file).use { jar ->
                var entry: JarEntry = jar.getJarEntry("nukkit.yml")
                if (entry == null) {
                    entry = jar.getJarEntry("plugin.yml")
                    if (entry == null) {
                        return null
                    }
                }
                jar.getInputStream(entry).use { stream -> return PluginDescription(Utils.readFile(stream)) }
            }
        } catch (e: IOException) {
            return null
        }
    }

    @Override
    override fun getPluginDescription(filename: String?): PluginDescription {
        return this.getPluginDescription(File(filename))
    }

    @Override
    override fun getPluginFilters(): Array<Pattern> {
        return arrayOf<Pattern>(Pattern.compile("^.+\\.jar$"))
    }

    private fun initPlugin(plugin: PluginBase, description: PluginDescription, dataFolder: File, file: File) {
        plugin.init(this, server, description, dataFolder, file)
        plugin.onLoad()
    }

    @Override
    fun enablePlugin(plugin: Plugin) {
        if (plugin is PluginBase && !plugin.isEnabled()) {
            log.info(server.getLanguage().translateString("nukkit.plugin.enable", plugin.getDescription().getFullName()))
            plugin.setEnabled(true)
            server.getPluginManager().callEvent(PluginEnableEvent(plugin))
        }
    }

    @PowerNukkitDifference(info = "Made impossible to disable special the PowerNukkitPlugin", since = "1.3.0.0-PN")
    @Override
    fun disablePlugin(plugin: Plugin) {
        if (plugin is PluginBase && plugin.isEnabled()) {
            if (plugin === PowerNukkitPlugin.getInstance()) {
                throw UnsupportedOperationException("The PowerNukkitPlugin cannot be disabled")
            }
            log.info(server.getLanguage().translateString("nukkit.plugin.disable", plugin.getDescription().getFullName()))
            server.getServiceManager().cancel(plugin)
            server.getPluginManager().callEvent(PluginDisableEvent(plugin))
            plugin.setEnabled(false)
        }
    }

    fun getClassByName(name: String): Class<*>? {
        var cachedClass: Class<*>? = classes[name]
        if (cachedClass != null) {
            return cachedClass
        } else {
            for (loader in classLoaders.values()) {
                try {
                    cachedClass = loader.findClass(name, false)
                } catch (e: ClassNotFoundException) {
                    //ignore
                }
                if (cachedClass != null) {
                    return cachedClass
                }
            }
        }
        return null
    }

    fun setClass(name: String, clazz: Class<*>?) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz)
        }
    }

    private fun removeClass(name: String) {
        val clazz: Class<*> = classes.remove(name)
    }

    init {
        this.server = server
    }
}