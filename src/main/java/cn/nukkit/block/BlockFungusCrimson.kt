package cn.nukkit.block

import cn.nukkit.Player

@Since("1.4.0.0-PN")
@PowerNukkitOnly
class BlockFungusCrimson @Since("1.4.0.0-PN") @PowerNukkitOnly constructor() : BlockFungus() {
    @get:Override
    override val id: Int
        get() = CRIMSON_FUNGUS

    @get:Override
    override val name: String
        get() = "Crimson Fungus"

    @Override
    protected fun canGrowOn(support: Block): Boolean {
        return support.getId() === CRIMSON_NYLIUM
    }

    @Override
    override fun grow(@Nullable cause: Player?): Boolean {
        // TODO Make it grow
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.NETHERRACK_BLOCK_COLOR
}