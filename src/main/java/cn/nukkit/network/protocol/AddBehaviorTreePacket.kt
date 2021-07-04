package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class AddBehaviorTreePacket : DataPacket() {
    var unknown: String? = null

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.ADD_BEHAVIOR_TREE_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putString(unknown)
    }
}