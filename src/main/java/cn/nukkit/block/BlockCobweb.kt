package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockCobweb @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(0) {
    @get:Override
    override val name: String
        get() = "Cobweb"

    @get:Override
    override val id: Int
        get() = COBWEB

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 4

    @get:Override
    override val resistance: Double
        get() = 20

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_SWORD

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun onEntityCollide(entity: Entity) {
        entity.resetFallDistance()
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isShears()) {
            arrayOf<Item>(
                    this.toItem()
            )
        } else if (item.isSword()) {
            arrayOf<Item>(
                    ItemString()
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.CLOTH_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun diffusesSkyLight(): Boolean {
        return true
    }
}