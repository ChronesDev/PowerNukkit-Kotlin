package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

/**
 * @author CreeperFace
 * @since 15.4.2017
 */
class BoneMealParticle(pos: Vector3) : Particle(pos.x, pos.y, pos.z) {
    private val position: Vector3? = null

    @Override
    override fun encode(): Array<DataPacket> {
        val pk = LevelEventPacket()
        pk.evid = LevelEventPacket.EVENT_PARTICLE_BONEMEAL
        pk.x = this.x as Float
        pk.y = this.y as Float
        pk.z = this.z as Float
        pk.data = 0
        return arrayOf<DataPacket>(pk)
    }
}