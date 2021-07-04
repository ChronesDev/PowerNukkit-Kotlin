package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@Since("1.3.0.0-PN")
@ToString
class CodeBuilderPacket : DataPacket() {
    @Since("1.3.0.0-PN")
    var isOpening = false

    @Since("1.3.0.0-PN")
    var url = ""

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        url = this.getString()
        isOpening = this.getBoolean()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putString(url)
        this.putBoolean(isOpening)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.CODE_BUILDER_PACKET
    }
}