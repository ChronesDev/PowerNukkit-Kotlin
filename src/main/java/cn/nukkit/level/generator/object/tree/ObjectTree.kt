package cn.nukkit.level.generator.`object`.tree

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class ObjectTree {
    protected fun overridable(id: Int): Boolean {
        return when (id) {
            Block.AIR, Block.SAPLING, Block.LOG, Block.LEAVES, Block.SNOW_LAYER, Block.LOG2, Block.LEAVES2 -> true
            else -> false
        }
    }

    val type: Int
        get() = 0
    val trunkBlock: Int
        get() = Block.LOG
    val leafBlock: Int
        get() = Block.LEAVES
    val treeHeight: Int
        get() = 7

    fun canPlaceObject(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom?): Boolean {
        var radiusToCheck = 0
        for (yy in 0 until treeHeight + 3) {
            if (yy == 1 || yy == treeHeight) {
                ++radiusToCheck
            }
            for (xx in -radiusToCheck until radiusToCheck + 1) {
                for (zz in -radiusToCheck until radiusToCheck + 1) {
                    if (!overridable(level.getBlockIdAt(x + xx, y + yy, z + zz))) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun placeObject(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom) {
        placeTrunk(level, x, y, z, random, treeHeight - 1)
        for (yy in y - 3 + treeHeight..y + treeHeight) {
            val yOff = (yy - (y + treeHeight)).toDouble()
            val mid = (1 - yOff / 2).toInt()
            for (xx in x - mid..x + mid) {
                val xOff: Int = Math.abs(xx - x)
                for (zz in z - mid..z + mid) {
                    val zOff: Int = Math.abs(zz - z)
                    if (xOff == mid && zOff == mid && (yOff == 0.0 || random.nextBoundedInt(2) === 0)) {
                        continue
                    }
                    if (!Block.solid.get(level.getBlockIdAt(xx, yy, zz))) {
                        level.setBlockAt(xx, yy, zz, leafBlock, type)
                    }
                }
            }
        }
    }

    protected fun placeTrunk(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom?, trunkHeight: Int) {
        // The base dirt block
        level.setBlockAt(x, y - 1, z, Block.DIRT)
        for (yy in 0 until trunkHeight) {
            val blockId: Int = level.getBlockIdAt(x, y + yy, z)
            if (overridable(blockId)) {
                level.setBlockAt(x, y + yy, z, trunkBlock, type)
            }
        }
    }

    companion object {
        fun growTree(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom) {
            growTree(level, x, y, z, random, 0)
        }

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Magic value in type", replaceWith = "growTree(ChunkManager level, int x, int y, int z, NukkitRandom random, WoodType type, boolean tall)")
        fun growTree(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom, type: Int) {
            val woodType: WoodType
            var tall = false
            when (type) {
                BlockSapling.SPRUCE -> woodType = WoodType.SPRUCE
                BlockSapling.BIRCH -> woodType = WoodType.BIRCH
                BlockSapling.JUNGLE -> woodType = WoodType.JUNGLE
                BlockSapling.BIRCH_TALL -> {
                    woodType = WoodType.BIRCH
                    tall = true
                }
                BlockSapling.OAK -> woodType = WoodType.OAK
                else -> woodType = WoodType.OAK
            }
            growTree(level, x, y, z, random, woodType, tall)
        }

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun growTree(level: ChunkManager, x: Int, y: Int, z: Int, random: NukkitRandom, type: WoodType?, tall: Boolean) {
            val tree: ObjectTree
            when (type) {
                SPRUCE -> tree = ObjectSpruceTree()
                BIRCH -> if (tall) {
                    tree = ObjectTallBirchTree()
                } else {
                    tree = ObjectBirchTree()
                }
                JUNGLE -> tree = ObjectJungleTree()
                OAK -> tree = ObjectOakTree()
                else -> tree = ObjectOakTree()
            }
            if (tree.canPlaceObject(level, x, y, z, random)) {
                tree.placeObject(level, x, y, z, random)
            }
        }
    }
}