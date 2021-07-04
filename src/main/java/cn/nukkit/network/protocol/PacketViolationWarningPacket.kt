package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@Since("1.3.0.0-PN")
@ToString
class PacketViolationWarningPacket : DataPacket() {
    @Since("1.3.0.0-PN")
    var type: PacketViolationType? = null

    @Since("1.3.0.0-PN")
    var severity: PacketViolationSeverity? = null

    @Since("1.3.0.0-PN")
    var packetId = 0

    @Since("1.3.0.0-PN")
    var context: String? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        type = PacketViolationType.values()[this.getVarInt() + 1]
        severity = PacketViolationSeverity.values()[this.getVarInt()]
        packetId = this.getVarInt()
        context = this.getString()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(type!!.ordinal() - 1)
        this.putVarInt(severity!!.ordinal())
        this.putVarInt(packetId)
        this.putString(context)
    }

    @Since("1.3.0.0-PN")
    enum class PacketViolationType {
        UNKNOWN, MALFORMED_PACKET
    }

    @Since("1.3.0.0-PN")
    enum class PacketViolationSeverity {
        UNKNOWN, WARNING, FINAL_WARNING, TERMINATING_CONNECTION
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.PACKET_VIOLATION_WARNING_PACKET
    }
}