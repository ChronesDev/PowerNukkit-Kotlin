package cn.nukkit.dispenser

import cn.nukkit.block.BlockDispenser

class SpawnEggDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item): Item? {
        val pos: Vector3 = block.getSide(face).add(0.5, 0.7, 0.5)
        val entity: Entity = Entity.createEntity((item as ItemSpawnEgg).getEntityNetworkId(), block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos))
        this.success = entity != null
        if (this.success) {
            if (item.hasCustomName() && entity is EntityLiving) {
                entity.setNameTag(item.getCustomName())
            }
            entity.spawnToAll()
            return null
        }
        return super.dispense(block, face, item)
    }
}