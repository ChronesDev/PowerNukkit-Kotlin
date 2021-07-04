package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ModalFormRequestPacket : DataPacket() {
    var formId = 0
    var data: String? = null

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.MODAL_FORM_REQUEST_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(formId)
        this.putString(data)
    }
}