package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.*

class ObjectSwampTree : TreeGenerator() {
    /**
     * The metadata value of the wood to use in tree generation.
     */
    private val metaWood: Block = Block.get(BlockID.WOOD, BlockWood.OAK)

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private val metaLeaves: Block = Block.get(BlockID.LEAVES, BlockLeaves.OAK)

    @Override
    override fun generate(worldIn: ChunkManager, rand: NukkitRandom, vectorPosition: Vector3): Boolean {
        val position = BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ())
        val i: Int = rand.nextBoundedInt(4) + 5
        var flag = true
        return if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (j in position.getY()..position.getY() + 1 + i) {
                var k = 1
                if (j == position.getY()) {
                    k = 0
                }
                if (j >= position.getY() + 1 + i - 2) {
                    k = 3
                }
                val pos2 = BlockVector3()
                var l: Int = position.getX() - k
                while (l <= position.getX() + k && flag) {
                    var i1: Int = position.getZ() - k
                    while (i1 <= position.getZ() + k && flag) {
                        if (j >= 0 && j < 256) {
                            pos2.setComponents(l, j, i1)
                            if (!this.canGrowInto(worldIn.getBlockIdAt(pos2.x, pos2.y, pos2.z))) {
                                flag = false
                            }
                        } else {
                            flag = false
                        }
                        ++i1
                    }
                    ++l
                }
            }
            if (!flag) {
                false
            } else {
                val down: BlockVector3 = position.down()
                val block: Int = worldIn.getBlockIdAt(down.x, down.y, down.z)
                if ((block == Block.GRASS || block == Block.DIRT) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(worldIn, down)
                    for (k1 in position.getY() - 3 + i..position.getY() + i) {
                        val j2: Int = k1 - (position.getY() + i)
                        val l2 = 2 - j2 / 2
                        for (j3 in position.getX() - l2..position.getX() + l2) {
                            val k3: Int = j3 - position.getX()
                            for (i4 in position.getZ() - l2..position.getZ() + l2) {
                                val j1: Int = i4 - position.getZ()
                                if (Math.abs(k3) !== l2 || Math.abs(j1) !== l2 || rand.nextBoundedInt(2) !== 0 && j2 != 0) {
                                    val blockpos = BlockVector3(j3, k1, i4)
                                    val id: Int = worldIn.getBlockIdAt(blockpos.x, blockpos.y, blockpos.z)
                                    if (id == Block.AIR || id == Block.LEAVES || id == Block.VINE) {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, metaLeaves)
                                    }
                                }
                            }
                        }
                    }
                    for (l1 in 0 until i) {
                        val up: BlockVector3 = position.up(l1)
                        val id: Int = worldIn.getBlockIdAt(up.x, up.y, up.z)
                        if (id == Block.AIR || id == Block.LEAVES || id == Block.WATER || id == Block.STILL_WATER) {
                            this.setBlockAndNotifyAdequately(worldIn, up, metaWood)
                        }
                    }
                    for (i2 in position.getY() - 3 + i..position.getY() + i) {
                        val k2: Int = i2 - (position.getY() + i)
                        val i3 = 2 - k2 / 2
                        val pos2 = BlockVector3()
                        for (l3 in position.getX() - i3..position.getX() + i3) {
                            for (j4 in position.getZ() - i3..position.getZ() + i3) {
                                pos2.setComponents(l3, i2, j4)
                                if (worldIn.getBlockIdAt(pos2.x, pos2.y, pos2.z) === Block.LEAVES) {
                                    val blockpos2: BlockVector3 = pos2.west()
                                    val blockpos3: BlockVector3 = pos2.east()
                                    val blockpos4: BlockVector3 = pos2.north()
                                    val blockpos1: BlockVector3 = pos2.south()
                                    if (rand.nextBoundedInt(4) === 0 && worldIn.getBlockIdAt(blockpos2.x, blockpos2.y, blockpos2.z) === Block.AIR) {
                                        addHangingVine(worldIn, blockpos2, 8)
                                    }
                                    if (rand.nextBoundedInt(4) === 0 && worldIn.getBlockIdAt(blockpos3.x, blockpos3.y, blockpos3.z) === Block.AIR) {
                                        addHangingVine(worldIn, blockpos3, 2)
                                    }
                                    if (rand.nextBoundedInt(4) === 0 && worldIn.getBlockIdAt(blockpos4.x, blockpos4.y, blockpos4.z) === Block.AIR) {
                                        addHangingVine(worldIn, blockpos4, 1)
                                    }
                                    if (rand.nextBoundedInt(4) === 0 && worldIn.getBlockIdAt(blockpos1.x, blockpos1.y, blockpos1.z) === Block.AIR) {
                                        addHangingVine(worldIn, blockpos1, 4)
                                    }
                                }
                            }
                        }
                    }
                    true
                } else {
                    false
                }
            }
        } else {
            false
        }
    }

    private fun addVine(worldIn: ChunkManager, pos: BlockVector3, meta: Int) {
        this.setBlockAndNotifyAdequately(worldIn, pos, Block.get(BlockID.VINE, meta))
    }

    private fun addHangingVine(worldIn: ChunkManager, pos: BlockVector3, meta: Int) {
        var pos: BlockVector3 = pos
        addVine(worldIn, pos, meta)
        var i = 4
        pos = pos.down()
        while (i > 0 && worldIn.getBlockIdAt(pos.x, pos.y, pos.z) === Block.AIR) {
            addVine(worldIn, pos, meta)
            pos = pos.down()
            --i
        }
    }
}