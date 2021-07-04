package cn.nukkit.network.protocol

import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.network.protocol.types.CommandOriginData.Origin
import CommandOriginData.Origin
import cn.nukkit.network.protocol.ItemStackRequestPacket.Request
import kotlin.jvm.Synchronized
import kotlin.jvm.JvmOverloads

/**
 * @author MagicDroidX (Nukkit Project)
 */
class BatchPacket : DataPacket() {
    var payload: ByteArray

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        payload = this.get()
    }

    @Override
    override fun encode() {
    }

    fun trim() {
        setBuffer(null)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.BATCH_PACKET
    }
}