package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class GUIDataPickItemPacket : DataPacket() {
    var hotbarSlot = 0

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET
    }

    @Override
    override fun encode() {
        this.reset()
        this.putLInt(hotbarSlot)
    }

    @Override
    override fun decode() {
        hotbarSlot = this.getLInt()
    }
}