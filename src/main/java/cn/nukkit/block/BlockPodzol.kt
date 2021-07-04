package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/11/22
 */
class BlockPodzol @JvmOverloads constructor(meta: Int = 0) : BlockDirt(0) {
    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = CommonBlockProperties.EMPTY_PROPERTIES

    @get:Override
    override val id: Int
        get() = PODZOL

    @get:Override
    override val name: String
        get() = "Podzol"

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    override var dirtType: Optional<DirtType>
        get() = Optional.empty()
        set(dirtType) {
            if (dirtType != null) {
                throw InvalidBlockPropertyValueException(DIRT_TYPE, null, dirtType, "$name don't support DirtType")
            }
        }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (!this.up().canBeReplaced()) {
            return false
        }
        if (item.isShovel()) {
            item.useOn(this)
            this.getLevel().setBlock(this, Block.get(BlockID.GRASS_PATH))
            if (player != null) {
                player.getLevel().addSound(player, Sound.USE_GRASS)
            }
            return true
        }
        return false
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.SPRUCE_BLOCK_COLOR
}