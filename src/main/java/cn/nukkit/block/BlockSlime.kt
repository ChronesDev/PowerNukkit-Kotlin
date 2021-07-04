package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author Pub4Game
 * @since 21.02.2016
 */
class BlockSlime : BlockSolid() {
    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val name: String
        get() = "Slime Block"

    @get:Override
    override val id: Int
        get() = SLIME_BLOCK

    @get:Override
    override val resistance: Double
        get() = 0

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GRASS_BLOCK_COLOR

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val lightFilter: Int
        get() = 1
}