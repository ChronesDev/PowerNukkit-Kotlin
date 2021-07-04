package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
class PositionTrackingDBClientRequestPacket : DataPacket() {
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    var action: Action? = null
        private set

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    var trackingId = 0
        private set

    @Override
    override fun encode() {
        reset()
        putByte(action!!.ordinal() as Byte)
        putVarInt(trackingId)
    }

    @Override
    override fun decode() {
        val aByte: Int = getByte()
        action = ACTIONS[aByte]
        trackingId = getVarInt()
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    enum class Action {
        QUERY
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NETWORK_ID: Byte = ProtocolInfo.POS_TRACKING_CLIENT_REQUEST_PACKET
        private val ACTIONS = Action.values()
    }
}