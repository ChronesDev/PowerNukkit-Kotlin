package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

class CloudParticle(pos: Vector3, scale: Int) : GenericParticle(pos, Particle.TYPE_EVAPORATION, scale) {
    constructor(pos: Vector3) : this(pos, 0) {}
}