package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class CompletedUsingItemPacket : DataPacket() {
    var itemId = 0
    var action = 0

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
        this.putLShort(itemId)
        this.putLInt(action)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.COMPLETED_USING_ITEM_PACKET
        const val ACTION_UNKNOWN = -1
        const val ACTION_EQUIP_ARMOR = 0
        const val ACTION_EAT = 1
        const val ACTION_ATTACK = 2
        const val ACTION_CONSUME = 3
        const val ACTION_THROW = 4
        const val ACTION_SHOOT = 5
        const val ACTION_PLACE = 6
        const val ACTION_FILL_BOTTLE = 7
        const val ACTION_FILL_BUCKET = 8
        const val ACTION_POUR_BUCKET = 9
        const val ACTION_USE_TOOL = 10
        const val ACTION_INTERACT = 11
        const val ACTION_RETRIEVE = 12
        const val ACTION_DYED = 13
        const val ACTION_TRADED = 14
    }
}