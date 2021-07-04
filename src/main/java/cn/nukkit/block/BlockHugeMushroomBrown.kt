package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author Pub4Game
 * @since 28.01.2016
 */
class BlockHugeMushroomBrown @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val name: String
        get() = "Brown Mushroom Block"

    @get:Override
    override val id: Int
        get() = BROWN_MUSHROOM_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val hardness: Double
        get() = 0.2

    @get:Override
    override val resistance: Double
        get() = 1

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return if (NukkitRandom().nextRange(1, 20) === 0) {
            arrayOf<Item>(
                    ItemBlock(Block.get(BlockID.BROWN_MUSHROOM))
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val HUGE_MUSHROOM: IntBlockProperty = IntBlockProperty("huge_mushroom_bits", true, 15)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(HUGE_MUSHROOM)
    }
}