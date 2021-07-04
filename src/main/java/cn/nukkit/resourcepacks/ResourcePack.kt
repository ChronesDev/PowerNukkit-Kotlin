package cn.nukkit.resourcepacks

import cn.nukkit.api.PowerNukkitOnly

interface ResourcePack {
    val packName: String?
    val packId: UUID?
    val packVersion: String?
    val packSize: Int
    val sha256: ByteArray?
    fun getPackChunk(off: Int, len: Int): ByteArray?

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val EMPTY_ARRAY = arrayOfNulls<ResourcePack>(0)
    }
}