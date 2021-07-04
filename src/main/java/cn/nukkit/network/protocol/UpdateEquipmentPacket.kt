package cn.nukkit.network.protocol

import lombok.ToString

@ToString(exclude = "namedtag")
class UpdateEquipmentPacket : DataPacket() {
    var windowId = 0
    var windowType = 0
    var unknown //TODO: find out what this is (vanilla always sends 0)
            = 0
    var eid: Long = 0
    var namedtag: ByteArray

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.UPDATE_EQUIPMENT_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(windowId.toByte())
        this.putByte(windowType.toByte())
        this.putEntityUniqueId(eid)
        this.put(namedtag)
    }
}