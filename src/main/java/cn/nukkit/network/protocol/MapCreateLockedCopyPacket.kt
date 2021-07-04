package cn.nukkit.network.protocol

import kotlin.jvm.Volatile
import kotlin.Throws
import cn.nukkit.network.protocol.types.CommandOriginData.Origin
import CommandOriginData.Origin
import cn.nukkit.network.protocol.ItemStackRequestPacket.Request
import kotlin.jvm.Synchronized
import kotlin.jvm.JvmOverloads

class MapCreateLockedCopyPacket : DataPacket() {
    var originalMapId: Long = 0
    var newMapId: Long = 0

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.MAP_CREATE_LOCKED_COPY_PACKET
    }

    @Override
    override fun decode() {
        originalMapId = this.getVarLong()
        newMapId = this.getVarLong()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarLong(originalMapId)
        this.putVarLong(newMapId)
    }
}