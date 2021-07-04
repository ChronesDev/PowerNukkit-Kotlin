package cn.nukkit.level.particle

import cn.nukkit.block.Block

/**
 * @author xtypr
 * @since 2015/11/21
 */
class DestroyBlockParticle(pos: Vector3, block: Block) : Particle(pos.x, pos.y, pos.z) {
    protected val data: Int

    @Override
    override fun encode(): Array<DataPacket> {
        val pk = LevelEventPacket()
        pk.evid = LevelEventPacket.EVENT_PARTICLE_DESTROY
        pk.x = this.x as Float
        pk.y = this.y as Float
        pk.z = this.z as Float
        pk.data = data
        return arrayOf<DataPacket>(pk)
    }

    init {
        data = GlobalBlockPalette.getOrCreateRuntimeId(block.getId(), block.getDamage())
    }
}