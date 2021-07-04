package cn.nukkit.level.particle

import cn.nukkit.block.Block

class PunchBlockParticle(pos: Vector3, blockId: Int, blockDamage: Int, face: BlockFace) : Particle(pos.x, pos.y, pos.z) {
    protected val data: Int

    constructor(pos: Vector3, block: Block, face: BlockFace) : this(pos, block.getId(), block.getDamage(), face) {}

    @Override
    override fun encode(): Array<DataPacket> {
        val pk = LevelEventPacket()
        pk.evid = LevelEventPacket.EVENT_PARTICLE_PUNCH_BLOCK
        pk.x = this.x as Float
        pk.y = this.y as Float
        pk.z = this.z as Float
        pk.data = data
        return arrayOf<DataPacket>(pk)
    }

    init {
        data = GlobalBlockPalette.getOrCreateRuntimeId(blockId, blockDamage) or (face.getIndex() shl 24)
    }
}