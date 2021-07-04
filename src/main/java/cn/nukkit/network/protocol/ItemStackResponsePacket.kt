package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
class ItemStackResponsePacket : DataPacket() {
    @Override
    override fun encode() {
        throw UnsupportedOperationException() //TODO
    }

    @Override
    override fun decode() {
        throw UnsupportedOperationException() //TODO
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NETWORK_ID: Byte = ProtocolInfo.ITEM_STACK_RESPONSE_PACKET
    }
}