package cn.nukkit.dispenser

import cn.nukkit.block.Block

class BoatDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item): Item? {
        var pos: Vector3 = block.getSide(face).multiply(1.125)
        val target: Block = block.getSide(face)
        if (target is BlockWater) {
            pos.y += 1
        } else if (target.getId() !== BlockID.AIR || target.down() !is BlockWater) {
            return super.dispense(block, face, item)
        }
        pos = target.getLocation().setYaw(face.getHorizontalAngle())
        val boat = EntityBoat(block.level.getChunk(target.getChunkX(), target.getChunkZ()),
                Entity.getDefaultNBT(pos)
                        .putByte("woodID", (item as ItemBoat).getLegacyBoatDamage().orElse(0))
        )
        boat.spawnToAll()
        return null
    }
}