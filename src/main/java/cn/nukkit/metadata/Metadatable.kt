package cn.nukkit.metadata

import cn.nukkit.plugin.Plugin

/**
 * @author MagicDroidX (Nukkit Project)
 */
interface Metadatable {
    @Throws(Exception::class)
    fun setMetadata(metadataKey: String?, newMetadataValue: MetadataValue?)

    @Throws(Exception::class)
    fun getMetadata(metadataKey: String?): List<MetadataValue?>?

    @Throws(Exception::class)
    fun hasMetadata(metadataKey: String?): Boolean

    @Throws(Exception::class)
    fun removeMetadata(metadataKey: String?, owningPlugin: Plugin?)
}