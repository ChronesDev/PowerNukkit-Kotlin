package cn.nukkit.block

import cn.nukkit.api.DeprecationDetails

/**
 * @author xtypr
 * @since 2015/12/1
 */
class BlockLeaves2 @JvmOverloads constructor(meta: Int = 0) : BlockLeaves(meta) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = NEW_LEAF_PROPERTIES

    @get:Override
    @set:Override
    override var type: WoodType
        get() = getPropertyValue(NEW_LEAF_TYPE)
        set(type) {
            setPropertyValue(NEW_LEAF_TYPE, type)
        }

    @get:Override
    override val id: Int
        get() = LEAVES2

    @Override
    protected override fun canDropApple(): Boolean {
        return type === WoodType.DARK_OAK
    }

    @get:Override
    protected override val sapling: Item
        protected get() = Item.get(BlockID.SAPLING, getIntValue(NEW_LEAF_TYPE) + 4)

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NEW_LEAF_TYPE: ArrayBlockProperty<WoodType> = ArrayBlockProperty("new_leaf_type", true, arrayOf<WoodType>(
                WoodType.ACACIA, WoodType.DARK_OAK
        ), 2)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val NEW_LEAF_PROPERTIES: BlockProperties = BlockProperties(NEW_LEAF_TYPE, PERSISTENT, UPDATE)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
        val ACACIA = 0

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", reason = "Magic value. Use the accessors instead")
        val DARK_OAK = 1
    }
}