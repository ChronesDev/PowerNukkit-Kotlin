package cn.nukkit.network.protocol

import cn.nukkit.Nukkit

@ToString(exclude = "tag")
class BiomeDefinitionListPacket : DataPacket() {
    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.BIOME_DEFINITION_LIST_PACKET
        private val TAG: ByteArray

        init {
            try {
                val inputStream: InputStream = Nukkit::class.java.getClassLoader().getResourceAsStream("biome_definitions.dat")
                if (cn.nukkit.network.protocol.inputStream == null) {
                    throw AssertionError("Could not find biome_definitions.dat")
                }
                TAG = ByteStreams.toByteArray(cn.nukkit.network.protocol.inputStream)
            } catch (e: Exception) {
                throw AssertionError("Error whilst loading biome_definitions.dat", e)
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
    }

    @Override
    override fun encode() {
        this.reset()
        this.put(tag)
    }
}