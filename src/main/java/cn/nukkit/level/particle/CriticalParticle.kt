package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

/**
 * @author xtypr
 * @since 2015/11/21
 */
class CriticalParticle(pos: Vector3, scale: Int) : GenericParticle(pos, Particle.TYPE_CRITICAL, scale) {
    constructor(pos: Vector3) : this(pos, 2) {}
}