package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.Block

class ObjectJungleBigTree(baseHeightIn: Int, extraRandomHeight: Int, woodMetadata: Block, leavesMetadata: Block) : HugeTreesGenerator(baseHeightIn, extraRandomHeight, woodMetadata, leavesMetadata) {
    override fun generate(level: ChunkManager, rand: NukkitRandom, position: Vector3): Boolean {
        val height: Int = this.getHeight(rand)
        return if (!this.ensureGrowable(level, rand, position, height)) {
            false
        } else {
            createCrown(level, position.up(height), 2)
            var j: Int = position.getY() as Int + height - 2 - rand.nextBoundedInt(4)
            while (j > position.getY() + height / 2) {
                val f: Float = rand.nextFloat() * (Math.PI as Float * 2f)
                var k = (position.getX() + (0.5f + MathHelper.cos(f) * 4.0f)) as Int
                var l = (position.getZ() + (0.5f + MathHelper.sin(f) * 4.0f)) as Int
                for (i1 in 0..4) {
                    k = (position.getX() + (1.5f + MathHelper.cos(f) * i1.toFloat()))
                    l = (position.getZ() + (1.5f + MathHelper.sin(f) * i1.toFloat()))
                    this.setBlockAndNotifyAdequately(level, BlockVector3(k, j - 3 + i1 / 2, l), this.woodMetadata)
                }
                val j2: Int = 1 + rand.nextBoundedInt(2)
                for (k1 in j - j2..j) {
                    val l1 = k1 - j
                    this.growLeavesLayer(level, Vector3(k, k1, l), 1 - l1)
                }
                j -= 2 + rand.nextBoundedInt(4)
            }
            for (i2 in 0 until height) {
                val blockpos: Vector3 = position.up(i2)
                if (this.canGrowInto(level.getBlockIdAt(blockpos.x as Int, blockpos.y as Int, blockpos.z as Int))) {
                    this.setBlockAndNotifyAdequately(level, blockpos, this.woodMetadata)
                    if (i2 > 0) {
                        placeVine(level, rand, blockpos.west(), 8)
                        placeVine(level, rand, blockpos.north(), 1)
                    }
                }
                if (i2 < height - 1) {
                    val blockpos1: Vector3 = blockpos.east()
                    if (this.canGrowInto(level.getBlockIdAt(blockpos1.x as Int, blockpos1.y as Int, blockpos1.z as Int))) {
                        this.setBlockAndNotifyAdequately(level, blockpos1, this.woodMetadata)
                        if (i2 > 0) {
                            placeVine(level, rand, blockpos1.east(), 2)
                            placeVine(level, rand, blockpos1.north(), 1)
                        }
                    }
                    val blockpos2: Vector3 = blockpos.south().east()
                    if (this.canGrowInto(level.getBlockIdAt(blockpos2.x as Int, blockpos2.y as Int, blockpos2.z as Int))) {
                        this.setBlockAndNotifyAdequately(level, blockpos2, this.woodMetadata)
                        if (i2 > 0) {
                            placeVine(level, rand, blockpos2.east(), 2)
                            placeVine(level, rand, blockpos2.south(), 4)
                        }
                    }
                    val blockpos3: Vector3 = blockpos.south()
                    if (this.canGrowInto(level.getBlockIdAt(blockpos3.x as Int, blockpos3.y as Int, blockpos3.z as Int))) {
                        this.setBlockAndNotifyAdequately(level, blockpos3, this.woodMetadata)
                        if (i2 > 0) {
                            placeVine(level, rand, blockpos3.west(), 8)
                            placeVine(level, rand, blockpos3.south(), 4)
                        }
                    }
                }
            }
            true
        }
    }

    private fun placeVine(level: ChunkManager, random: NukkitRandom, pos: Vector3, meta: Int) {
        if (random.nextBoundedInt(3) > 0 && level.getBlockIdAt(pos.x as Int, pos.y as Int, pos.z as Int) === 0) {
            this.setBlockAndNotifyAdequately(level, pos, Block.get(BlockID.VINE, meta))
        }
    }

    private fun createCrown(level: ChunkManager, pos: Vector3, i1: Int) {
        for (j in -2..0) {
            this.growLeavesLayerStrict(level, pos.up(j), i1 + 1 - j)
        }
    }
}