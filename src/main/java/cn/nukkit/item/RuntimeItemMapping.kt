package cn.nukkit.item

import cn.nukkit.api.API

/**
 * Responsible for mapping item full ids, item network ids and item namespaced ids between each other.
 *
 *  * A **full id** is a combination of **item id** and **item damage**.
 * The way they are combined may change in future, so you should not combine them by yourself and neither store them
 * permanently. It's mainly used to preserve backward compatibility with plugins that don't support *namespaced ids*.
 *  * A **network id** is an id that is used to communicated with the client, it may change between executions of the
 * same server version depending on how the plugins are setup.
 *  * A **namespaced id** is the new way Mojang saves the ids, a string like `minecraft:stone`. It may change
 * in Minecraft updates but tends to be permanent, unless Mojang decides to change them for some random reasons...
 */
@Since("1.4.0.0-PN")
class RuntimeItemMapping {
    private val legacyNetworkMap: Int2IntMap
    private val networkLegacyMap: Int2IntMap
    private val itemDataPalette: ByteArray
    private val namespaceNetworkMap: Map<String, OptionalInt>
    private val networkNamespaceMap: Int2ObjectMap<String>

    @Since("1.4.0.0-PN")
    constructor(itemDataPalette: ByteArray, legacyNetworkMap: Int2IntMap, networkLegacyMap: Int2IntMap) {
        this.itemDataPalette = itemDataPalette
        this.legacyNetworkMap = legacyNetworkMap
        this.networkLegacyMap = networkLegacyMap
        this.legacyNetworkMap.defaultReturnValue(-1)
        this.networkLegacyMap.defaultReturnValue(-1)
        namespaceNetworkMap = LinkedHashMap()
        networkNamespaceMap = Int2ObjectOpenHashMap()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @API(definition = API.Definition.INTERNAL, usage = API.Usage.BLEEDING)
    constructor(
            itemDataPalette: ByteArray, legacyNetworkMap: Int2IntMap, networkLegacyMap: Int2IntMap,
            namespaceNetworkMap: Map<String?, Integer?>, networkNamespaceMap: Int2ObjectMap<String?>) {
        this.itemDataPalette = itemDataPalette
        this.legacyNetworkMap = legacyNetworkMap
        this.networkLegacyMap = networkLegacyMap
        this.legacyNetworkMap.defaultReturnValue(-1)
        this.networkLegacyMap.defaultReturnValue(-1)
        this.networkNamespaceMap = networkNamespaceMap
        this.namespaceNetworkMap = namespaceNetworkMap.entrySet().stream()
                .map { e -> SimpleEntry(e.getKey(), OptionalInt.of(e.getValue())) }
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
    }

    /**
     * Returns the **network id** based on the **full id** of the given item.
     * @param item Given item
     * @return The **network id**
     * @throws IllegalArgumentException If the mapping of the **full id** to the **network id** is unknown
     */
    @Since("1.4.0.0-PN")
    fun getNetworkFullId(item: Item): Int {
        val fullId: Int = RuntimeItems.getFullId(item.getId(), if (item.hasMeta()) item.getDamage() else -1)
        var networkId: Int = legacyNetworkMap.get(fullId)
        if (networkId == -1 && !item.hasMeta() && item.getDamage() !== 0) { // Fuzzy crafting recipe of a remapped item, like charcoal
            networkId = legacyNetworkMap.get(RuntimeItems.getFullId(item.getId(), item.getDamage()))
        }
        if (networkId == -1) {
            networkId = legacyNetworkMap.get(RuntimeItems.getFullId(item.getId(), 0))
        }
        if (networkId == -1) {
            throw IllegalArgumentException("Unknown item mapping $item")
        }
        return networkId
    }

    /**
     * Returns the **full id** of a given **network id**.
     * @param networkId The given **network id**
     * @return The **full id**
     * @throws IllegalArgumentException If the mapping of the **full id** to the **network id** is unknown
     */
    @Since("1.4.0.0-PN")
    fun getLegacyFullId(networkId: Int): Int {
        val fullId: Int = networkLegacyMap.get(networkId)
        if (fullId == -1) {
            throw IllegalArgumentException("Unknown network mapping: $networkId")
        }
        return fullId
    }

    @Since("1.4.0.0-PN")
    fun getItemDataPalette(): ByteArray {
        return itemDataPalette
    }

    /**
     * Returns the **namespaced id** of a given **network id**.
     * @param networkId The given **network id**
     * @return The **namespace id** or `null` if it is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    fun getNamespacedIdByNetworkId(networkId: Int): String {
        return networkNamespaceMap.get(networkId)
    }

    /**
     * Returns the **network id** of a given **namespaced id**.
     * @param namespaceId The given **namespaced id**
     * @return A **network id** wrapped in [OptionalInt] or an empty [OptionalInt] if it is unknown
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getNetworkIdByNamespaceId(@Nonnull namespaceId: String?): OptionalInt {
        return namespaceNetworkMap.getOrDefault(namespaceId, OptionalInt.empty())
    }

    /**
     * Creates a new instance of the respective [Item] by the **namespaced id**.
     * @param namespaceId The namespaced id
     * @param amount How many items will be in the stack.
     * @return The correct [Item] instance with the write **item id** and **item damage** values.
     * @throws IllegalArgumentException If there are unknown mappings in the process.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    fun getItemByNamespaceId(@Nonnull namespaceId: String, amount: Int): Item? {
        val legacyFullId = getLegacyFullId(
                getNetworkIdByNamespaceId(namespaceId)
                        .orElseThrow { IllegalArgumentException("The network id of \"$namespaceId\" is unknown") }
        )
        return if (RuntimeItems.hasData(legacyFullId)) {
            Item.get(RuntimeItems.getId(legacyFullId), RuntimeItems.getData(legacyFullId), amount)
        } else {
            val item: Item = Item.get(RuntimeItems.getId(legacyFullId))
            item!!.setCount(amount)
            item
        }
    }
}