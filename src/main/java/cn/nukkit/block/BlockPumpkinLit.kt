package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/8
 */
class BlockPumpkinLit @JvmOverloads constructor(meta: Int = 0) : BlockPumpkin(meta) {
    @get:Override
    override val name: String
        get() = "Jack o'Lantern"

    @get:Override
    override val id: Int
        get() = LIT_PUMPKIN

    @get:Override
    override val lightLevel: Int
        get() = 15

    @Override
    override fun canBeActivated(): Boolean {
        return false
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return false
    }
}