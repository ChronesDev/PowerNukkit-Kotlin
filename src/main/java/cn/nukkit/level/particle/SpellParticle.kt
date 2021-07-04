package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

/**
 * @author xtypr
 * @since 2015/12/27
 * The name "spell" comes from minecraft wiki.
 */
class SpellParticle(pos: Vector3, protected val data: Int) : Particle(pos.x, pos.y, pos.z) {
    constructor(pos: Vector3?) : this(pos, 0) {}
    constructor(pos: Vector3?, blockColor: BlockColor) : this(pos, blockColor.getRed(), blockColor.getGreen(), blockColor.getBlue()) {
        //alpha is ignored
    }

    constructor(pos: Vector3?, r: Int, g: Int, b: Int) : this(pos, r, g, b, 0x00) {}
    protected constructor(pos: Vector3?, r: Int, g: Int, b: Int, a: Int) : this(pos, a and 0xff shl 24 or (r and 0xff shl 16) or (g and 0xff shl 8) or (b and 0xff)) {}

    @Override
    override fun encode(): Array<DataPacket> {
        val pk = LevelEventPacket()
        pk.evid = LevelEventPacket.EVENT_PARTICLE_SPLASH
        pk.x = this.x as Float
        pk.y = this.y as Float
        pk.z = this.z as Float
        pk.data = data
        return arrayOf<DataPacket>(pk)
    }
}