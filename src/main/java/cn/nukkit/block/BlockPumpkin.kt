package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/8
 */
class BlockPumpkin @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta), Faceable {
    @get:Override
    override val name: String
        get() = "Pumpkin"

    @get:Override
    override val id: Int
        get() = PUMPKIN

    @get:Override
    @get:Nonnull
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 1

    @get:Override
    override val resistance: Double
        get() = 1

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isShears()) {
            val carvedPumpkin = BlockCarvedPumpkin()
            // TODO: Use the activated block face not the player direction
            if (player == null) {
                carvedPumpkin.setBlockFace(BlockFace.SOUTH)
            } else {
                carvedPumpkin.setBlockFace(player.getDirection().getOpposite())
            }
            item.useOn(this)
            this.level.setBlock(this, carvedPumpkin, true, true)
            this.getLevel().dropItem(add(0.5, 0.5, 0.5), Item.get(ItemID.PUMPKIN_SEEDS)) // TODO: Get correct drop item position
            return true
        }
        return false
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        blockFace = if (player == null) {
            BlockFace.SOUTH
        } else {
            player.getDirection().getOpposite()
        }
        this.level.setBlock(block, this, true, true)
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.ORANGE_BLOCK_COLOR

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @get:Override
    @set:Override
    var blockFace: BlockFace
        get() = getPropertyValue(DIRECTION)
        set(face) {
            setPropertyValue(DIRECTION, face)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(
                DIRECTION
        )
    }
}