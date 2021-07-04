package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.Block

/**
 * @author DaPorkchop_ (Nukkit Project)
 */
class ObjectBigSpruceTree(private val leafStartHeightMultiplier: Float, private val baseLeafRadius: Int) : ObjectSpruceTree() {
    @Override
    override fun placeObject(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom) {
        this.treeHeight = random.nextBoundedInt(15) + 20
        val topSize: Int = this.treeHeight - (this.treeHeight * leafStartHeightMultiplier) as Int
        val lRadius: Int = baseLeafRadius + random.nextBoundedInt(2)
        placeTrunk(level, x, y, z, random, this.getTreeHeight() - random.nextBoundedInt(3))
        this.placeLeaves(level, topSize, lRadius, x, y, z, random)
    }

    @Override
    protected override fun placeTrunk(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom?, trunkHeight: Int) {
        // The base dirt block
        level.setBlockAt(x, y - 1, z, Block.DIRT)
        val radius = 2
        for (yy in 0 until trunkHeight) {
            for (xx in 0 until radius) {
                for (zz in 0 until radius) {
                    val blockId: Int = level.getBlockIdAt(x, y + yy, z)
                    if (this.overridable(blockId)) {
                        level.setBlockAt(x + xx, y + yy, z + zz, this.getTrunkBlock(), this.getType())
                    }
                }
            }
        }
    }
}