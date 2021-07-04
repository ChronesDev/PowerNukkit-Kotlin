package cn.nukkit.utils

import java.util.*

/**
 * @author fromgate
 * @since 26.04.2016
 */
class ConfigSection
/**
 * Empty ConfigSection constructor
 */
() : LinkedHashMap<String?, Object?>() {
    /**
     * Constructor of ConfigSection that contains initial key/value data
     *
     * @param key
     * @param value
     */
    constructor(key: String, value: Object?) : this() {
        this[key] = value
    }

    /**
     * Constructor of ConfigSection, based on values stored in map.
     *
     * @param map
     */
    constructor(map: LinkedHashMap<String?, Object?>?) : this() {
        if (map == null || map.isEmpty()) return
        for (entry in map.entrySet()) {
            if (entry.getValue() is LinkedHashMap) {
                super.put(entry.getKey(), ConfigSection(entry.getValue() as LinkedHashMap))
            } else if (entry.getValue() is List) {
                super.put(entry.getKey(), parseList(entry.getValue() as List))
            } else {
                super.put(entry.getKey(), entry.getValue())
            }
        }
    }

    private fun parseList(list: List): List {
        val newList: List<Object> = ArrayList()
        for (o in list) {
            if (o is LinkedHashMap) {
                newList.add(ConfigSection(o as LinkedHashMap))
            } else {
                newList.add(o)
            }
        }
        return newList
    }

    /**
     * Get root section as LinkedHashMap
     *
     * @return
     */
    val allMap: Map<String, Any>
        get() = LinkedHashMap(this)

    /**
     * Get new instance of config section
     *
     * @return
     */
    val all: ConfigSection
        get() = ConfigSection(this)

    /**
     * Get object by key. If section does not contain value, return null
     */
    operator fun get(key: String?): Object? {
        return this.get<Any?>(key, null)
    }

    /**
     * Get object by key. If section does not contain value, return default value
     *
     * @param key
     * @param defaultValue
     * @return
     */
    operator fun <T> get(key: String?, defaultValue: T): T {
        if (key == null || key.isEmpty()) return defaultValue
        if (super.containsKey(key)) return super.get(key) as T
        val keys: Array<String> = key.split("\\.", 2)
        if (!super.containsKey(keys[0])) return defaultValue
        val value: Object = super.get(keys[0])
        if (value is ConfigSection) {
            val section = value as ConfigSection
            return section.get(keys[1], defaultValue)
        }
        return defaultValue
    }

    /**
     * Store value into config section
     *
     * @param key
     * @param value
     */
    operator fun set(key: String, value: Object?) {
        val subKeys: Array<String> = key.split("\\.", 2)
        if (subKeys.size > 1) {
            var childSection = ConfigSection()
            if (this.containsKey(subKeys[0]) && super.get(subKeys[0]) is ConfigSection) childSection = super.get(subKeys[0]) as ConfigSection
            childSection[subKeys[1]] = value
            super.put(subKeys[0], childSection)
        } else super.put(subKeys[0], value)
    }

    /**
     * Check type of section element defined by key. Return true this element is ConfigSection
     *
     * @param key
     * @return
     */
    fun isSection(key: String?): Boolean {
        val value: Object? = this[key]
        return value is ConfigSection
    }

    /**
     * Get config section element defined by key
     *
     * @param key
     * @return
     */
    fun getSection(key: String?): ConfigSection {
        return this[key, ConfigSection()]
    }
    //@formatter:off
    /**
     * Get all ConfigSections in root path.
     * Example config:
     * a1:
     * b1:
     * c1:
     * c2:
     * a2:
     * b2:
     * c3:
     * c4:
     * a3: true
     * a4: "hello"
     * a5: 100
     *
     *
     * getSections() will return new ConfigSection, that contains sections a1 and a2 only.
     *
     * @return
     */
    //@formatter:on
    val sections: ConfigSection
        get() = getSections(null)

    /**
     * Get sections (and only sections) from provided path
     *
     * @param key - config section path, if null or empty root path will used.
     * @return
     */
    fun getSections(key: String?): ConfigSection {
        val sections = ConfigSection()
        val parent = (if (key == null || key.isEmpty()) all else getSection(key))
                ?: return sections
        parent.forEach { key1, value -> if (value is ConfigSection) sections.put(key1, value) }
        return sections
    }

    /**
     * Get int value of config section element
     *
     * @param key - key (inside) current section (default value equals to 0)
     * @return
     */
    fun getInt(key: String?): Int {
        return this.getInt(key, 0)
    }

    /**
     * Get int value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getInt(key: String?, defaultValue: Int): Int {
        return this.get<Any>(key, defaultValue as Number).intValue()
    }

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key
     * @return
     */
    fun isInt(key: String?): Boolean {
        val `val`: Object? = get(key)
        return `val` is Integer
    }

    /**
     * Get long value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getLong(key: String?): Long {
        return this.getLong(key, 0)
    }

    /**
     * Get long value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getLong(key: String?, defaultValue: Long): Long {
        return this.get<Any>(key, defaultValue as Number).longValue()
    }

    /**
     * Check type of section element defined by key. Return true this element is Long
     *
     * @param key
     * @return
     */
    fun isLong(key: String?): Boolean {
        val `val`: Object? = get(key)
        return `val` is Long
    }

    /**
     * Get double value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getDouble(key: String?): Double {
        return this.getDouble(key, 0.0)
    }

    /**
     * Get double value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getDouble(key: String?, defaultValue: Double): Double {
        return this.get<Any>(key, defaultValue as Number).doubleValue()
    }

    /**
     * Check type of section element defined by key. Return true this element is Double
     *
     * @param key
     * @return
     */
    fun isDouble(key: String?): Boolean {
        val `val`: Object? = get(key)
        return `val` is Double
    }

    /**
     * Get String value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getString(key: String?): String {
        return this.getString(key, "")
    }

    /**
     * Get String value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getString(key: String?, defaultValue: String?): String {
        val result: Object? = this.get<Any?>(key, defaultValue)
        return String.valueOf(result)
    }

    /**
     * Check type of section element defined by key. Return true this element is String
     *
     * @param key
     * @return
     */
    fun isString(key: String?): Boolean {
        val `val`: Object? = get(key)
        return `val` is String
    }

    /**
     * Get boolean value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getBoolean(key: String?): Boolean {
        return this.getBoolean(key, false)
    }

    /**
     * Get boolean value of config section element
     *
     * @param key          - key (inside) current section
     * @param defaultValue - default value that will returned if section element is not exists
     * @return
     */
    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return this.get(key, defaultValue)
    }

    /**
     * Check type of section element defined by key. Return true this element is Integer
     *
     * @param key
     * @return
     */
    fun isBoolean(key: String?): Boolean {
        val `val`: Object? = get(key)
        return `val` is Boolean
    }

    /**
     * Get List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getList(key: String?): List? {
        return this.getList(key, null)
    }

    /**
     * Get List value of config section element
     *
     * @param key         - key (inside) current section
     * @param defaultList - default value that will returned if section element is not exists
     * @return
     */
    fun getList(key: String?, defaultList: List?): List? {
        return this.get<T?>(key, defaultList)
    }

    /**
     * Check type of section element defined by key. Return true this element is List
     *
     * @param key
     * @return
     */
    fun isList(key: String?): Boolean {
        val `val`: Object? = get(key)
        return `val` is List
    }

    /**
     * Get String List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getStringList(key: String?): List<String> {
        val value: List = this.getList(key) ?: return ArrayList(0)
        val result: List<String> = ArrayList()
        for (o in value) {
            if (o is String || o is Number || o is Boolean || o is Character) {
                result.add(String.valueOf(o))
            }
        }
        return result
    }

    /**
     * Get Integer List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getIntegerList(key: String?): List<Integer> {
        val list: List<*> = getList(key) ?: return ArrayList(0)
        val result: List<Integer> = ArrayList()
        for (`object` in list) {
            if (`object` is Integer) {
                result.add(`object` as Integer)
            } else if (`object` is String) {
                try {
                    result.add(Integer.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
            } else if (`object` is Character) {
                result.add(`object` as Character as Int)
            } else if (`object` is Number) {
                result.add(`object`.intValue())
            }
        }
        return result
    }

    /**
     * Get Boolean List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getBooleanList(key: String?): List<Boolean> {
        val list: List<*> = getList(key) ?: return ArrayList(0)
        val result: List<Boolean> = ArrayList()
        for (`object` in list) {
            if (`object` is Boolean) {
                result.add(`object`)
            } else if (`object` is String) {
                if (Boolean.TRUE.toString().equals(`object`)) {
                    result.add(true)
                } else if (Boolean.FALSE.toString().equals(`object`)) {
                    result.add(false)
                }
            }
        }
        return result
    }

    /**
     * Get Double List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getDoubleList(key: String?): List<Double> {
        val list: List<*> = getList(key) ?: return ArrayList(0)
        val result: List<Double> = ArrayList()
        for (`object` in list) {
            if (`object` is Double) {
                result.add(`object`)
            } else if (`object` is String) {
                try {
                    result.add(Double.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
            } else if (`object` is Character) {
                result.add(`object` as Character as Double)
            } else if (`object` is Number) {
                result.add(`object`.doubleValue())
            }
        }
        return result
    }

    /**
     * Get Float List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getFloatList(key: String?): List<Float> {
        val list: List<*> = getList(key) ?: return ArrayList(0)
        val result: List<Float> = ArrayList()
        for (`object` in list) {
            if (`object` is Float) {
                result.add(`object`)
            } else if (`object` is String) {
                try {
                    result.add(Float.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
            } else if (`object` is Character) {
                result.add(`object` as Character as Float)
            } else if (`object` is Number) {
                result.add(`object`.floatValue())
            }
        }
        return result
    }

    /**
     * Get Long List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getLongList(key: String?): List<Long> {
        val list: List<*> = getList(key) ?: return ArrayList(0)
        val result: List<Long> = ArrayList()
        for (`object` in list) {
            if (`object` is Long) {
                result.add(`object`)
            } else if (`object` is String) {
                try {
                    result.add(Long.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
            } else if (`object` is Character) {
                result.add(`object` as Character as Long)
            } else if (`object` is Number) {
                result.add(`object`.longValue())
            }
        }
        return result
    }

    /**
     * Get Byte List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getByteList(key: String?): List<Byte> {
        val list: List<*> = getList(key) ?: return ArrayList(0)
        val result: List<Byte> = ArrayList()
        for (`object` in list) {
            if (`object` is Byte) {
                result.add(`object`)
            } else if (`object` is String) {
                try {
                    result.add(Byte.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
            } else if (`object` is Character) {
                result.add((`object` as Character).charValue() as Byte)
            } else if (`object` is Number) {
                result.add(`object`.byteValue())
            }
        }
        return result
    }

    /**
     * Get Character List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getCharacterList(key: String?): List<Character> {
        val list: List<*> = getList(key) ?: return ArrayList(0)
        val result: List<Character> = ArrayList()
        for (`object` in list) {
            if (`object` is Character) {
                result.add(`object` as Character)
            } else if (`object` is String) {
                val str = `object`
                if (str.length() === 1) {
                    result.add(str.charAt(0))
                }
            } else if (`object` is Number) {
                result.add(`object`.intValue() as Char)
            }
        }
        return result
    }

    /**
     * Get Short List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getShortList(key: String?): List<Short> {
        val list: List<*> = getList(key) ?: return ArrayList(0)
        val result: List<Short> = ArrayList()
        for (`object` in list) {
            if (`object` is Short) {
                result.add(`object`)
            } else if (`object` is String) {
                try {
                    result.add(Short.valueOf(`object`))
                } catch (ex: Exception) {
                    //ignore
                }
            } else if (`object` is Character) {
                result.add((`object` as Character).charValue() as Short)
            } else if (`object` is Number) {
                result.add(`object`.shortValue())
            }
        }
        return result
    }

    /**
     * Get Map List value of config section element
     *
     * @param key - key (inside) current section
     * @return
     */
    fun getMapList(key: String?): List<Map> {
        val list: List<Map>? = getList(key)
        val result: List<Map> = ArrayList()
        if (list == null) {
            return result
        }
        for (`object` in list) {
            if (`object` is Map) {
                result.add(`object` as Map)
            }
        }
        return result
    }

    /**
     * Check existence of config section element
     *
     * @param key
     * @param ignoreCase
     * @return
     */
    fun exists(key: String, ignoreCase: Boolean): Boolean {
        var key = key
        if (ignoreCase) key = key.toLowerCase()
        for (existKey in getKeys(true)) {
            if (ignoreCase) existKey = existKey.toLowerCase()
            if (existKey.equals(key)) return true
        }
        return false
    }

    /**
     * Check existence of config section element
     *
     * @param key
     * @return
     */
    fun exists(key: String): Boolean {
        return exists(key, false)
    }

    /**
     * Remove config section element
     *
     * @param key
     */
    fun remove(key: String?) {
        if (key == null || key.isEmpty()) return
        if (super.containsKey(key)) super.remove(key) else if (this.containsKey(".")) {
            val keys: Array<String> = key.split("\\.", 2)
            if (super.get(keys[0]) is ConfigSection) {
                val section = super.get(keys[0]) as ConfigSection
                section.remove(keys[1])
            }
        }
    }

    /**
     * Get all keys
     *
     * @param child - true = include child keys
     * @return
     */
    fun getKeys(child: Boolean): Set<String> {
        val keys: Set<String> = LinkedHashSet()
        this.forEach { key, value ->
            keys.add(key)
            if (value is ConfigSection) {
                if (child) (value as ConfigSection).getKeys(true).forEach { childKey -> keys.add(key.toString() + "." + childKey) }
            }
        }
        return keys
    }

    /**
     * Get all keys
     *
     * @return
     */
    val keys: Set<String>
        get() = getKeys(true)
}