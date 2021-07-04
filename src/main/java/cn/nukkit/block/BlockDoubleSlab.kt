package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Deprecated
@DeprecationDetails(reason = "Unused and the same as BlockDoubleSlabStone", since = "1.4.0.0-PN", replaceWith = "BlockDoubleSlabBase")
@PowerNukkitDifference(info = "Overrides BlockDoubleSlabStone only in PowerNukkit", since = "1.4.0.0-PN")
class BlockDoubleSlab @JvmOverloads constructor(meta: Int = 0) : BlockDoubleSlabStone(meta) {
    companion object {
        const val STONE = 0
        const val SANDSTONE = 1
        const val WOODEN = 2
        const val COBBLESTONE = 3
        const val BRICK = 4
        const val STONE_BRICK = 5
        const val QUARTZ = 6
        const val NETHER_BRICK = 7
    }
}