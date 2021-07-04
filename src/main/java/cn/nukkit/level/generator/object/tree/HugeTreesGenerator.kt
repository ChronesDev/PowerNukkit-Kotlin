package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.Block

abstract class HugeTreesGenerator(
        /**
         * The base height of the tree
         */
        protected val baseHeight: Int, protected var extraRandomHeight: Int, woodMetadataIn: Block, leavesMetadataIn: Block) : TreeGenerator() {
    /**
     * Sets the metadata for the wood blocks used
     */
    protected val woodMetadata: Block

    /**
     * Sets the metadata for the leaves used in huge trees
     */
    protected val leavesMetadata: Block

    /*
     * Calculates the height based on this trees base height and its extra random height
     */
    protected fun getHeight(rand: NukkitRandom): Int {
        var i: Int = rand.nextBoundedInt(3) + baseHeight
        if (extraRandomHeight > 1) {
            i += rand.nextBoundedInt(extraRandomHeight)
        }
        return i
    }

    /*
     * returns whether or not there is space for a tree to grow at a certain position
     */
    private fun isSpaceAt(worldIn: ChunkManager, leavesPos: Vector3, height: Int): Boolean {
        var flag = true
        return if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= 256) {
            for (i in 0..1 + height) {
                var j = 2
                if (i == 0) {
                    j = 1
                } else if (i >= 1 + height - 2) {
                    j = 2
                }
                var k = -j
                while (k <= j && flag) {
                    var l = -j
                    while (l <= j && flag) {
                        val blockPos: Vector3 = leavesPos.add(k, i, l)
                        if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= 256 || !this.canGrowInto(worldIn.getBlockIdAt(blockPos.x as Int, blockPos.y as Int, blockPos.z as Int))) {
                            flag = false
                        }
                        ++l
                    }
                    ++k
                }
            }
            flag
        } else {
            false
        }
    }

    /*
     * returns whether or not there is dirt underneath the block where the tree will be grown.
     * It also generates dirt around the block in a 2x2 square if there is dirt underneath the blockpos.
     */
    private fun ensureDirtsUnderneath(pos: Vector3, worldIn: ChunkManager): Boolean {
        val blockpos: Vector3 = pos.down()
        val block: Int = worldIn.getBlockIdAt(blockpos.x as Int, blockpos.y as Int, blockpos.z as Int)
        return if ((block == Block.GRASS || block == Block.DIRT) && pos.getY() >= 2) {
            this.setDirtAt(worldIn, blockpos)
            this.setDirtAt(worldIn, blockpos.east())
            this.setDirtAt(worldIn, blockpos.south())
            this.setDirtAt(worldIn, blockpos.south().east())
            true
        } else {
            false
        }
    }

    /*
     * returns whether or not a tree can grow at a specific position.
     * If it can, it generates surrounding dirt underneath.
     */
    protected fun ensureGrowable(worldIn: ChunkManager, rand: NukkitRandom?, treePos: Vector3, p_175929_4_: Int): Boolean {
        return isSpaceAt(worldIn, treePos, p_175929_4_) && ensureDirtsUnderneath(treePos, worldIn)
    }

    /*
     * grow leaves in a circle with the outsides being within the circle
     */
    protected fun growLeavesLayerStrict(worldIn: ChunkManager, layerCenter: Vector3, width: Int) {
        val i = width * width
        for (j in -width..width + 1) {
            for (k in -width..width + 1) {
                val l = j - 1
                val i1 = k - 1
                if (j * j + k * k <= i || l * l + i1 * i1 <= i || j * j + i1 * i1 <= i || l * l + k * k <= i) {
                    val blockpos: Vector3 = layerCenter.add(j, 0, k)
                    val id: Int = worldIn.getBlockIdAt(blockpos.x as Int, blockpos.y as Int, blockpos.z as Int)
                    if (id == Block.AIR || id == Block.LEAVES) {
                        this.setBlockAndNotifyAdequately(worldIn, blockpos, leavesMetadata)
                    }
                }
            }
        }
    }

    /*
     * grow leaves in a circle
     */
    protected fun growLeavesLayer(worldIn: ChunkManager, layerCenter: Vector3, width: Int) {
        val i = width * width
        for (j in -width..width) {
            for (k in -width..width) {
                if (j * j + k * k <= i) {
                    val blockpos: Vector3 = layerCenter.add(j, 0, k)
                    val id: Int = worldIn.getBlockIdAt(blockpos.x as Int, blockpos.y as Int, blockpos.z as Int)
                    if (id == Block.AIR || id == Block.LEAVES) {
                        this.setBlockAndNotifyAdequately(worldIn, blockpos, leavesMetadata)
                    }
                }
            }
        }
    }

    init {
        woodMetadata = woodMetadataIn
        leavesMetadata = leavesMetadataIn
    }
}