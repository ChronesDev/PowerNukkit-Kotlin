package cn.nukkit.dispenser

import cn.nukkit.block.BlockDispenser

/**
 * @author CreeperFace
 */
class DefaultDispenseBehavior : DispenseBehavior {
    var success = true

    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item): Item? {
        val dispensePos: Vector3 = block.getDispensePosition()
        if (face.getAxis() === Axis.Y) {
            dispensePos.y -= 0.125
        } else {
            dispensePos.y -= 0.15625
        }
        val rand: Random = ThreadLocalRandom.current()
        val motion = Vector3()
        val offset: Double = rand.nextDouble() * 0.1 + 0.2
        motion.x = face.getXOffset() * offset
        motion.y = 0.20000000298023224
        motion.z = face.getZOffset() * offset
        motion.x += rand.nextGaussian() * 0.007499999832361937 * 6
        motion.y += rand.nextGaussian() * 0.007499999832361937 * 6
        motion.z += rand.nextGaussian() * 0.007499999832361937 * 6
        val clone: Item = item.clone()
        clone.count = 1
        block.level.dropItem(dispensePos, clone, motion)
        return null
    }

    private fun getParticleMetadataForFace(face: BlockFace): Int {
        return face.getXOffset() + 1 + (face.getZOffset() + 1) * 3
    }
}