package cn.nukkit.network.protocol

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString
class PositionTrackingDBServerBroadcastPacket : DataPacket() {
    private var action: Action? = null
    private var trackingId = 0
    private var tag: CompoundTag? = null
    private fun requireTag(): CompoundTag? {
        if (tag == null) {
            tag = CompoundTag()
                    .putByte("version", 1)
                    .putString("id", String.format("0x%08x", trackingId))
        }
        return tag
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setAction(action: Action?) {
        this.action = action
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setTrackingId(trackingId: Int) {
        this.trackingId = trackingId
        if (tag != null) {
            tag.putString("id", String.format("0x%08x", trackingId))
        }
    }

    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var position: BlockVector3?
        get() {
            if (tag == null) {
                return null
            }
            val pos: ListTag<IntTag> = tag.getList("pos", IntTag::class.java)
            return if (pos == null || pos.size() !== 3) {
                null
            } else BlockVector3(pos.get(0).data, pos.get(1).data, pos.get(2).data)
        }
        set(position) {
            setPosition(position.x, position.y, position.z)
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setPosition(position: Vector3) {
        setPosition(position.getFloorX(), position.getFloorY(), position.getFloorZ())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun setPosition(x: Int, y: Int, z: Int) {
        requireTag().putList(ListTag("pos")
                .add(IntTag("", x))
                .add(IntTag("", y))
                .add(IntTag("", z))
        )
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var status: Int
        get() = if (tag == null) {
            0
        } else tag.getByte("status")
        set(status) {
            requireTag().putByte("status", status)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var version: Int
        get() = if (tag == null) {
            0
        } else tag.getByte("version")
        set(status) {
            requireTag().putByte("version", status)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var dimension: Int
        get() = if (tag == null) {
            0
        } else tag.getByte("dim")
        set(dimension) {
            requireTag().putInt("dim", dimension)
        }

    @Override
    override fun encode() {
        reset()
        putByte(action!!.ordinal() as Byte)
        putVarInt(trackingId)
        try {
            put(NBTIO.writeNetwork(if (tag != null) tag else CompoundTag()))
        } catch (e: IOException) {
            throw EncoderException(e)
        }
    }

    @Override
    override fun decode() {
        action = ACTIONS[getByte()]
        trackingId = getVarInt()
        try {
            FastByteArrayInputStream(get()).use { inputStream -> tag = NBTIO.readNetworkCompressed(inputStream) }
        } catch (e: IOException) {
            throw EncoderException(e)
        }
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun clone(): PositionTrackingDBServerBroadcastPacket? {
        return super.clone() as PositionTrackingDBServerBroadcastPacket?
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    enum class Action {
        UPDATE, DESTROY, NOT_FOUND
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NETWORK_ID: Byte = ProtocolInfo.POS_TRACKING_SERVER_BROADCAST_PACKET

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        private val ACTIONS = Action.values()
    }
}