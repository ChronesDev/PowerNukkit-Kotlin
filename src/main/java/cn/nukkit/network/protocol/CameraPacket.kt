package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class CameraPacket : DataPacket() {
    var cameraUniqueId: Long = 0
    var playerUniqueId: Long = 0

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.CAMERA_PACKET
    }

    @Override
    override fun decode() {
        cameraUniqueId = this.getVarLong()
        playerUniqueId = this.getVarLong()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityUniqueId(cameraUniqueId)
        this.putEntityUniqueId(playerUniqueId)
    }
}