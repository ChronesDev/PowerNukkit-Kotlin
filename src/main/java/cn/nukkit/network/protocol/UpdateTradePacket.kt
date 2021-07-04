package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class UpdateTradePacket : DataPacket() {
    var windowId: Byte = 0
    var windowType: Byte = 15 //trading id
    var unknownVarInt1 // hardcoded to 0
            = 0
    var tradeTier = 0
    var trader: Long = 0
    var player: Long = 0
    var displayName: String? = null
    var screen2 = false
    var isWilling = false
    var offers: ByteArray

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
        this.putByte(windowId)
        this.putByte(windowType)
        this.putVarInt(unknownVarInt1)
        this.putVarInt(tradeTier)
        this.putEntityUniqueId(trader)
        this.putEntityUniqueId(player)
        this.putString(displayName)
        this.putBoolean(screen2)
        this.putBoolean(isWilling)
        this.put(offers)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.UPDATE_TRADE_PACKET
    }
}