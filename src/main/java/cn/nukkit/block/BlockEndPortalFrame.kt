package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
class BlockEndPortalFrame @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable {
    @get:Override
    override val id: Int
        get() = END_PORTAL_FRAME

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val resistance: Double
        get() = 18000000

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @get:Override
    override val lightLevel: Int
        get() = 1

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val name: String
        get() = "End Portal Frame"

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @get:Override
    override val maxY: Double
        get() = this.y + if (this.getDamage() and 0x04 > 0) 1 else 0.8125

    @Override
    override fun canBePushed(): Boolean {
        return false
    }

    @Override
    override fun canBePulled(): Boolean {
        return false
    }

    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    override val comparatorInputOverride: Int
        get() = if (getDamage() and 4 !== 0) 15 else 0

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (this.getDamage() and 0x04 === 0 && player != null && item.getId() === Item.ENDER_EYE) {
            this.setDamage(this.getDamage() + 4)
            this.getLevel().setBlock(this, this, true, true)
            this.getLevel().addSound(this, Sound.BLOCK_END_PORTAL_FRAME_FILL)
            //this.createPortal(); TODO Re-enable this after testing
            return true
        }
        return false
    }

    @Since("1.3.0.0-PN")
    fun createPortal() {
        val centerSpot: Vector3? = searchCenter(ArrayList())
        if (centerSpot != null) {
            for (x in -2..2) {
                for (z in -2..2) {
                    if ((x == -2 || x == 2) && (z == -2 || z == 2)) continue
                    if (x == -2 || x == 2 || z == -2 || z == 2) {
                        if (!this.checkFrame(this.getLevel().getBlock(centerSpot.add(x, 0, z)), x, z)) {
                            return
                        }
                    }
                }
            }
            for (x in -1..1) {
                for (z in -1..1) {
                    val vector3: Vector3 = centerSpot.add(x, 0, z)
                    if (this.getLevel().getBlock(vector3).getId() !== Block.AIR) {
                        this.getLevel().useBreakOn(vector3)
                    }
                    this.getLevel().setBlock(vector3, Block.get(Block.END_PORTAL))
                }
            }
        }
    }

    private fun searchCenter(visited: List<Block>): Vector3? {
        for (x in -2..2) {
            if (x == 0) continue
            var block: Block = this.getLevel().getBlock(this.add(x, 0, 0))
            val iBlock: Block = this.getLevel().getBlock(this.add(x * 2, 0, 0))
            if (this.checkFrame(block) && !visited.contains(block)) {
                visited.add(block)
                if ((x == -1 || x == 1) && this.checkFrame(iBlock)) return (block as BlockEndPortalFrame).searchCenter(visited)
                for (z in -4..4) {
                    if (z == 0) continue
                    block = this.getLevel().getBlock(this.add(x, 0, z))
                    if (this.checkFrame(block)) {
                        return this.add(x / 2, 0, z / 2)
                    }
                }
            }
        }
        for (z in -2..2) {
            if (z == 0) continue
            var block: Block = this.getLevel().getBlock(this.add(0, 0, z))
            val iBlock: Block = this.getLevel().getBlock(this.add(0, 0, z * 2))
            if (this.checkFrame(block) && !visited.contains(block)) {
                visited.add(block)
                if ((z == -1 || z == 1) && this.checkFrame(iBlock)) return (block as BlockEndPortalFrame).searchCenter(visited)
                for (x in -4..4) {
                    if (x == 0) continue
                    block = this.getLevel().getBlock(this.add(x, 0, z))
                    if (this.checkFrame(block)) {
                        return this.add(x / 2, 0, z / 2)
                    }
                }
            }
        }
        return null
    }

    private fun checkFrame(block: Block): Boolean {
        return block.getId() === id && block.getDamage() and 4 === 4
    }

    private fun checkFrame(block: Block, x: Int, z: Int): Boolean {
        return block.getId() === id && block.getDamage() - 4 === if (x == -2) 3 else if (x == 2) 1 else if (z == -2) 0 else if (z == 2) 2 else -1
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        this.setDamage(FACES.get(if (player != null) player.getDirection().getHorizontalIndex() else 0))
        this.getLevel().setBlock(block, this, true)
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.GREEN_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val END_PORTAL_EYE: BooleanBlockProperty = BooleanBlockProperty("end_portal_eye_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, END_PORTAL_EYE)
        private val FACES = intArrayOf(2, 3, 0, 1)
    }
}