package cn.nukkit.network.protocol

import cn.nukkit.math.BlockVector3

@ToString
class CommandBlockUpdatePacket : DataPacket() {
    var isBlock = false
    var x = 0
    var y = 0
    var z = 0
    var commandBlockMode = 0
    var isRedstoneMode = false
    var isConditional = false
    var minecartEid: Long = 0
    var command: String? = null
    var lastOutput: String? = null
    var name: String? = null
    var shouldTrackOutput = false

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.COMMAND_BLOCK_UPDATE_PACKET
    }

    @Override
    override fun decode() {
        isBlock = this.getBoolean()
        if (isBlock) {
            val v: BlockVector3 = this.getBlockVector3()
            x = v.x
            y = v.y
            z = v.z
            commandBlockMode = this.getUnsignedVarInt() as Int
            isRedstoneMode = this.getBoolean()
            isConditional = this.getBoolean()
        } else {
            minecartEid = this.getEntityRuntimeId()
        }
        command = this.getString()
        lastOutput = this.getString()
        name = this.getString()
        shouldTrackOutput = this.getBoolean()
    }

    @Override
    override fun encode() {
        this.reset()
        this.putBoolean(isBlock)
        if (isBlock) {
            this.putBlockVector3(x, y, z)
            this.putUnsignedVarInt(commandBlockMode)
            this.putBoolean(isRedstoneMode)
            this.putBoolean(isConditional)
        } else {
            this.putEntityRuntimeId(minecartEid)
        }
        this.putString(command)
        this.putString(lastOutput)
        this.putString(name)
        this.putBoolean(shouldTrackOutput)
    }
}