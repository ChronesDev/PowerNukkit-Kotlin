package cn.nukkit.dispenser

import cn.nukkit.block.BlockDispenser

class FireworksDispenseBehavior : DefaultDispenseBehavior() {
    @Override
    override fun dispense(block: BlockDispenser, face: BlockFace, item: Item?): Item? {
        val opposite: BlockFace = face.getOpposite()
        val pos: Vector3 = block.getSide(face).add(0.5 + opposite.getXOffset() * 0.2, 0.5 + opposite.getYOffset() * 0.2, 0.5 + opposite.getZOffset() * 0.2)
        val nbt: CompoundTag = Entity.getDefaultNBT(pos)
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item))
        val firework = EntityFirework(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()), nbt)
        firework.spawnToAll()
        return null
    }
}