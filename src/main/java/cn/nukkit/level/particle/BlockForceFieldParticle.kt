package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

class BlockForceFieldParticle(pos: Vector3, scale: Int) : GenericParticle(pos, Particle.TYPE_BLOCK_FORCE_FIELD) {
    constructor(pos: Vector3) : this(pos, 0) {}
}