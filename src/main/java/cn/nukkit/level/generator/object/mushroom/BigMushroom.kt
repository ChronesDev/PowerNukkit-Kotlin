package cn.nukkit.level.generator.`object`.mushroom

import cn.nukkit.block.Block

class BigMushroom : BasicGenerator {
    /**
     * The mushroom type. 0 for brown, 1 for red.
     */
    private var mushroomType: Int

    constructor(mushroomType: Int) {
        this.mushroomType = mushroomType
    }

    constructor() {
        mushroomType = -1
    }

    fun generate(level: ChunkManager, rand: NukkitRandom, position: Vector3): Boolean {
        var block = mushroomType
        if (block < 0) {
            block = if (rand.nextBoolean()) RED else BROWN
        }
        val mushroom: Block = if (block == 0) Block.get(BlockID.BROWN_MUSHROOM_BLOCK) else Block.get(BlockID.RED_MUSHROOM_BLOCK)
        var i: Int = rand.nextBoundedInt(3) + 4
        if (rand.nextBoundedInt(12) === 0) {
            i *= 2
        }
        var flag = true
        return if (position.getY() >= 1 && position.getY() + i + 1 < 256) {
            for (j in position.getFloorY()..position.getY() + 1 + i) {
                var k = 3
                if (j <= position.getY() + 3) {
                    k = 0
                }
                val pos = Vector3()
                var l: Int = position.getFloorX() - k
                while (l <= position.getX() + k && flag) {
                    var i1: Int = position.getFloorZ() - k
                    while (i1 <= position.getZ() + k && flag) {
                        if (j >= 0 && j < 256) {
                            pos.setComponents(l, j, i1)
                            val material: Int = level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ())
                            if (material != Block.AIR && material != Block.LEAVES) {
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
                val pos2: Vector3 = position.down()
                val block1: Int = level.getBlockIdAt(pos2.getFloorX(), pos2.getFloorY(), pos2.getFloorZ())
                if (block1 != Block.DIRT && block1 != Block.GRASS && block1 != Block.MYCELIUM) {
                    false
                } else {
                    var k2: Int = position.getFloorY() + i
                    if (block == RED) {
                        k2 = position.getFloorY() + i - 3
                    }
                    for (l2 in k2..position.getY() + i) {
                        var j3 = 1
                        if (l2 < position.getY() + i) {
                            ++j3
                        }
                        if (block == BROWN) {
                            j3 = 3
                        }
                        val k3: Int = position.getFloorX() - j3
                        val l3: Int = position.getFloorX() + j3
                        val j1: Int = position.getFloorZ() - j3
                        val k1: Int = position.getFloorZ() + j3
                        for (l1 in k3..l3) {
                            for (i2 in j1..k1) {
                                var j2 = 5
                                if (l1 == k3) {
                                    --j2
                                } else if (l1 == l3) {
                                    ++j2
                                }
                                if (i2 == j1) {
                                    j2 -= 3
                                } else if (i2 == k1) {
                                    j2 += 3
                                }
                                var meta = j2
                                if (block == BROWN || l2 < position.getY() + i) {
                                    if ((l1 == k3 || l1 == l3) && (i2 == j1 || i2 == k1)) {
                                        continue
                                    }
                                    if (l1 == position.getX() - (j3 - 1) && i2 == j1) {
                                        meta = NORTH_WEST
                                    }
                                    if (l1 == k3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = NORTH_WEST
                                    }
                                    if (l1 == position.getX() + (j3 - 1) && i2 == j1) {
                                        meta = NORTH_EAST
                                    }
                                    if (l1 == l3 && i2 == position.getZ() - (j3 - 1)) {
                                        meta = NORTH_EAST
                                    }
                                    if (l1 == position.getX() - (j3 - 1) && i2 == k1) {
                                        meta = SOUTH_WEST
                                    }
                                    if (l1 == k3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = SOUTH_WEST
                                    }
                                    if (l1 == position.getX() + (j3 - 1) && i2 == k1) {
                                        meta = SOUTH_EAST
                                    }
                                    if (l1 == l3 && i2 == position.getZ() + (j3 - 1)) {
                                        meta = SOUTH_EAST
                                    }
                                }
                                if (meta == CENTER && l2 < position.getY() + i) {
                                    meta = ALL_INSIDE
                                }
                                if (position.getY() >= position.getY() + i - 1 || meta != ALL_INSIDE) {
                                    val blockPos = Vector3(l1, l2, i2)
                                    if (!Block.solid.get(level.getBlockIdAt(blockPos.getFloorX(), blockPos.getFloorY(), blockPos.getFloorZ()))) {
                                        mushroom.setDamage(meta)
                                        this.setBlockAndNotifyAdequately(level, blockPos, mushroom)
                                    }
                                }
                            }
                        }
                    }
                    for (i3 in 0 until i) {
                        val pos: Vector3 = position.up(i3)
                        val id: Int = level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ())
                        if (!Block.solid.get(id)) {
                            mushroom.setDamage(STEM)
                            this.setBlockAndNotifyAdequately(level, pos, mushroom)
                        }
                    }
                    true
                }
            }
        } else {
            false
        }
    }

    companion object {
        const val NORTH_WEST = 1
        const val NORTH = 2
        const val NORTH_EAST = 3
        const val WEST = 4
        const val CENTER = 5
        const val EAST = 6
        const val SOUTH_WEST = 7
        const val SOUTH = 8
        const val SOUTH_EAST = 9
        const val STEM = 10
        const val ALL_INSIDE = 0
        const val ALL_OUTSIDE = 14
        const val ALL_STEM = 15
        const val BROWN = 0
        const val RED = 1
    }
}