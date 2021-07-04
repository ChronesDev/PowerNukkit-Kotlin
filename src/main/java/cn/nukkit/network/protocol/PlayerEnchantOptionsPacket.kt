package cn.nukkit.network.protocol

import cn.nukkit.api.DeprecationDetails

@Since("1.3.0.0-PN")
@ToString
class PlayerEnchantOptionsPacket : DataPacket() {
    @Since("1.3.0.0-PN")
    val options: List<EnchantOptionData> = ArrayList()

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        val size = this.getUnsignedVarInt() as Int
        for (i in 0 until size) {
            val minLevel: Int = this.getVarInt()
            val slot: Int = this.getInt()
            var eSize = this.getUnsignedVarInt() as Int
            val list1: List<EnchantData> = ObjectArrayList()
            for (j in 0 until eSize) {
                val data: EnchantData = EnchantData(this.getByte(), this.getByte())
                list1.add(data)
            }
            eSize = this.getUnsignedVarInt() as Int
            val list2: List<EnchantData> = ObjectArrayList()
            for (j in 0 until eSize) {
                val data: EnchantData = EnchantData(this.getByte(), this.getByte())
                list2.add(data)
            }
            eSize = this.getUnsignedVarInt() as Int
            val list3: List<EnchantData> = ObjectArrayList()
            for (j in 0 until eSize) {
                val data: EnchantData = EnchantData(this.getByte(), this.getByte())
                list3.add(data)
            }
            val enchantName: String = this.getString()
            val eNetId = this.getUnsignedVarInt() as Int
            options.add(EnchantOptionData(minLevel, slot, list1, list2, list3, enchantName, eNetId))
        }
    }

    @Override
    override fun encode() {
        this.reset()
        this.putUnsignedVarInt(options.size())
        for (option in options) {
            this.putVarInt(option.getMinLevel())
            this.putInt(option.getPrimarySlot())
            this.putUnsignedVarInt(option.getEnchants0().size())
            for (data in option.getEnchants0()) {
                this.putByte(data.getType() as Byte)
                this.putByte(data.getLevel() as Byte)
            }
            this.putUnsignedVarInt(option.getEnchants1().size())
            for (data in option.getEnchants1()) {
                this.putByte(data.getType() as Byte)
                this.putByte(data.getLevel() as Byte)
            }
            this.putUnsignedVarInt(option.getEnchants2().size())
            for (data in option.getEnchants2()) {
                this.putByte(data.getType() as Byte)
                this.putByte(data.getLevel() as Byte)
            }
            this.putString(option.getEnchantName())
            this.putUnsignedVarInt(option.getEnchantNetId())
        }
    }

    @Since("1.3.0.0-PN")
    @Value
    inner class EnchantOptionData {
        @Since("1.3.1.0-PN")
        private val minLevel = 0
        private val primarySlot = 0
        private val enchants0: List<EnchantData>? = null
        private val enchants1: List<EnchantData>? = null
        private val enchants2: List<EnchantData>? = null
        private val enchantName: String? = null
        private val enchantNetId = 0
    }

    @Since("1.3.0.0-PN")
    @Value
    inner class EnchantData {
        private val type = 0
        private val level = 0
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.PLAYER_ENCHANT_OPTIONS_PACKET
    }
}