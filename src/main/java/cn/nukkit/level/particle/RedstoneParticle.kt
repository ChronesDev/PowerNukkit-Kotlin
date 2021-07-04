package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

/**
 * @author xtypr
 * @since 2015/11/21
 */
class RedstoneParticle(pos: Vector3, lifetime: Int) : GenericParticle(pos, Particle.TYPE_REDSTONE, lifetime) {
    constructor(pos: Vector3) : this(pos, 1) {}
}