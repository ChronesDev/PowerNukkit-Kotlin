package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemAppleGold @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : ItemEdible(GOLDEN_APPLE, meta, count, "Golden Apple") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun onClickAir(player: Player?, directionVector: Vector3?): Boolean {
        return true
    }
}