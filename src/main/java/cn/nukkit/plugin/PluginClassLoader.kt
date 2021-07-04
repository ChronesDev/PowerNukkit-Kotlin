package cn.nukkit.plugin

import java.io.File

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PluginClassLoader(loader: JavaPluginLoader, parent: ClassLoader?, file: File) : URLClassLoader(arrayOf<URL>(file.toURI().toURL()), parent) {
    private val loader: JavaPluginLoader
    private val classes: Map<String, Class> = HashMap()
    @Override
    @Throws(ClassNotFoundException::class)
    protected fun findClass(name: String): Class<*> {
        return this.findClass(name, true)
    }

    @Throws(ClassNotFoundException::class)
    fun findClass(name: String, checkGlobal: Boolean): Class<*>? {
        if (name.startsWith("cn.nukkit.") || name.startsWith("net.minecraft.")) {
            throw ClassNotFoundException(name)
        }
        var result: Class<*>? = classes[name]
        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name)
            }
            if (result == null) {
                result = super.findClass(name)
                if (result != null) {
                    loader.setClass(name, result)
                }
            }
            classes.put(name, result)
        }
        return result
    }

    fun getClasses(): Set<String> {
        return classes.keySet()
    }

    init {
        this.loader = loader
    }
}