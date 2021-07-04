package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author CreeperFace
 * @since 5.3.2017
 */
@ToString
class ClientboundMapItemDataPacket : DataPacket() {
    //TODO: update to 1.2
    var eids: IntArray = EmptyArrays.EMPTY_INTS
    var mapId: Long = 0
    var update = 0
    var scale: Byte = 0
    var isLocked = false
    var width = 0
    var height = 0
    var offsetX = 0
    var offsetZ = 0
    var dimensionId: Byte = 0
    var decorators = MapDecorator.EMPTY_ARRAY
    var colors: IntArray = EmptyArrays.EMPTY_INTS
    var image: BufferedImage? = null

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET
    }

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityUniqueId(mapId)
        var update = 0
        if (eids.size > 0) {
            update = update or 0x08
        }
        if (decorators.size > 0) {
            update = update or DECORATIONS_UPDATE
        }
        if (image != null || colors.size > 0) {
            update = update or TEXTURE_UPDATE
        }
        this.putUnsignedVarInt(update)
        this.putByte(dimensionId)
        this.putBoolean(isLocked)
        if (update and 0x08 != 0) { //TODO: find out what these are for
            this.putUnsignedVarInt(eids.size)
            for (eid in eids) {
                this.putEntityUniqueId(eid)
            }
        }
        if (update and (TEXTURE_UPDATE or DECORATIONS_UPDATE) != 0) {
            this.putByte(scale)
        }
        if (update and DECORATIONS_UPDATE != 0) {
            this.putUnsignedVarInt(decorators.size)
            for (decorator in decorators) {
                this.putByte(decorator!!.rotation)
                this.putByte(decorator!!.icon)
                this.putByte(decorator!!.offsetX)
                this.putByte(decorator!!.offsetZ)
                this.putString(decorator!!.label)
                this.putVarInt(decorator!!.color.getRGB())
            }
        }
        if (update and TEXTURE_UPDATE != 0) {
            this.putVarInt(width)
            this.putVarInt(height)
            this.putVarInt(offsetX)
            this.putVarInt(offsetZ)
            this.putUnsignedVarInt(width * height)
            if (image != null) {
                for (y in 0 until width) {
                    for (x in 0 until height) {
                        putUnsignedVarInt(Utils.toABGR(image.getRGB(x, y)))
                    }
                }
                image.flush()
            } else if (colors.size > 0) {
                for (color in colors) {
                    putUnsignedVarInt(color)
                }
            }
        }
    }

    class MapDecorator {
        var rotation: Byte = 0
        var icon: Byte = 0
        var offsetX: Byte = 0
        var offsetZ: Byte = 0
        var label: String? = null
        var color: Color? = null

        companion object {
            @PowerNukkitOnly
            @Since("1.4.0.0-PN")
            val EMPTY_ARRAY = arrayOfNulls<MapDecorator>(0)
        }
    }

    companion object {
        //update
        const val TEXTURE_UPDATE = 2
        const val DECORATIONS_UPDATE = 4
        const val ENTITIES_UPDATE = 8
    }
}