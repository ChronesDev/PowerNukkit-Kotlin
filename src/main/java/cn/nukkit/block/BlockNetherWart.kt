package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Leonidius20
 * @since 22.03.17
 */
class BlockNetherWart @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta) {
    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val down: Block = this.down()
        if (down.getId() === SOUL_SAND) {
            this.getLevel().setBlock(block, this, true, true)
            return true
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() !== SOUL_SAND) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (Random().nextInt(10) === 1) {
                if (this.getDamage() < 0x03) {
                    val block = this.clone() as BlockNetherWart
                    block.setDamage(block.getDamage() + 1)
                    val ev = BlockGrowEvent(this, block)
                    Server.getInstance().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), true, true)
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM
            }
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.RED_BLOCK_COLOR

    @get:Override
    override val name: String
        get() = "Nether Wart Block"

    @get:Override
    override val id: Int
        get() = NETHER_WART_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return if (this.getDamage() === 0x03) {
            arrayOf<Item>(
                    ItemNetherWart(0, 2 + (Math.random() * (4 - 2 + 1)) as Int)
            )
        } else {
            arrayOf<Item>(
                    ItemNetherWart()
            )
        }
    }

    @Override
    override fun toItem(): Item {
        return ItemNetherWart()
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val AGE: IntBlockProperty = IntBlockProperty("age", false, 3)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(AGE)
    }
}