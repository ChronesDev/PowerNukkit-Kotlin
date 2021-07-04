package cn.nukkit.permission

import com.google.gson.Gson

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class BanEntry(name: String?) {
    val name: String
    private var creationDate: Date? = null
    var source: String? = "(Unknown)"
    private var expirationDate: Date? = null
    var reason: String? = "Banned by an operator."
    fun getCreationDate(): Date? {
        return creationDate
    }

    fun setCreationDate(creationDate: Date?) {
        this.creationDate = creationDate
    }

    fun getExpirationDate(): Date? {
        return expirationDate
    }

    fun setExpirationDate(expirationDate: Date?) {
        this.expirationDate = expirationDate
    }

    fun hasExpired(): Boolean {
        val now = Date()
        return expirationDate != null && expirationDate.before(now)
    }

    val map: LinkedHashMap<String, String>
        get() {
            val map: LinkedHashMap<String, String> = LinkedHashMap()
            map.put("name", name)
            map.put("creationDate", SimpleDateFormat(format).format(getCreationDate()))
            map.put("source", source)
            map.put("expireDate", if (getExpirationDate() != null) SimpleDateFormat(format).format(getExpirationDate()) else "Forever")
            map.put("reason", reason)
            return map
        }
    val string: String
        get() = Gson().toJson(map)

    companion object {
        const val format = "yyyy-MM-dd HH:mm:ss Z"
        fun fromMap(map: Map<String?, String>): BanEntry {
            val banEntry = BanEntry(map["name"])
            try {
                banEntry.setCreationDate(SimpleDateFormat(format).parse(map["creationDate"]))
                banEntry.setExpirationDate(if (!map["expireDate"]!!.equals("Forever")) SimpleDateFormat(format).parse(map["expireDate"]) else null)
            } catch (e: ParseException) {
                log.error("An exception happed while loading the ban list.", e)
            }
            banEntry.source = map["source"]
            banEntry.reason = map["reason"]
            return banEntry
        }

        fun fromString(str: String?): BanEntry {
            val map: Map<String, String> = Gson().fromJson(str, object : TypeToken<TreeMap<String?, String?>?>() {}.getType())
            val banEntry = BanEntry(map["name"])
            try {
                banEntry.setCreationDate(SimpleDateFormat(format).parse(map["creationDate"]))
                banEntry.setExpirationDate(if (!map["expireDate"]!!.equals("Forever")) SimpleDateFormat(format).parse(map["expireDate"]) else null)
            } catch (e: ParseException) {
                log.error("An exception happened while loading a ban entry from the string {}", str, e)
            }
            banEntry.source = map["source"]
            banEntry.reason = map["reason"]
            return banEntry
        }
    }

    init {
        this.name = name.toLowerCase()
        creationDate = Date()
    }
}