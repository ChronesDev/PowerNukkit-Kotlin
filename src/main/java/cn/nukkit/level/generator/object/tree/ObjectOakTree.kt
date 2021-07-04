package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ObjectOakTree : ObjectTree() {
    @get:Override
    override var treeHeight = 7
        private set

    @get:Override
    override val trunkBlock: Int
        get() = Block.LOG

    @get:Override
    override val leafBlock: Int
        get() = Block.LEAVES

    @get:Override
    override val type: Int
        get() = BlockWood.OAK

    @Override
    override fun placeObject(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom) {
        treeHeight = random.nextBoundedInt(3) + 4
        super.placeObject(level, x, y, z, random)
    }
}