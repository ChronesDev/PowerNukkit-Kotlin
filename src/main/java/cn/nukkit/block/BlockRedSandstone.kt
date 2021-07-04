package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
class BlockRedSandstone @JvmOverloads constructor(meta: Int = 0) : BlockSandstone(meta) {
    @get:Override
    override val id: Int
        get() = RED_SANDSTONE

    @get:Override
    override val name: String
        get() {
            val names = arrayOf(
                    "Red Sandstone",
                    "Chiseled Red Sandstone",
                    "Smooth Red Sandstone",
                    ""
            )
            return names[this.getDamage() and 0x03]
        }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, this.getDamage() and 0x03)
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ORANGE_BLOCK_COLOR
}