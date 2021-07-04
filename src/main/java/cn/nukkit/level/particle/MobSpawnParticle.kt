package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

/**
 * @author xtypr
 * @since 2015/11/21
 */
class MobSpawnParticle(pos: Vector3, width: Float, height: Float) : Particle(pos.x, pos.y, pos.z) {
    protected val width: Int
    protected val height: Int

    @Override
    override fun encode(): Array<DataPacket> {
        val packet = LevelEventPacket()
        packet.evid = LevelEventPacket.EVENT_PARTICLE_SPAWN
        packet.x = this.x as Float
        packet.y = this.y as Float
        packet.z = this.z as Float
        packet.data = (width and 0xff) + (height and 0xff shl 8)
        return arrayOf<DataPacket>(packet)
    }

    init {
        this.width = width.toInt()
        this.height = height.toInt()
    }
}