package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.5.0.0-PN")
class SyncEntityPropertyPacket : DataPacket() {
    private var data: CompoundTag? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        data = getTag()
    }

    @Override
    override fun encode() {
        putTag(data)
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    fun getData(): CompoundTag? {
        return data
    }

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    fun setData(data: CompoundTag?) {
        this.data = data
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val NETWORK_ID: Byte = ProtocolInfo.SYNC_ENTITY_PROPERTY
    }
}