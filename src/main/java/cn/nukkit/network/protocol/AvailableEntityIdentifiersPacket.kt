package cn.nukkit.network.protocol

import cn.nukkit.Nukkit

@ToString(exclude = ["tag"])
class AvailableEntityIdentifiersPacket : DataPacket() {
    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET
        private val TAG: ByteArray

        init {
            try {
                val inputStream: InputStream = Nukkit::class.java.getClassLoader().getResourceAsStream("entity_identifiers.dat")
                if (cn.nukkit.network.protocol.inputStream == null) {
                    throw AssertionError("Could not find entity_identifiers.dat")
                }
                TAG = ByteStreams.toByteArray(cn.nukkit.network.protocol.inputStream)
            } catch (e: Exception) {
                throw AssertionError("Error whilst loading entity_identifiers.dat", e)
            }
        }
    }

    var tag = TAG

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        tag = this.get()
    }

    @Override
    override fun encode() {
        this.reset()
        this.put(tag)
    }
}