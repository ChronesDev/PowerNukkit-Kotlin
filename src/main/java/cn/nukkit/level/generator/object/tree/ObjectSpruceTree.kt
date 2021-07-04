package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ObjectSpruceTree : ObjectTree() {
    @get:Override
    override var treeHeight = 0
        protected set

    @get:Override
    override val trunkBlock: Int
        get() = Block.LOG

    @get:Override
    override val leafBlock: Int
        get() = Block.LEAVES

    @get:Override
    override val type: Int
        get() = BlockWood.SPRUCE

    @Override
    override fun placeObject(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom) {
        treeHeight = random.nextBoundedInt(4) + 6
        val topSize: Int = treeHeight - (1 + random.nextBoundedInt(2))
        val lRadius: Int = 2 + random.nextBoundedInt(2)
        this.placeTrunk(level, x, y, z, random, treeHeight - random.nextBoundedInt(3))
        placeLeaves(level, topSize, lRadius, x, y, z, random)
    }

    fun placeLeaves(level: ChunkManager, topSize: Int, lRadius: Int, x: Int, y: Int, z: Int, random: NukkitRandom) {
        var radius: Int = random.nextBoundedInt(2)
        var maxR = 1
        var minR = 0
        for (yy in 0..topSize) {
            val yyy = y + treeHeight - yy
            for (xx in x - radius..x + radius) {
                val xOff: Int = Math.abs(xx - x)
                for (zz in z - radius..z + radius) {
                    val zOff: Int = Math.abs(zz - z)
                    if (xOff == radius && zOff == radius && radius > 0) {
                        continue
                    }
                    if (!Block.solid.get(level.getBlockIdAt(xx, yyy, zz))) {
                        level.setBlockAt(xx, yyy, zz, leafBlock, type)
                    }
                }
            }
            if (radius >= maxR) {
                radius = minR
                minR = 1
                if (++maxR > lRadius) {
                    maxR = lRadius
                }
            } else {
                ++radius
            }
        }
    }
}