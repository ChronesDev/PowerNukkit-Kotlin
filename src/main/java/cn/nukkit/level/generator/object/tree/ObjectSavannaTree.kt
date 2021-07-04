package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.Block

class ObjectSavannaTree : TreeGenerator() {
    override fun generate(level: ChunkManager, rand: NukkitRandom, position: Vector3): Boolean {
        val i: Int = rand.nextBoundedInt(3) + rand.nextBoundedInt(3) + 5
        var flag = true
        return if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (j in position.getY() as Int..position.getY() + 1 + i) {
                var k = 1
                if (j == position.getY()) {
                    k = 0
                }
                if (j >= position.getY() + 1 + i - 2) {
                    k = 2
                }
                val vector3 = Vector3()
                var l = position.getX() as Int - k
                while (l <= position.getX() + k && flag) {
                    var i1 = position.getZ() as Int - k
                    while (i1 <= position.getZ() + k && flag) {
                        if (j >= 0 && j < 256) {
                            vector3.setComponents(l, j, i1)
                            if (!this.canGrowInto(level.getBlockIdAt(vector3.x as Int, vector3.y as Int, vector3.z as Int))) {
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
                val down: Vector3 = position.down()
                val block: Int = level.getBlockIdAt(down.getFloorX(), down.getFloorY(), down.getFloorZ())
                if ((block == Block.GRASS || block == Block.DIRT) && position.getY() < 256 - i - 1) {
                    this.setDirtAt(level, position.down())
                    val face: BlockFace = BlockFace.Plane.HORIZONTAL.random(rand)
                    val k2: Int = i - rand.nextBoundedInt(4) - 1
                    var l2: Int = 3 - rand.nextBoundedInt(3)
                    var i3: Int = position.getFloorX()
                    var j1: Int = position.getFloorZ()
                    var k1 = 0
                    for (l1 in 0 until i) {
                        val i2: Int = position.getFloorY() + l1
                        if (l1 >= k2 && l2 > 0) {
                            i3 += face.getXOffset()
                            j1 += face.getZOffset()
                            --l2
                        }
                        val blockpos = Vector3(i3, i2, j1)
                        val material: Int = level.getBlockIdAt(blockpos.getFloorX(), blockpos.getFloorY(), blockpos.getFloorZ())
                        if (material == Block.AIR || material == Block.LEAVES) {
                            placeLogAt(level, blockpos)
                            k1 = i2
                        }
                    }
                    var blockpos2 = Vector3(i3, k1, j1)
                    for (j3 in -3..3) {
                        for (i4 in -3..3) {
                            if (Math.abs(j3) !== 3 || Math.abs(i4) !== 3) {
                                placeLeafAt(level, blockpos2.add(j3, 0, i4))
                            }
                        }
                    }
                    blockpos2 = blockpos2.up()
                    for (k3 in -1..1) {
                        for (j4 in -1..1) {
                            placeLeafAt(level, blockpos2.add(k3, 0, j4))
                        }
                    }
                    placeLeafAt(level, blockpos2.east(2))
                    placeLeafAt(level, blockpos2.west(2))
                    placeLeafAt(level, blockpos2.south(2))
                    placeLeafAt(level, blockpos2.north(2))
                    i3 = position.getFloorX()
                    j1 = position.getFloorZ()
                    val face1: BlockFace = BlockFace.Plane.HORIZONTAL.random(rand)
                    if (face1 !== face) {
                        val l3: Int = k2 - rand.nextBoundedInt(2) - 1
                        var k4: Int = 1 + rand.nextBoundedInt(3)
                        k1 = 0
                        var l4 = l3
                        while (l4 < i && k4 > 0) {
                            if (l4 >= 1) {
                                val j2: Int = position.getFloorY() + l4
                                i3 += face1.getXOffset()
                                j1 += face1.getZOffset()
                                val blockpos1 = Vector3(i3, j2, j1)
                                val material1: Int = level.getBlockIdAt(blockpos1.getFloorX(), blockpos1.getFloorY(), blockpos1.getFloorZ())
                                if (material1 == Block.AIR || material1 == Block.LEAVES) {
                                    placeLogAt(level, blockpos1)
                                    k1 = j2
                                }
                            }
                            ++l4
                            --k4
                        }
                        if (k1 > 0) {
                            var blockpos3 = Vector3(i3, k1, j1)
                            for (i5 in -2..2) {
                                for (k5 in -2..2) {
                                    if (Math.abs(i5) !== 2 || Math.abs(k5) !== 2) {
                                        placeLeafAt(level, blockpos3.add(i5, 0, k5))
                                    }
                                }
                            }
                            blockpos3 = blockpos3.up()
                            for (j5 in -1..1) {
                                for (l5 in -1..1) {
                                    placeLeafAt(level, blockpos3.add(j5, 0, l5))
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

    private fun placeLogAt(worldIn: ChunkManager, pos: Vector3) {
        this.setBlockAndNotifyAdequately(worldIn, pos, TRUNK)
    }

    private fun placeLeafAt(worldIn: ChunkManager, pos: Vector3) {
        val material: Int = worldIn.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ())
        if (material == Block.AIR || material == Block.LEAVES) {
            this.setBlockAndNotifyAdequately(worldIn, pos, LEAF)
        }
    }

    companion object {
        private val TRUNK: Block = Block.get(BlockID.WOOD2, BlockWood2.ACACIA)
        private val LEAF: Block = Block.get(BlockID.LEAVES2, BlockLeaves2.ACACIA)
    }
}