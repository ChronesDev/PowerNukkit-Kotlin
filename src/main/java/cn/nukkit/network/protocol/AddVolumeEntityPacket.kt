package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.5.0.0-PN")
class AddVolumeEntityPacket : DataPacket() {
    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var id: Long = 0
    private var data: CompoundTag? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        id = getUnsignedVarInt()
        data = getTag()
    }

    @Override
    override fun encode() {
        putUnsignedVarInt(id)
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
        val NETWORK_ID: Byte = ProtocolInfo.ADD_VOLUME_ENTITY
    }
}