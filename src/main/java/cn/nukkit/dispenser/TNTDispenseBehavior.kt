package cn.nukkit.dispenser

import cn.nukkit.block.BlockDispenser

class TNTDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace?, item: Item?): Item? {
        val pos: Vector3 = block.getSide(face).add(0.5, 0, 0.5)
        val tnt = EntityPrimedTNT(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos))
        tnt.spawnToAll()
        return null
    }
}