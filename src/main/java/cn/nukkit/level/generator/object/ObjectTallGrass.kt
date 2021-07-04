package cn.nukkit.level.generator.`object`

import cn.nukkit.block.Block

/**
 * @author ItsLucas (Nukkit Project)
 */
object ObjectTallGrass {
    fun growGrass(level: ChunkManager, pos: Vector3, random: NukkitRandom) {
        for (i in 0..127) {
            var num = 0
            var x: Int = pos.getFloorX()
            var y: Int = pos.getFloorY() + 1
            var z: Int = pos.getFloorZ()
            while (true) {
                if (num >= i / 16) {
                    if (level.getBlockIdAt(x, y, z) === Block.AIR) {
                        if (random.nextBoundedInt(8) === 0) {
                            //porktodo: biomes have specific flower types that can grow in them
                            if (random.nextBoolean()) {
                                level.setBlockAt(x, y, z, Block.DANDELION)
                            } else {
                                level.setBlockAt(x, y, z, Block.POPPY)
                            }
                        } else {
                            level.setBlockAt(x, y, z, Block.TALL_GRASS, 1)
                        }
                    }
                    break
                }
                x += random.nextRange(-1, 1)
                y += random.nextRange(-1, 1) * random.nextBoundedInt(3) / 2
                z += random.nextRange(-1, 1)
                if (level.getBlockIdAt(x, y - 1, z) !== Block.GRASS || y > 255 || y < 0) {
                    break
                }
                ++num
            }
        }
    }

    fun growGrass(level: ChunkManager, pos: Vector3, random: NukkitRandom, count: Int, radius: Int) {
        val arr = arrayOf(intArrayOf(Block.DANDELION, 0), intArrayOf(Block.POPPY, 0), intArrayOf(Block.TALL_GRASS, 1), intArrayOf(Block.TALL_GRASS, 1), intArrayOf(Block.TALL_GRASS, 1), intArrayOf(Block.TALL_GRASS, 1))
        val arrC = arr.size - 1
        for (c in 0 until count) {
            val x: Int = random.nextRange((pos.x - radius) as Int, (pos.x + radius) as Int)
            val z: Int = random.nextRange(pos.z as Int - radius, (pos.z + radius) as Int)
            if (level.getBlockIdAt(x, (pos.y + 1) as Int, z) === Block.AIR && level.getBlockIdAt(x, pos.y as Int, z) === Block.GRASS) {
                val t = arr[random.nextRange(0, arrC)]
                level.setBlockAt(x, (pos.y + 1) as Int, z, t[0], t[1])
            }
        }
    }
}