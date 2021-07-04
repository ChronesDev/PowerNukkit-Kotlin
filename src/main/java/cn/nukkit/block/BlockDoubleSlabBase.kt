package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
abstract class BlockDoubleSlabBase @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockSolidMeta(meta) {
    @get:Override
    override val name: String
        get() = "Double $slabName Slab"

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = BlockSlab.SIMPLE_SLAB_PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val slabName: String

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    abstract val singleSlabId: Int

    @Override
    override fun toItem(): Item {
        return getCurrentState().forItem().withBlockId(singleSlabId).asItemBlock()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun isCorrectTool(item: Item?): Boolean {
        return canHarvestWithHand() || canHarvest(item)
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return if (isCorrectTool(item)) {
            val slab: Item = toItem()
            slab.setCount(2)
            arrayOf<Item>(slab)
        } else {
            Item.EMPTY_ARRAY
        }
    }
}