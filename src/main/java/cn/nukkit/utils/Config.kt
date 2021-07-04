package cn.nukkit.utils

import cn.nukkit.Server

/**
 * @author MagicDroidX (Nukkit)
 */
@Log4j2
class Config {
    //private LinkedHashMap<String, Object> config = new LinkedHashMap<>();
    private var config: ConfigSection = ConfigSection()
    private var file: File? = null
    var isCorrect = false
        private set
    private var type = DETECT

    companion object {
        const val DETECT = -1 //Detect by file extension
        const val PROPERTIES = 0 // .properties
        const val CNF = PROPERTIES // .cnf
        const val JSON = 1 // .js, .json
        const val YAML = 2 // .yml, .yaml

        //public static final int EXPORT = 3; // .export, .xport
        //public static final int SERIALIZED = 4; // .sl
        const val ENUM = 5 // .txt, .list, .enum
        const val ENUMERATION = ENUM
        val format: Map<String, Integer> = TreeMap()

        init {
            format.put("properties", PROPERTIES)
            format.put("con", PROPERTIES)
            format.put("conf", PROPERTIES)
            format.put("config", PROPERTIES)
            format.put("js", JSON)
            format.put("json", JSON)
            format.put("yml", YAML)
            format.put("yaml", YAML)
            //format.put("sl", Config.SERIALIZED);
            //format.put("serialize", Config.SERIALIZED);
            format.put("txt", ENUM)
            format.put("list", ENUM)
            format.put("enum", ENUM)
        }
    }
    /**
     * Constructor for Config instance with undefined file object
     *
     * @param type - Config type
     */
    /**
     * Constructor for Config (YAML) instance with undefined file object
     */
    @JvmOverloads
    constructor(type: Int = YAML) {
        this.type = type
        isCorrect = true
        config = ConfigSection()
    }

    constructor(file: String?) : this(file, DETECT) {}
    constructor(file: File) : this(file.toString(), DETECT) {}
    constructor(file: String?, type: Int) : this(file, type, ConfigSection()) {}
    constructor(file: File, type: Int) : this(file.toString(), type, ConfigSection()) {}

    @Deprecated
    constructor(file: String?, type: Int, defaultMap: LinkedHashMap<String?, Object?>?) {
        this.load(file, type, ConfigSection(defaultMap))
    }

    constructor(file: String?, type: Int, defaultMap: ConfigSection) {
        this.load(file, type, defaultMap)
    }

    constructor(file: File, type: Int, defaultMap: ConfigSection) {
        this.load(file.toString(), type, defaultMap)
    }

    @Deprecated
    constructor(file: File, type: Int, defaultMap: LinkedHashMap<String?, Object?>?) : this(file.toString(), type, ConfigSection(defaultMap)) {
    }

    fun reload() {
        config.clear()
        isCorrect = false
        //this.load(this.file.toString());
        if (file == null) throw IllegalStateException("Failed to reload Config. File object is undefined.")
        this.load(file.toString(), type)
    }

    fun load(file: String?): Boolean {
        return this.load(file, DETECT)
    }

    fun load(file: String?, type: Int): Boolean {
        return this.load(file, type, ConfigSection())
    }

