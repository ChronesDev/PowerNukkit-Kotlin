package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.*

/**
 * @author CreeperFace
 * @since 26. 10. 2016
 */
class NewJungleTree(
        /**
         * The minimum height of a generated tree.
         */
        private val minTreeHeight: Int, private val maxTreeHeight: Int) : TreeGenerator() {
    /**
     * The metadata value of the wood to use in tree generation.
     */
    private val metaWood: Block = Block.get(BlockID.WOOD, BlockWood.JUNGLE)

    /**
     * The metadata value of the leaves to use in tree generation.
     */
    private val metaLeaves: Block = Block.get(BlockID.LEAVES, BlockLeaves.JUNGLE)

    @Override
    override fun generate(worldIn: ChunkManager, rand: NukkitRandom, vectorPosition: Vector3): Boolean {
        val position = BlockVector3(vectorPosition.getFloorX(), vectorPosition.getFloorY(), vectorPosition.getFloorZ())
        val i: Int = rand.nextBoundedInt(maxTreeHeight) + minTreeHeight
        var flag = true
        return if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (j in position.getY()..position.getY() + 1 + i) {
                var k = 1
                if (j == position.getY()) {
                    k = 0
                }
                if (j >= position.getY() + 1 + i - 2) {
                    k = 2
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
                if ((block == Block.GRASS || block == Block.DIRT || block == Block.FARMLAND) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(worldIn, down)
                    val k2 = 3
                    val l2 = 0
                    for (i3 in position.getY() - 3 + i..position.getY() + i) {
                        val i4: Int = i3 - (position.getY() + i)
                        val j1 = 1 - i4 / 2
                        for (k1 in position.getX() - j1..position.getX() + j1) {
                            val l1: Int = k1 - position.getX()
                            for (i2 in position.getZ() - j1..position.getZ() + j1) {
                                val j2: Int = i2 - position.getZ()
                                if (Math.abs(l1) !== j1 || Math.abs(j2) !== j1 || rand.nextBoundedInt(2) !== 0 && i4 != 0) {
                                    val blockpos = BlockVector3(k1, i3, i2)
                                    val id: Int = worldIn.getBlockIdAt(blockpos.x, blockpos.y, blockpos.z)
                                    if (id == Block.AIR || id == Block.LEAVES || id == Block.VINE) {
                                        this.setBlockAndNotifyAdequately(worldIn, blockpos, metaLeaves)
                                    }
                                }
                            }
                        }
                    }
                    for (j3 in 0 until i) {
                        val up: BlockVector3 = position.up(j3)
                        val id: Int = worldIn.getBlockIdAt(up.x, up.y, up.z)
                        if (id == Block.AIR || id == Block.LEAVES || id == Block.VINE) {
                            this.setBlockAndNotifyAdequately(worldIn, up, metaWood)
                            if (j3 > 0) {
                                if (rand.nextBoundedInt(3) > 0 && isAirBlock(worldIn, position.add(-1, j3, 0))) {
                                    addVine(worldIn, position.add(-1, j3, 0), 8)
                                }
                                if (rand.nextBoundedInt(3) > 0 && isAirBlock(worldIn, position.add(1, j3, 0))) {
                                    addVine(worldIn, position.add(1, j3, 0), 2)
                                }
                                if (rand.nextBoundedInt(3) > 0 && isAirBlock(worldIn, position.add(0, j3, -1))) {
                                    addVine(worldIn, position.add(0, j3, -1), 1)
                                }
                                if (rand.nextBoundedInt(3) > 0 && isAirBlock(worldIn, position.add(0, j3, 1))) {
                                    addVine(worldIn, position.add(0, j3, 1), 4)
                                }
                            }
                        }
                    }
                    for (k3 in position.getY() - 3 + i..position.getY() + i) {
                        val j4: Int = k3 - (position.getY() + i)
                        val k4 = 2 - j4 / 2
                        val pos2 = BlockVector3()
                        for (l4 in position.getX() - k4..position.getX() + k4) {
                            for (i5 in position.getZ() - k4..position.getZ() + k4) {
                                pos2.setComponents(l4, k3, i5)
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
                    if (rand.nextBoundedInt(5) === 0 && i > 5) {
                        for (l3 in 0..1) {
                            for (enumfacing in BlockFace.Plane.HORIZONTAL) {
                                if (rand.nextBoundedInt(4 - l3) === 0) {
                                    val enumfacing1: BlockFace = enumfacing.getOpposite()
                                    placeCocoa(worldIn, rand.nextBoundedInt(3), position.add(enumfacing1.getXOffset(), i - 5 + l3, enumfacing1.getZOffset()), enumfacing)
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

    private fun placeCocoa(worldIn: ChunkManager, age: Int, pos: BlockVector3, side: BlockFace) {
        val meta = getCocoaMeta(age, side.getIndex())
        this.setBlockAndNotifyAdequately(worldIn, pos, BlockUnknown(127, meta))
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

    private fun isAirBlock(level: ChunkManager, v: BlockVector3): Boolean {
        return level.getBlockIdAt(v.x, v.y, v.z) === 0
    }

    private fun getCocoaMeta(age: Int, side: Int): Int {
        var meta = age * 4
        when (side) {
            4 -> meta++
            2 -> meta += 2
            5 -> meta += 3
        }
        return meta
    }
}