package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ScriptCustomEventPacket : DataPacket() {
    var eventName: String? = null
    var eventData: ByteArray

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.SCRIPT_CUSTOM_EVENT_PACKET
    }

    @Override
    override fun decode() {
        eventName = this.getString()
        eventData = this.getByteArray()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putString(eventName)
        this.putByteArray(eventData)
    }
}