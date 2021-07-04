package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class StructureBlockUpdatePacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        //TODO
    }
}