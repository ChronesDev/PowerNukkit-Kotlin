package cn.nukkit.level.particle

import cn.nukkit.math.Vector3

/**
 * @author xtypr
 * @since 2015/11/21
 */
class GenericParticle(pos: Vector3, id: Int, data: Int) : Particle(pos.x, pos.y, pos.z) {
    protected var id = 0
    protected val data: Int

    constructor(pos: Vector3, id: Int) : this(pos, id, 0) {}

    @Override
    override fun encode(): Array<DataPacket> {
        val pk = LevelEventPacket()
        pk.evid = (LevelEventPacket.EVENT_ADD_PARTICLE_MASK or id)
        pk.x = this.x as Float
        pk.y = this.y as Float
        pk.z = this.z as Float
        pk.data = data
        return arrayOf<DataPacket>(pk)
    }

    init {
        this.id = id
        this.data = data
    }
}