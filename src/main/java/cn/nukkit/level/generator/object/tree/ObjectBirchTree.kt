package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.block.Block

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ObjectBirchTree : ObjectTree() {
    @get:Override
    override var treeHeight = 7
        protected set

    @get:Override
    override val trunkBlock: Int
        get() = Block.LOG

    @get:Override
    override val leafBlock: Int
        get() = Block.LEAVES

    @get:Override
    override val type: Int
        get() = BlockWood.BIRCH

    @Override
    override fun placeObject(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom) {
        treeHeight = random.nextBoundedInt(2) + 5
        super.placeObject(level, x, y, z, random)
    }
}