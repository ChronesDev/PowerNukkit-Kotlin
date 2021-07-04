package cn.nukkit.block

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Angelic47 (Nukkit Project)
 * @apiNote Extends BlockSolidMeta instead of BlockSolid only in PowerNukkit
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Extends BlockSolidMeta instead of BlockSolid only in PowerNukkit")
class BlockBedrock @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockSolidMeta(meta) {
    constructor() : this(0) {}

    @get:Override
    override val id: Int
        get() = BEDROCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var burnIndefinitely: Boolean
        get() = getBooleanValue(INFINIBURN)
        set(infiniburn) {
            setBooleanValue(INFINIBURN, infiniburn)
        }

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @get:Override
    override val resistance: Double
        get() = 18000000

    @get:Override
    override val name: String
        get() = "Bedrock"

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val INFINIBURN: BooleanBlockProperty = BooleanBlockProperty("infiniburn_bit", true)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(INFINIBURN)
    }
}