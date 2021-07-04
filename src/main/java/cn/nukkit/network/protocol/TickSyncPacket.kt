package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("1.5.0.0-PN")
@ToString
class TickSyncPacket : DataPacket() {
    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var requestTimestamp: Long = 0

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var responseTimestamp: Long = 0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        requestTimestamp = this.getLLong()
        responseTimestamp = this.getLLong()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putLLong(requestTimestamp)
        this.putLLong(responseTimestamp)
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val NETWORK_ID: Byte = ProtocolInfo.TICK_SYNC_PACKET
    }
}