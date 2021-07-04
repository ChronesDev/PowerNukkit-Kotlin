package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author GoodLucky777
 */
@ToString
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemComponentPacket : DataPacket() {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    private var entries: Array<Entry?>? = Entry.EMPTY_ARRAY
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setEntries(entries: Array<Entry?>?) {
        this.entries = if (entries == null) null else if (entries.size == 0) Entry.EMPTY_ARRAY else entries.clone()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getEntries(): Array<Entry>? {
        return if (entries == null) null else if (entries!!.size == 0) Entry.EMPTY_ARRAY else entries.clone()
    }

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
        this.putUnsignedVarInt(entries!!.size)
        try {
            for (entry in entries!!) {
                this.putString(entry!!.name)
                this.put(NBTIO.write(entry!!.getData(), ByteOrder.LITTLE_ENDIAN, true))
            }
        } catch (e: IOException) {
            MainLogger.getLogger().error("Error while encoding NBT data of ItemComponentPacket", e)
        }
    }

    @ToString
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    class Entry @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(@get:PowerNukkitOnly
                                                                  @get:Since("1.4.0.0-PN") val name: String, data: CompoundTag) {
        private val data: CompoundTag
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun getData(): CompoundTag {
            return data
        }

        companion object {
            @PowerNukkitOnly
            @Since("1.4.0.0-PN")
            val EMPTY_ARRAY = arrayOfNulls<Entry>(0)
        }

        init {
            this.data = data
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NETWORK_ID: Byte = ProtocolInfo.ITEM_COMPONENT_PACKET
    }
}