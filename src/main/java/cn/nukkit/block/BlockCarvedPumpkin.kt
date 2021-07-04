package cn.nukkit.block

import cn.nukkit.Player

class BlockCarvedPumpkin : BlockPumpkin() {
    @get:Override
    override val id: Int
        get() = CARVED_PUMPKIN

    @get:Override
    override val name: String
        get() = "Carved Pumpkin"

    @Override
    override fun canBeActivated(): Boolean {
        return false
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        return false
    }
}