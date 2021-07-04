package cn.nukkit.item

import cn.nukkit.block.Block

/**
 * @author Snake1999
 * @since 2016/2/3
 */
class ItemSkull @JvmOverloads constructor(meta: Integer = 0, count: Int = 1) : Item(SKULL, meta, count, getItemSkullName(meta)) {
    constructor(meta: Integer) : this(meta, 1) {}

    companion object {
        const val SKELETON_SKULL = 0
        const val WITHER_SKELETON_SKULL = 1
        const val ZOMBIE_HEAD = 2
        const val HEAD = 3
        const val CREEPER_HEAD = 4
        const val DRAGON_HEAD = 5
        fun getItemSkullName(meta: Int): String {
            return when (meta) {
                1 -> "Wither Skeleton Skull"
                2 -> "Zombie Head"
                3 -> "Head"
                4 -> "Creeper Head"
                5 -> "Dragon Head"
                0 -> "Skeleton Skull"
                else -> "Skeleton Skull"
            }
        }
    }

    init {
        this.block = Block.get(Block.SKULL_BLOCK)
    }
}