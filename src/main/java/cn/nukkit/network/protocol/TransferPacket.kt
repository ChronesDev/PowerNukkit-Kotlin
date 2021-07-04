package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class TransferPacket : DataPacket() {
    var address // Server address
            : String? = null
    var port = 19132 // Server port

    @Override
    override fun decode() {
        address = this.getString()
        port = this.getLShort() as Short.toInt()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putString(address)
        this.putLShort(port)
    }

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.TRANSFER_PACKET
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.TRANSFER_PACKET
    }
}