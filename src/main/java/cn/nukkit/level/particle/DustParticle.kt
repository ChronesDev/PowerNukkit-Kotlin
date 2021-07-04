package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

/**
 * @author xtypr
 * @since 2015/11/21
 */
class DustParticle(pos: Vector3, r: Int, g: Int, b: Int, a: Int) : GenericParticle(pos, Particle.TYPE_DUST, a and 0xff shl 24 or (r and 0xff shl 16) or (g and 0xff shl 8) or (b and 0xff)) {
    constructor(pos: Vector3, blockColor: BlockColor) : this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue(), blockColor.getAlpha()) {}
    constructor(pos: Vector3, r: Int, g: Int, b: Int) : this(pos, r, g, b, 255) {}
}