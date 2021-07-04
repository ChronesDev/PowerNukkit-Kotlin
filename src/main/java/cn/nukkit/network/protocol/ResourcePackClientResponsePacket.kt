package cn.nukkit.network.protocol

import lombok.ToString

@ToString
class ResourcePackClientResponsePacket : DataPacket() {
    var responseStatus: Byte = 0
    var packEntries: Array<Entry?>

    @Override
    override fun decode() {
        responseStatus = this.getByte() as Byte
        packEntries = arrayOfNulls(this.getLShort())
        for (i in packEntries.indices) {
            val entry: Array<String> = this.getString().split("_")
            packEntries[i] = Entry(UUID.fromString(entry[0]), entry[1])
        }
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(responseStatus)
        this.putLShort(packEntries.size)
        for (entry in packEntries) {
            this.putString(entry!!.uuid.toString() + '_' + entry.version)
        }
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @ToString
    class Entry(uuid: UUID, version: String) {
        val uuid: UUID
        val version: String

        init {
            this.uuid = uuid
            this.version = version
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET
        const val STATUS_REFUSED: Byte = 1
        const val STATUS_SEND_PACKS: Byte = 2
        const val STATUS_HAVE_ALL_PACKS: Byte = 3
        const val STATUS_COMPLETED: Byte = 4
    }
}