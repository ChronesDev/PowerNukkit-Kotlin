package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class SetCommandsEnabledPacket : DataPacket() {
    var enabled = false

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putBoolean(enabled)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.SET_COMMANDS_ENABLED_PACKET
    }
}