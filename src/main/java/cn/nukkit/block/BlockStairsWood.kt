package cn.nukkit.block

import cn.nukkit.item.Item

/**
 * @author xtypr
 * @since 2015/11/25
 */
class BlockStairsWood @JvmOverloads constructor(meta: Int = 0) : BlockStairs(meta) {
    @get:Override
    override val id: Int
        get() = WOOD_STAIRS

    @get:Override
    override val name: String
        get() = "Wood Stairs"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val burnChance: Int
        get() = 5

    @get:Override
    override val burnAbility: Int
        get() = 20

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                toItem()
        )
    }
}