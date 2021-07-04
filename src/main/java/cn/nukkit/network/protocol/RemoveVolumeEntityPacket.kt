package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.5.0.0-PN")
class RemoveVolumeEntityPacket : DataPacket() {
    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var id: Long = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        id = getUnsignedVarInt()
    }

    @Override
    override fun encode() {
        putUnsignedVarInt(id)
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val NETWORK_ID: Byte = ProtocolInfo.REMOVE_VOLUME_ENTITY
    }
}