package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.Block

abstract class TreeGenerator : cn.nukkit.level.generator.`object`.BasicGenerator() {
    /*
     * returns whether or not a tree can grow into a block
     * For example, a tree will not grow into stone
     */
    protected fun canGrowInto(id: Int): Boolean {
        return id == Item.AIR || id == Item.LEAVES || id == Item.GRASS || id == Item.DIRT || id == Item.LOG || id == Item.LOG2 || id == Item.SAPLING || id == Item.VINE
    }

    fun generateSaplings(level: Level?, random: Random?, pos: Vector3?) {}
    protected fun setDirtAt(level: ChunkManager?, pos: BlockVector3) {
        setDirtAt(level, Vector3(pos.x, pos.y, pos.z))
    }

    /*
     * sets dirt at a specific location if it isn't already dirt
     */
    protected fun setDirtAt(level: ChunkManager, pos: Vector3) {
        if (level.getBlockIdAt(pos.x as Int, pos.y as Int, pos.z as Int) !== Item.DIRT) {
            this.setBlockAndNotifyAdequately(level, pos, Block.get(BlockID.DIRT))
        }
    }
}