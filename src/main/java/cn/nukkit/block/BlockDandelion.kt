package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2015/12/2
 */
class BlockDandelion @JvmOverloads constructor(meta: Int = 0) : BlockFlower(0) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    override val id: Int
        get() = DANDELION

    @get:Override
    protected override val uncommonFlower: cn.nukkit.block.Block
        protected get() = get(POPPY)

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    override var flowerType: SmallFlowerType
        get() = SmallFlowerType.DANDELION
        set(flowerType) {
            setOnSingleFlowerType(SmallFlowerType.DANDELION, flowerType)
        }
}