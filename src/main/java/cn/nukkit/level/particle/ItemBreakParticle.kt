package cn.nukkit.level.particle

import cn.nukkit.item.Item

/**
 * @author xtypr
 * @since 2015/11/21
 */
class ItemBreakParticle(pos: Vector3, item: Item) : Particle(pos.x, pos.y, pos.z) {
    private val data: Int

    @Override
    override fun encode(): Array<DataPacket> {
        val packet = LevelEventPacket()
        packet.evid = (LevelEventPacket.EVENT_ADD_PARTICLE_MASK or Particle.TYPE_ITEM_BREAK)
        packet.x = this.x as Float
        packet.y = this.y as Float
        packet.z = this.z as Float
        packet.data = data
        return arrayOf<DataPacket>(packet)
    }

    init {
        val networkFullId: Int = RuntimeItems.getRuntimeMapping().getNetworkFullId(item)
        val networkId: Int = RuntimeItems.getNetworkId(networkFullId)
        data = networkId shl 16 or item.getDamage()
    }
}