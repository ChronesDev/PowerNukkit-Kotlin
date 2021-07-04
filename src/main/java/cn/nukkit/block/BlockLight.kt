package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
class BlockLight @PowerNukkitOnly constructor(meta: Int) : BlockTransparentMeta(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Light Block"

    @get:Override
    override val id: Int
        get() = LIGHT_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val lightLevel: Int
        get() = getDamage() and 0xF

    @get:Override
    override val boundingBox: AxisAlignedBB?
        get() = null

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    override val hardness: Double
        get() = 0

    @get:Override
    override val resistance: Double
        get() = 0

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun toItem(): Item {
        return Item.get(Item.AIR)
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val LIGHT_LEVEL: IntBlockProperty = IntBlockProperty("block_light_level", true, 15)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(LIGHT_LEVEL)
    }
}