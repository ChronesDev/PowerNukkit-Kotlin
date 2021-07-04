package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 09.01.2016
 */
class BlockSugarcane @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta) {
    @get:Override
    override val name: String
        get() = "Sugarcane"

    @get:Override
    override val id: Int
        get() = SUGARCANE_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun toItem(): Item {
        return ItemSugarcane()
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isFertilizer()) { //Bonemeal
            var count = 1
            for (i in 1..2) {
                val id: Int = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ())
                if (id == SUGARCANE_BLOCK) {
                    count++
                }
            }
            if (count < 3) {
                var success = false
                val toGrow = 3 - count
                for (i in 1..toGrow) {
                    val block: Block = this.up(i)
                    if (block.getId() === 0) {
                        val ev = BlockGrowEvent(block, Block.get(BlockID.SUGARCANE_BLOCK))
                        Server.getInstance().getPluginManager().callEvent(ev)
                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState(), true)
                            success = true
                        }
                    } else if (block.getId() !== SUGARCANE_BLOCK) {
                        break
                    }
                }
                if (success) {
                    if (player != null && player.gamemode and 0x01 === 0) {
                        item.count--
                    }
                    this.level.addParticle(BoneMealParticle(this))
                }
            }
            return true
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        val level: Level = getLevel()
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 0)
            return type
        }
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isSupportValid) {
                level.useBreakOn(this)
            }
            return type
        }
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!isSupportValid) {
                level.scheduleUpdate(this, 0)
                return type
            }
            if (getDamage() < 15) {
                setDamage(this.getDamage() + 1)
                level.setBlock(this, this, false)
                return type
            }
            val up: Block = up()
            if (up.getId() !== AIR) {
                return type
            }
            var height = 0
            var current: Block = this
            while (height < 3 && current.getId() === SUGARCANE_BLOCK) {
                current = current.down()
                height++
            }
            if (height >= 3) {
                return type
            }
            val ev = BlockGrowEvent(up, Block.get(BlockID.SUGARCANE_BLOCK))
            Server.getInstance().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return type
            }
            if (!level.setBlock(up, Block.get(BlockID.SUGARCANE_BLOCK), false)) {
                return type
            }
            setDamage(0)
            level.setBlock(this, this, false)
            return type
        }
        return 0
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (block.getId() !== AIR) {
            return false
        }
        if (isSupportValid) {
            this.getLevel().setBlock(block, this, true)
            return true
        }
        return false
    }

    /**
     * @since 1.2.0.2-PN
     */
    private val isSupportValid: Boolean
        private get() {
            val down: Block = this.down()
            val downId: Int = down.getId()
            if (downId == SUGARCANE_BLOCK) {
                return true
            }
            if (downId != GRASS && downId != DIRT && downId != SAND || down.getId() === PODZOL) {
                return false
            }
            for (face in BlockFace.Plane.HORIZONTAL) {
                val possibleWater: Block = down.getSide(face)
                if (possibleWater is BlockWater
                        || possibleWater is BlockIceFrosted
                        || possibleWater.getLevelBlockAtLayer(1) is BlockWater) {
                    return true
                }
            }
            return false
        }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val AGE: IntBlockProperty = CommonBlockProperties.AGE_15

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(AGE)
    }
}