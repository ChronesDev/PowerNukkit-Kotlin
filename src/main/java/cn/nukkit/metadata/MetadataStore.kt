package cn.nukkit.metadata

import cn.nukkit.plugin.Plugin

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class MetadataStore {
    private val metadataMap: Map<String, Map<Plugin, MetadataValue>> = HashMap()
    fun setMetadata(subject: Object?, metadataKey: String?, newMetadataValue: MetadataValue?) {
        if (newMetadataValue == null) {
            throw ServerException("Value cannot be null")
        }
        val owningPlugin: Plugin = newMetadataValue.getOwningPlugin() ?: throw PluginException("Plugin cannot be null")
        val key = disambiguate(subject as Metadatable?, metadataKey)
        val entry: Map<Plugin, MetadataValue> = metadataMap.computeIfAbsent(key) { k -> WeakHashMap(1) }
        entry.put(owningPlugin, newMetadataValue)
    }

    fun getMetadata(subject: Object?, metadataKey: String?): List<MetadataValue> {
        val key = disambiguate(subject as Metadatable?, metadataKey)
        if (metadataMap.containsKey(key)) {
            val values: Collection = (metadataMap[key] as Map).values()
            return Collections.unmodifiableList(ArrayList(values))
        }
        return Collections.emptyList()
    }

    fun hasMetadata(subject: Object?, metadataKey: String?): Boolean {
        return metadataMap.containsKey(disambiguate(subject as Metadatable?, metadataKey))
    }

    fun removeMetadata(subject: Object?, metadataKey: String?, owningPlugin: Plugin?) {
        if (owningPlugin == null) {
            throw PluginException("Plugin cannot be null")
        }
        val key = disambiguate(subject as Metadatable?, metadataKey)
        val entry: Map<Any, Any> = metadataMap[key] ?: return
        entry.remove(owningPlugin)
        if (entry.isEmpty()) {
            metadataMap.remove(key)
        }
    }

    fun invalidateAll(owningPlugin: Plugin?) {
        if (owningPlugin == null) {
            throw PluginException("Plugin cannot be null")
        }
        for (value in metadataMap.values()) {
            if (value.containsKey(owningPlugin)) {
                (value.get(owningPlugin) as MetadataValue).invalidate()
            }
        }
    }

    protected abstract fun disambiguate(subject: Metadatable?, metadataKey: String?): String
}