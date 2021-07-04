package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author xtypr
 * @since 2016/1/5
 * The name NetherPortalBlock comes from minecraft wiki.
 */
class BlockNetherPortal @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(0), Faceable {
    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @get:Override
    override val name: String
        get() = "Nether Portal Block"

    @get:Override
    override val id: Int
        get() = NETHER_PORTAL

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun isBreakable(item: Item?): Boolean {
        return false
    }

    @get:Override
    override val hardness: Double
        get() = (-1).toDouble()

    @get:Override
    override val lightLevel: Int
        get() = 11

    @Override
    override fun toItem(): Item {
        return ItemBlock(Block.get(BlockID.AIR))
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        var result: Boolean = super.onBreak(item)
        for (face in BlockFace.values()) {
            val b: Block = this.getSide(face)
            if (b != null) {
                if (b is BlockNetherPortal) {
                    result = result and b.onBreak(item)
                }
            }
        }
        return result
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        return this
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    companion object {
        private val PORTAL_AXIS: ArrayBlockProperty<String> = ArrayBlockProperty("portal_axis", false, arrayOf("unknown", "x", "z"))

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(PORTAL_AXIS)
        fun spawnPortal(pos: Position) {
            val lvl: Level = pos.level
            val x: Int = pos.getFloorX()
            var y: Int = pos.getFloorY()
            var z: Int = pos.getFloorZ()
            val air: Block = Block.get(AIR)
            val obsidian: Block = Block.get(OBSIDIAN)
            val netherPortal: Block = Block.get(NETHER_PORTAL)
            for (xx in -1..3) {
                for (yy in 1..3) {
                    for (zz in -1..2) {
                        lvl.setBlock(x + xx, y + yy, z + zz, air, false, true)
                    }
                }
            }
            lvl.setBlock(x + 1, y, z, obsidian, false, true)
            lvl.setBlock(x + 2, y, z, obsidian, false, true)
            z++
            lvl.setBlock(x, y, z, obsidian, false, true)
            lvl.setBlock(x + 1, y, z, obsidian, false, true)
            lvl.setBlock(x + 2, y, z, obsidian, false, true)
            lvl.setBlock(x + 3, y, z, obsidian, false, true)
            z++
            lvl.setBlock(x + 1, y, z, obsidian, false, true)
            lvl.setBlock(x + 2, y, z, obsidian, false, true)
            z--
            for (i in 0..2) {
                y++
                lvl.setBlock(x, y, z, obsidian, false, true)
                lvl.setBlock(x + 1, y, z, netherPortal, false, true)
                lvl.setBlock(x + 2, y, z, netherPortal, false, true)
                lvl.setBlock(x + 3, y, z, obsidian, false, true)
            }
            y++
            lvl.setBlock(x, y, z, obsidian, false, true)
            lvl.setBlock(x + 1, y, z, obsidian, false, true)
            lvl.setBlock(x + 2, y, z, obsidian, false, true)
            lvl.setBlock(x + 3, y, z, obsidian, false, true)
        }
    }
}