package cn.nukkit.network.protocol

import cn.nukkit.api.Since

@Since("1.4.0.0-PN")
@ToString
class ItemStackRequestPacket : DataPacket() {
    @Since("1.4.0.0-PN")
    val requests: List<Request> = ArrayList()

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.ITEM_STACK_REQUEST_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
    }

    @Since("1.4.0.0-PN")
    @Value
    class Request {
        private val requestId = 0
        private val actions: List<ItemStackAction>? = null
    }

    @Since("1.4.0.0-PN")
    @Value
    class ItemStackAction {
        private val type: Byte = 0
        private val bool0 = false
        private val byte0: Byte = 0
        private val varInt0 = 0
        private val varInt1 = 0
        private val baseByte0: Byte = 0
        private val baseByte1: Byte = 0
        private val baseByte2: Byte = 0
        private val baseVarInt0 = 0
        private val flagsByte0: Byte = 0
        private val flagsByte1: Byte = 0
        private val flagsVarInt0 = 0
        private val items: List<Item>? = null

        @Override
        override fun toString(): String {
            val joiner = StringJoiner(", ")
            joiner.add("type=$type")
            when (type) {
                0, 1, 2 -> joiner.add("baseByte0=$baseByte0")
                        .add("baseByte1=$baseByte1")
                        .add("baseByte2=$baseByte2")
                        .add("baseVarInt0=$baseVarInt0")
                        .add("flagsByte0=$flagsByte0")
                        .add("flagsByte1=$flagsByte1")
                        .add("flagsVarInt0=$flagsVarInt0")
                3 -> joiner.add("bool0=$bool0")
                        .add("baseByte0=$baseByte0")
                        .add("baseByte1=$baseByte1")
                        .add("baseByte2=$baseByte2")
                        .add("baseVarInt0=$baseVarInt0")
                4, 5 -> joiner.add("baseByte0=$baseByte0")
                        .add("baseByte1=$baseByte1")
                        .add("baseByte2=$baseByte2")
                        .add("baseVarInt0=$baseVarInt0")
                6 -> joiner.add("byte0=$byte0")
                8 -> joiner.add("varInt0=$varInt0")
                        .add("varInt1=$varInt1")
                10, 11, 12, 13, 14, 15 -> joiner.add("varInt0=$varInt0")
                17 -> joiner.add("items=$items")
            }
            return "ItemStackAction(" + joiner.toString().toString() + ")"
        }
    }
}