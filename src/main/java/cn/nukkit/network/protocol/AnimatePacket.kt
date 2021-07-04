package cn.nukkit.network.protocol

import it.unimi.dsi.fastutil.ints.Int2ObjectMap

/**
 * @author Nukkit Project Team
 */
@ToString
class AnimatePacket : DataPacket() {
    var eid: Long = 0
    var action: Action? = null
    var rowingTime = 0f

    @Override
    override fun decode() {
        action = Action.fromId(this.getVarInt())
        eid = getEntityRuntimeId()
        if (action == Action.ROW_RIGHT || action == Action.ROW_LEFT) {
            rowingTime = this.getLFloat()
        }
    }

    @Override
    override fun encode() {
        this.reset()
        this.putVarInt(action!!.id)
        this.putEntityRuntimeId(eid)
        if (action == Action.ROW_RIGHT || action == Action.ROW_LEFT) {
            this.putLFloat(rowingTime)
        }
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    enum class Action(val id: Int) {
        NO_ACTION(0), SWING_ARM(1), WAKE_UP(3), CRITICAL_HIT(4), MAGIC_CRITICAL_HIT(5), ROW_RIGHT(128), ROW_LEFT(129);

        companion object {
            private val ID_LOOKUP: Int2ObjectMap<Action> = Int2ObjectOpenHashMap()
            fun fromId(id: Int): Action {
                return ID_LOOKUP.get(id)
            }

            init {
                for (value in values()) {
                    ID_LOOKUP.put(cn.nukkit.network.protocol.value.id, cn.nukkit.network.protocol.value)
                }
            }
        }

    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.ANIMATE_PACKET
    }
}