package cn.nukkit.permission

import cn.nukkit.utils.Utils

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class BanList(private val file: String) {
    private var list: LinkedHashMap<String, BanEntry> = LinkedHashMap()
    var isEnable = true
    val entires: LinkedHashMap<String, BanEntry>
        get() {
            removeExpired()
            return list
        }

    fun isBanned(name: String?): Boolean {
        return if (!isEnable || name == null) {
            false
        } else {
            removeExpired()
            list.containsKey(name.toLowerCase())
        }
    }

    fun add(entry: BanEntry) {
        list.put(entry.getName(), entry)
        save()
    }

    fun addBan(target: String?): BanEntry {
        return this.addBan(target, null)
    }

    fun addBan(target: String?, reason: String?): BanEntry {
        return this.addBan(target, reason, null)
    }

    fun addBan(target: String?, reason: String?, expireDate: Date?): BanEntry {
        return this.addBan(target, reason, expireDate, null)
    }

    fun addBan(target: String?, reason: String?, expireDate: Date?, source: String?): BanEntry {
        val entry = BanEntry(target)
        entry.setSource(source ?: entry.getSource())
        entry.setExpirationDate(expireDate)
        entry.setReason(reason ?: entry.getReason())
        add(entry)
        return entry
    }

    fun remove(name: String) {
        var name = name
        name = name.toLowerCase()
        if (list.containsKey(name)) {
            list.remove(name)
            save()
        }
    }

    fun removeExpired() {
        for (name in ArrayList(list.keySet())) {
            val entry: BanEntry = list.get(name)
            if (entry.hasExpired()) {
                list.remove(name)
            }
        }
    }

    fun load() {
        list = LinkedHashMap()
        val file = File(file)
        try {
            if (!file.exists()) {
                file.createNewFile()
                save()
            } else {
                val list: LinkedList<TreeMap<String, String>> = Gson().fromJson(Utils.readFile(this.file), object : TypeToken<LinkedList<TreeMap<String?, String?>?>?>() {}.getType())
                for (map in list) {
                    val entry: BanEntry = BanEntry.fromMap(map)
                    this.list.put(entry.getName(), entry)
                }
            }
        } catch (e: IOException) {
            log.error("Could not load ban list: ", e)
        }
    }

    fun save() {
        removeExpired()
        try {
            val file = File(file)
            if (!file.exists()) {
                file.createNewFile()
            }
            val list: LinkedList<LinkedHashMap<String, String>> = LinkedList()
            for (entry in this.list.values()) {
                list.add(entry.getMap())
            }
            Utils.writeFile(this.file, ByteArrayInputStream(GsonBuilder().setPrettyPrinting().create().toJson(list).getBytes(StandardCharsets.UTF_8)))
        } catch (e: IOException) {
            log.error("Could not save ban list ", e)
        }
    }
}