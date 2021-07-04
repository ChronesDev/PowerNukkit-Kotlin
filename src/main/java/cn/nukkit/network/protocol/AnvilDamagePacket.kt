package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

@Since("1.4.0.0-PN")
@ToString
class AnvilDamagePacket : DataPacket() {
    @Since("1.4.0.0-PN")
    var damage = 0

    @Since("1.4.0.0-PN")
    var x = 0

    @Since("1.4.0.0-PN")
    var y = 0

    @Since("1.4.0.0-PN")
    var z = 0

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.ANVIL_DAMAGE_PACKET
    }

    @Override
    override fun decode() {
        damage = this.getByte()
        val vec: BlockVector3 = this.getBlockVector3()
        x = vec.x
        y = vec.y
        z = vec.z
    }

    @Override
    override fun encode() {
    }

    companion object {
        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        val NETWORK_ID: Byte = ProtocolInfo.ANVIL_DAMAGE_PACKET
    }
}