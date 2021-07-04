package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.level.ChunkManager

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ObjectTallBirchTree : ObjectBirchTree() {
    @Override
    override fun placeObject(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom) {
        this.treeHeight = random.nextBoundedInt(3) + 10
        super.placeObject(level, x, y, z, random)
    }
}