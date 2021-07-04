package cn.nukkit.network.protocol

import lombok.ToString

/**
 * @author MagicDroidX (Nukkit Project)
 */
@ToString
class ContainerSetDataPacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var windowId = 0
    var property = 0
    var value = 0

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putByte(windowId.toByte())
        this.putVarInt(property)
        this.putVarInt(value)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.CONTAINER_SET_DATA_PACKET
        const val PROPERTY_FURNACE_TICK_COUNT = 0
        const val PROPERTY_FURNACE_LIT_TIME = 1
        const val PROPERTY_FURNACE_LIT_DURATION = 2

        //TODO: check property 3
        const val PROPERTY_FURNACE_FUEL_AUX = 4
        const val PROPERTY_BREWING_STAND_BREW_TIME = 0
        const val PROPERTY_BREWING_STAND_FUEL_AMOUNT = 1
        const val PROPERTY_BREWING_STAND_FUEL_TOTAL = 2
    }
}