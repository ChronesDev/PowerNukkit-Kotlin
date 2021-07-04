package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author CreeperFace
 * @since 5.3.2017
 */
@ToString
class MapInfoRequestPacket : DataPacket() {
    var mapId: Long = 0

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.MAP_INFO_REQUEST_PACKET
    }

    @Override
    override fun decode() {
        mapId = this.getEntityUniqueId()
    }

    @Override
    override fun encode() {
    }
}