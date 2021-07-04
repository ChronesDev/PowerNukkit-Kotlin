package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author Snake1999
 * @since 2016/1/14
 */
class ItemAppleGoldEnchanted @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(GOLDEN_APPLE_ENCHANTED, meta, count, "Enchanted Golden Apple") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun onClickAir(player: Player?, directionVector: Vector3?): Boolean {
        return true
    }
}