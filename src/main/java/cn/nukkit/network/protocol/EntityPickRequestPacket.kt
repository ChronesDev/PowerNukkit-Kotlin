package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class EntityPickRequestPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        //TODO
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.ENTITY_PICK_REQUEST_PACKET
    }
}