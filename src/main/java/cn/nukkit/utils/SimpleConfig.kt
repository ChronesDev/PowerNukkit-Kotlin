package cn.nukkit.utils

import cn.nukkit.plugin.Plugin

/**
 * SimpleConfig for Nukkit
 * added 11/02/2016 by fromgate
 */
@Log4j2
abstract class SimpleConfig(file: File) {
    private val configFile: File

    constructor(plugin: Plugin) : this(plugin, "config.yml") {}
    constructor(plugin: Plugin, fileName: String) : this(File(plugin.getDataFolder() + File.separator + fileName)) {}

    @JvmOverloads
    fun save(async: Boolean = false): Boolean {
        if (configFile.exists()) try {
            configFile.createNewFile()
        } catch (e: Exception) {
            return false
        }
        val cfg = Config(configFile, Config.YAML)
        for (field in this.getClass().getDeclaredFields()) {
            if (skipSave(field)) continue
            val path = getPath(field)
            try {
                if (path != null) cfg.set(path, field.get(this))
            } catch (e: Exception) {
                return false
            }
        }
        cfg.save(async)
        return true
    }

    fun load(): Boolean {
        if (!configFile.exists()) return false
        val cfg = Config(configFile, Config.YAML)
        for (field in this.getClass().getDeclaredFields()) {
            if (field.getName().equals("configFile")) continue
            if (skipSave(field)) continue
            val path = getPath(field) ?: continue
            if (path.isEmpty()) continue
            field.setAccessible(true)
            try {
                if (field.getType() === Int::class.javaPrimitiveType || field.getType() === Integer::class.java) field.set(this, cfg.getInt(path, field.getInt(this))) else if (field.getType() === Boolean::class.javaPrimitiveType || field.getType() === Boolean::class.java) field.set(this, cfg.getBoolean(path, field.getBoolean(this))) else if (field.getType() === Long::class.javaPrimitiveType || field.getType() === Long::class.java) field.set(this, cfg.getLong(path, field.getLong(this))) else if (field.getType() === Double::class.javaPrimitiveType || field.getType() === Double::class.java) field.set(this, cfg.getDouble(path, field.getDouble(this))) else if (field.getType() === String::class.java) field.set(this, cfg.getString(path, field.get(this) as String)) else if (field.getType() === ConfigSection::class.java) field.set(this, cfg.getSection(path)) else if (field.getType() === List::class.java) {
                    val genericFieldType: Type = field.getGenericType()
                    if (genericFieldType is ParameterizedType) {
                        val aType: ParameterizedType = genericFieldType as ParameterizedType
                        val fieldArgClass: Class = aType.getActualTypeArguments().get(0) as Class
                        if (fieldArgClass === Integer::class.java) field.set(this, cfg.getIntegerList(path)) else if (fieldArgClass === Boolean::class.java) field.set(this, cfg.getBooleanList(path)) else if (fieldArgClass === Double::class.java) field.set(this, cfg.getDoubleList(path)) else if (fieldArgClass === Character::class.java) field.set(this, cfg.getCharacterList(path)) else if (fieldArgClass === Byte::class.java) field.set(this, cfg.getByteList(path)) else if (fieldArgClass === Float::class.java) field.set(this, cfg.getFloatList(path)) else if (fieldArgClass === Short::class.java) field.set(this, cfg.getFloatList(path)) else if (fieldArgClass === String::class.java) field.set(this, cfg.getStringList(path))
                    } else field.set(this, cfg.getList(path)) // Hell knows what's kind of List was found :)
                } else throw IllegalStateException("SimpleConfig did not supports class: " + field.getType().getName().toString() + " for config field " + configFile.getName())
            } catch (e: Exception) {
                log.error("An error occurred while loading the config {}", configFile, e)
                return false
            }
        }
        return true
    }

    private fun getPath(field: Field): String? {
        var path: String? = null
        if (field.isAnnotationPresent(Path::class.java)) {
            val pathDefine: Path = field.getAnnotation(Path::class.java)
            path = pathDefine.value()
        }
        if (path == null || path.isEmpty()) path = field.getName().replaceAll("_", ".")
        if (Modifier.isFinal(field.getModifiers())) return null
        if (Modifier.isPrivate(field.getModifiers())) field.setAccessible(true)
        return path
    }

    private fun skipSave(field: Field): Boolean {
        return if (!field.isAnnotationPresent(Skip::class.java)) false else field.getAnnotation(Skip::class.java).skipSave()
    }

    private fun skipLoad(field: Field): Boolean {
        return if (!field.isAnnotationPresent(Skip::class.java)) false else field.getAnnotation(Skip::class.java).skipLoad()
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    annotation class Path(val value: String = "")

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    annotation class Skip(val skipSave: Boolean = true, val skipLoad: Boolean = true)

    init {
        configFile = file
        configFile.getParentFile().mkdirs()
    }
}