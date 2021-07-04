package cn.nukkit.block

import cn.nukkit.entity.Entity

/**
 * @author rcsuperman (Nukkit Project)
 */
abstract class BlockFallable protected constructor() : BlockSolid() {
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val down: Block = this.down()
            if (down.getId() === AIR || down is BlockFire || down is BlockLiquid || down.getLevelBlockAtLayer(1) is BlockLiquid) {
                val event = BlockFallEvent(this)
                this.level.getServer().getPluginManager().callEvent(event)
                if (event.isCancelled()) {
                    return type
                }
                this.level.setBlock(this, Block.get(Block.AIR), true, true)
                val fall: EntityFallingBlock? = createFallingEntity(CompoundTag())
                fall.spawnToAll()
            }
        }
        return type
    }

    protected fun createFallingEntity(customNbt: CompoundTag): EntityFallingBlock? {
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", this.x + 0.5))
                        .add(DoubleTag("", this.y))
                        .add(DoubleTag("", this.z + 0.5)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("", 0))
                        .add(DoubleTag("", 0))
                        .add(DoubleTag("", 0)))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("", 0))
                        .add(FloatTag("", 0)))
                .putInt("TileID", this.getId())
                .putByte("Data", this.getDamage())
        for (customTag in customNbt.getAllTags()) {
            nbt.put(customTag.getName(), customTag.copy())
        }
        val fall: EntityFallingBlock = Entity.createEntity("FallingSand", this.getLevel().getChunk(this.x as Int shr 4, this.z as Int shr 4), nbt) as EntityFallingBlock
        if (fall != null) {
            fall.spawnToAll()
        }
        return fall
    }
}