    fun load(file: String?, type: Int, defaultMap: ConfigSection): Boolean {
        isCorrect = true
        this.type = type
        this.file = File(file)
        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdirs()
                this.file.createNewFile()
            } catch (e: IOException) {
                log.error("Could not create Config {}", this.file.toString(), e)
            }
            config = defaultMap
            this.save()
        } else {
            if (this.type == DETECT) {
                var extension = ""
                if (this.file.getName().lastIndexOf(".") !== -1 && this.file.getName().lastIndexOf(".") !== 0) {
                    extension = this.file.getName().substring(this.file.getName().lastIndexOf(".") + 1)
                }
                if (format.containsKey(extension)) {
                    this.type = format[extension]
                } else {
                    isCorrect = false
                }
            }
            if (isCorrect) {
                var content = ""
                try {
                    content = Utils.readFile(this.file)
                } catch (e: IOException) {
                    log.error("An error occurred while loading the file {}", file, e)
                }
                parseContent(content)
                if (!isCorrect) return false
                if (this.setDefault(defaultMap) > 0) {
                    this.save()
                }
            } else {
                return false
            }
        }
        return true
    }

    fun load(inputStream: InputStream?): Boolean {
        if (inputStream == null) return false
        if (isCorrect) {
            val content: String
            content = try {
                Utils.readFile(inputStream)
            } catch (e: IOException) {
                log.error("An error occurred while loading a config from an input stream, input: {}", inputStream, e)
                return false
            }
            parseContent(content)
        }
        return isCorrect
    }

    fun check(): Boolean {
        return isCorrect
    }

    /**
     * Save configuration into provided file. Internal file object will be set to new file.
     *
     * @param file
     * @param async
     * @return
     */
    fun save(file: File?, async: Boolean): Boolean {
        this.file = file
        return save(async)
    }

    fun save(file: File?): Boolean {
        this.file = file
        return save()
    }

    fun save(): Boolean {
        return this.save(false)
    }

    fun save(async: Boolean): Boolean {
        if (file == null) throw IllegalStateException("Failed to save Config. File object is undefined.")
        return if (isCorrect) {
            var content = StringBuilder()
            when (type) {
                PROPERTIES -> content = StringBuilder(writeProperties())
                JSON -> content = StringBuilder(GsonBuilder().setPrettyPrinting().create().toJson(config))
                YAML -> {
                    val dumperOptions = DumperOptions()
                    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
                    val yaml = Yaml(dumperOptions)
                    content = StringBuilder(yaml.dump(config))
                }
                ENUM -> for (o in config.entrySet()) {
                    content.append(o.getKey()).append("\r\n")
                }
            }
            if (async) {
                Server.getInstance().getScheduler().scheduleAsyncTask(FileWriteTask(file, content.toString()))
            } else {
                try {
                    Utils.writeFile(file, content.toString())
                } catch (e: IOException) {
                    log.error("Failed to save the config file {}", file, e)
                }
            }
            true
        } else {
            false
        }
    }

    operator fun set(key: String, value: Object?) {
        config.set(key, value)
    }

    operator fun get(key: String?): Object? {
        return this.get<Any?>(key, null)
    }

    operator fun <T> get(key: String?, defaultValue: T): T {
        return if (isCorrect) config.get(key, defaultValue) else defaultValue
    }

    fun getSection(key: String?): ConfigSection {
        return if (isCorrect) config.getSection(key) else ConfigSection()
    }

    fun isSection(key: String?): Boolean {
        return config.isSection(key)
    }

    fun getSections(key: String?): ConfigSection {
        return if (isCorrect) config.getSections(key) else ConfigSection()
    }

    val sections: cn.nukkit.utils.ConfigSection
        get() = if (isCorrect) config.getSections() else ConfigSection()

    fun getInt(key: String?): Int {
        return this.getInt(key, 0)
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return if (isCorrect) config.getInt(key, defaultValue) else defaultValue
    }

    fun isInt(key: String?): Boolean {
        return config.isInt(key)
    }

    fun getLong(key: String?): Long {
        return this.getLong(key, 0)
    }

    fun getLong(key: String?, defaultValue: Long): Long {
        return if (isCorrect) config.getLong(key, defaultValue) else defaultValue
    }

    fun isLong(key: String?): Boolean {
        return config.isLong(key)
    }

    fun getDouble(key: String?): Double {
        return this.getDouble(key, 0.0)
    }

    fun getDouble(key: String?, defaultValue: Double): Double {
        return if (isCorrect) config.getDouble(key, defaultValue) else defaultValue
    }

    fun isDouble(key: String?): Boolean {
        return config.isDouble(key)
    }

    fun getString(key: String?): String {
        return this.getString(key, "")
    }

    fun getString(key: String?, defaultValue: String?): String {
        return if (isCorrect) config.getString(key, defaultValue) else defaultValue!!
    }

    fun isString(key: String?): Boolean {
        return config.isString(key)
    }

    fun getBoolean(key: String?): Boolean {
        return this.getBoolean(key, false)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return if (isCorrect) config.getBoolean(key, defaultValue) else defaultValue
    }

    fun isBoolean(key: String?): Boolean {
        return config.isBoolean(key)
    }

    fun getList(key: String?): List? {
        return this.getList(key, null)
    }

    fun getList(key: String?, defaultList: List?): List? {
        return if (isCorrect) config.getList(key, defaultList) else defaultList
    }

    fun isList(key: String?): Boolean {
        return config.isList(key)
    }

    fun getStringList(key: String?): List<String> {
        return config.getStringList(key)
    }

    fun getIntegerList(key: String?): List<Integer> {
        return config.getIntegerList(key)
    }

    fun getBooleanList(key: String?): List<Boolean> {
        return config.getBooleanList(key)
    }

    fun getDoubleList(key: String?): List<Double> {
        return config.getDoubleList(key)
    }

    fun getFloatList(key: String?): List<Float> {
        return config.getFloatList(key)
    }

    fun getLongList(key: String?): List<Long> {
        return config.getLongList(key)
    }

    fun getByteList(key: String?): List<Byte> {
        return config.getByteList(key)
    }

    fun getCharacterList(key: String?): List<Character> {
        return config.getCharacterList(key)
    }

    fun getShortList(key: String?): List<Short> {
        return config.getShortList(key)
    }

    fun getMapList(key: String?): List<Map> {
        return config.getMapList(key)
    }

    fun setAll(section: ConfigSection) {
        config = section
    }

    fun exists(key: String): Boolean {
        return config.exists(key)
    }

    fun exists(key: String, ignoreCase: Boolean): Boolean {
        return config.exists(key, ignoreCase)
    }

    fun remove(key: String?) {
        config.remove(key)
    }

    var all: Map<String, Object>
        get() = config.getAllMap()
        set(map) {
            config = ConfigSection(map)
        }

    /**
     * Get root (main) config section of the Config
     *
     * @return
     */
    val rootSection: cn.nukkit.utils.ConfigSection
        get() = config

    fun setDefault(map: LinkedHashMap<String?, Object?>?): Int {
        return setDefault(ConfigSection(map))
    }

    fun setDefault(map: ConfigSection): Int {
        val size: Int = config.size()
        config = fillDefaults(map, config)
        return config.size() - size
    }

    private fun fillDefaults(defaultMap: ConfigSection, data: ConfigSection): ConfigSection {
        for (key in defaultMap.keySet()) {
            if (!data.containsKey(key)) {
                data.put(key, defaultMap.get(key))
            }
        }
        return data
    }

    private fun parseList(content: String) {
        var content = content
        content = content.replace("\r\n", "\n")
        for (v in content.split("\n")) {
            if (v.trim().isEmpty()) {
                continue
            }
            config.put(v, true)
        }
    }

    private fun writeProperties(): String {
        val content = StringBuilder("""
    #Properties Config file
    #${SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()).toString()}
    
    """.trimIndent())
        for (o in config.entrySet()) {
            val entry: Map.Entry = o
            var v: Object = entry.getValue()
            val k: Object = entry.getKey()
            if (v is Boolean) {
                v = if (v) "on" else "off"
            }
            content.append(k).append("=").append(v).append("\r\n")
        }
        return content.toString()
    }

    private fun parseProperties(content: String) {
        for (line in content.split("\n")) {
            if (Pattern.compile("[a-zA-Z0-9\\-_.]*+=+[^\\r\\n]*").matcher(line).matches()) {
                val splitIndex: Int = line.indexOf('=')
                if (splitIndex == -1) {
                    continue
                }
                val key: String = line.substring(0, splitIndex)
                val value: String = line.substring(splitIndex + 1)
                val valueLower: String = value.toLowerCase()
                if (config.containsKey(key)) {
                    log.debug("[Config] Repeated property {} on file {}", key, file.toString())
                }
                when (valueLower) {
                    "on", "true", "yes" -> config.put(key, true)
                    "off", "false", "no" -> config.put(key, false)
                    else -> config.put(key, value)
                }
            }
        }
    }

    @Deprecated
    @Deprecated("use {@link #get(String)} instead")
    fun getNested(key: String?): Object? {
        return get(key)
    }

    @Deprecated
    @Deprecated("use {@link #get(String, Object)} instead")
    fun <T> getNested(key: String?, defaultValue: T): T {
        return get(key, defaultValue)
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    @Deprecated("use {@link #get(String)} instead")
    fun <T> getNestedAs(key: String?, type: Class<T>?): T? {
        return get(key)
    }

    @Deprecated
    @Deprecated("use {@link #remove(String)} instead")
    fun removeNested(key: String?) {
        remove(key)
    }

    private fun parseContent(content: String) {
        try {
            when (type) {
                PROPERTIES -> parseProperties(content)
                JSON -> {
                    val builder = GsonBuilder()
                    val gson: Gson = builder.create()
                    config = ConfigSection(gson.fromJson(content, object : TypeToken<LinkedHashMap<String?, Object?>?>() {}.getType()))
                }
                YAML -> {
                    val dumperOptions = DumperOptions()
                    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
                    val yaml = Yaml(dumperOptions)
                    config = ConfigSection(yaml.loadAs(content, LinkedHashMap::class.java))
                }
                ENUM -> parseList(content)
                else -> isCorrect = false
            }
        } catch (e: Exception) {
            log.warn("Failed to parse the config file {}", file, e)
            throw e
        }
    }

    val keys: Set<String>
        get() = if (isCorrect) config.getKeys() else HashSet()

    fun getKeys(child: Boolean): Set<String> {
        return if (isCorrect) config.getKeys(child) else HashSet()
    }
}