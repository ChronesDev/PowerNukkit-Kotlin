package cn.nukkit.positiontracking

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class NamedPosition : Vector3 {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(x: Double) : super(x) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(x: Double, y: Double) : super(x, y) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(x: Double, y: Double, z: Double) : super(x, y, z) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    abstract fun getLevelName(): String
    fun matchesNamedPosition(position: NamedPosition): Boolean {
        return x === position.x && y === position.y && z === position.z && getLevelName().equals(position.getLevelName())
    }

    @Override
    fun clone(): NamedPosition {
        return super.clone() as NamedPosition
    }
}