package cn.nukkit.network.protocol

import cn.nukkit.network.protocol.types.ContainerIds

@ToString
class PlayerHotbarPacket : DataPacket() {
    var selectedHotbarSlot = 0
    var windowId: Int = ContainerIds.INVENTORY
    var selectHotbarSlot = true

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.PLAYER_HOTBAR_PACKET
    }

    @Override
    override fun decode() {
        selectedHotbarSlot = this.getUnsignedVarInt() as Int
        windowId = this.getByte()
        selectHotbarSlot = this.getBoolean()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putUnsignedVarInt(selectedHotbarSlot)
        this.putByte(windowId.toByte())
        this.putBoolean(selectHotbarSlot)
    }
}