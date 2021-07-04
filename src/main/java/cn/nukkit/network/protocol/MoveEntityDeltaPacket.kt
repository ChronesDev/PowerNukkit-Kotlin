package cn.nukkit.network.protocol

import cn.nukkit.api.DeprecationDetails

@ToString
class MoveEntityDeltaPacket : DataPacket() {
    var flags = 0

    @Since("1.4.0.0-PN")
    var x = 0f

    @Since("1.4.0.0-PN")
    var y = 0f

    @Since("1.4.0.0-PN")
    var z = 0f

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Changed to float", replaceWith = "x")
    @PowerNukkitOnly("Re-added for backward-compatibility")
    var xDelta = 0

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Changed to float", replaceWith = "y")
    @PowerNukkitOnly("Re-added for backward-compatibility")
    var yDelta = 0

    @PowerNukkitOnly("Re-added for backward-compatibility")
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Changed to float", replaceWith = "z")
    var zDelta = 0
    private var xDecoded = 0
    private var yDecoded = 0
    private var zDecoded = 0
    var yawDelta = 0.0
    var headYawDelta = 0.0
    var pitchDelta = 0.0

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        flags = this.getByte()
        x = getCoordinate(FLAG_HAS_X)
        y = getCoordinate(FLAG_HAS_Y)
        z = getCoordinate(FLAG_HAS_Z)
        yawDelta = getRotation(FLAG_HAS_YAW)
        headYawDelta = getRotation(FLAG_HAS_HEAD_YAW)
        pitchDelta = getRotation(FLAG_HAS_PITCH)
        xDecoded = NukkitMath.floorFloat(x)
        xDelta = xDecoded
        yDecoded = NukkitMath.floorFloat(y)
        yDelta = yDecoded
        zDecoded = NukkitMath.floorFloat(z)
        zDelta = zDecoded
    }

    @Override
    override fun encode() {
        this.putByte(flags.toByte())
        var x = x
        var y = y
        var z = z
        if (xDelta != xDecoded || yDelta != yDecoded || zDelta != zDecoded) {
            x = xDelta.toFloat()
            y = yDelta.toFloat()
            z = zDelta.toFloat()
        }
        putCoordinate(FLAG_HAS_X, x)
        putCoordinate(FLAG_HAS_Y, y)
        putCoordinate(FLAG_HAS_Z, z)
        putRotation(FLAG_HAS_YAW, yawDelta)
        putRotation(FLAG_HAS_HEAD_YAW, headYawDelta)
        putRotation(FLAG_HAS_PITCH, pitchDelta)
    }

    private fun getCoordinate(flag: Int): Float {
        return if (flags and flag != 0) {
            this.getLFloat()
        } else 0
    }

    private fun getRotation(flag: Int): Double {
        return if (flags and flag != 0) {
            this.getByte() * (360.0 / 256.0)
        } else 0.0
    }

    private fun putCoordinate(flag: Int, value: Float) {
        if (flags and flag != 0) {
            this.putLFloat(value)
        }
    }

    private fun putRotation(flag: Int, value: Double) {
        if (flags and flag != 0) {
            this.putByte((value / (360.0 / 256.0)).toByte())
        }
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET
        const val FLAG_HAS_X = 1
        const val FLAG_HAS_Y = 2
        const val FLAG_HAS_Z = 4
        const val FLAG_HAS_YAW = 8
        const val FLAG_HAS_HEAD_YAW = 16
        const val FLAG_HAS_PITCH = 32
    }
}