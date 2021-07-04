package cn.nukkit.item

import cn.nukkit.Server

@Since("1.4.0.0-PN")
@UtilityClass
@Log4j2
object RuntimeItems {
    private val GSON: Gson = Gson()
    private val ENTRY_TYPE: Type = object : TypeToken<ArrayList<Entry?>?>() {}.getType()
    private val itemPalette: RuntimeItemMapping? = null
    @Since("1.4.0.0-PN")
    fun getRuntimeMapping(): RuntimeItemMapping? {
        return itemPalette
    }

    @Since("1.4.0.0-PN")
    fun getId(fullId: Int): Int {
        return (fullId shr 16).toShort()
    }

    @Since("1.4.0.0-PN")
    fun getData(fullId: Int): Int {
        return fullId shr 1 and 0x7fff
    }

    @Since("1.4.0.0-PN")
    fun getFullId(id: Int, data: Int): Int {
        return id.toShort() shl 16 or (data and 0x7fff shl 1)
    }

    @Since("1.4.0.0-PN")
    fun getNetworkId(networkFullId: Int): Int {
        return networkFullId shr 1
    }

    @Since("1.4.0.0-PN")
    fun hasData(id: Int): Boolean {
        return id and 0x1 != 0
    }

    @ToString
    @RequiredArgsConstructor
    internal class Entry {
        var name: String? = null
        var id = 0
        var oldId: Integer? = null
        var oldData: Integer? = null

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        var deprecated: Boolean? = null
    }

    init {
        log.debug("Loading runtime items...")
        val stream: InputStream = Server::class.java.getClassLoader().getResourceAsStream("runtime_item_ids.json")
        if (cn.nukkit.item.stream == null) {
            throw AssertionError("Unable to load runtime_item_ids.json")
        }
        val reader = InputStreamReader(cn.nukkit.item.stream, StandardCharsets.UTF_8)
        val entries: Collection<Entry> = GSON.fromJson(cn.nukkit.item.reader, ENTRY_TYPE)
        val paletteBuffer = BinaryStream()
        cn.nukkit.item.paletteBuffer.putUnsignedVarInt(cn.nukkit.item.entries.size())
        val legacyNetworkMap: Int2IntMap = Int2IntOpenHashMap()
        val networkLegacyMap: Int2IntMap = Int2IntOpenHashMap()
        val namespaceNetworkMap: Map<String, Integer> = LinkedHashMap()
        val networkNamespaceMap: Int2ObjectMap<String> = Int2ObjectOpenHashMap()
        for (entry in cn.nukkit.item.entries) {
            cn.nukkit.item.paletteBuffer.putString(cn.nukkit.item.entry.name.replace("minecraft:", ""))
            cn.nukkit.item.paletteBuffer.putLShort(cn.nukkit.item.entry.id)
            cn.nukkit.item.paletteBuffer.putBoolean(false) // Component item
            cn.nukkit.item.namespaceNetworkMap.put(cn.nukkit.item.entry.name, cn.nukkit.item.entry.id)
            cn.nukkit.item.networkNamespaceMap.put(cn.nukkit.item.entry.id, cn.nukkit.item.entry.name)
            if (cn.nukkit.item.entry.oldId != null) {
                val hasData = cn.nukkit.item.entry.oldData != null
                val fullId = getFullId(cn.nukkit.item.entry.oldId, if (cn.nukkit.item.hasData) cn.nukkit.item.entry.oldData else 0)
                if (cn.nukkit.item.entry.deprecated !== Boolean.TRUE) {
                    verify(cn.nukkit.item.legacyNetworkMap.put(cn.nukkit.item.fullId, cn.nukkit.item.entry.id shl 1 or if (cn.nukkit.item.hasData) 1 else 0) === 0,
                            "Conflict while registering an item runtime id!"
                    )
                }
                verify(cn.nukkit.item.networkLegacyMap.put(cn.nukkit.item.entry.id, cn.nukkit.item.fullId or if (cn.nukkit.item.hasData) 1 else 0) === 0,
                        "Conflict while registering an item runtime id!"
                )
            }
        }
        val itemDataPalette: ByteArray = cn.nukkit.item.paletteBuffer.getBuffer()
        itemPalette = RuntimeItemMapping(cn.nukkit.item.itemDataPalette, cn.nukkit.item.legacyNetworkMap, cn.nukkit.item.networkLegacyMap,
                cn.nukkit.item.namespaceNetworkMap, cn.nukkit.item.networkNamespaceMap)
    }
}