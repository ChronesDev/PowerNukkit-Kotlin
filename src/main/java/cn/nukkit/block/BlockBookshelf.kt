package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author Nukkit Project Team
 */
class BlockBookshelf @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    override val name: String
        get() = "Bookshelf"

    @get:Override
    override val id: Int
        get() = BOOKSHELF

    @get:Override
    override val hardness: Double
        get() = 1.5

    @get:Override
    override val resistance: Double
        get() = 7.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val burnChance: Int
        get() = 30

    @get:Override
    override val burnAbility: Int
        get() = 20

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                Item.get(ItemID.BOOK, 0, 3)
        )
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }
}