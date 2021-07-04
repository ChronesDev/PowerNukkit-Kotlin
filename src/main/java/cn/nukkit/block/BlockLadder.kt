package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author xtypr
 * @since 2015/12/8
 */
class BlockLadder @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable {
    @get:Override
    override val name: String
        get() = "Ladder"

    @get:Override
    override val id: Int
        get() = LADDER

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @Override
    override fun canBeClimbed(): Boolean {
        return true
    }

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val hardness: Double
        get() = 0.4

    @get:Override
    override val resistance: Double
        get() = 2
    private var offMinX = 0.0
    private var offMinZ = 0.0
    private var offMaxX = 0.0
    private var offMaxZ = 0.0
    private fun calculateOffsets() {
        val f = 0.1875
        when (this.getDamage()) {
            2 -> {
                offMinX = 0.0
                offMinZ = 1 - f
                offMaxX = 1.0
                offMaxZ = 1.0
            }
            3 -> {
                offMinX = 0.0
                offMinZ = 0.0
                offMaxX = 1.0
                offMaxZ = f
            }
            4 -> {
                offMinX = 1 - f
                offMinZ = 0.0
                offMaxX = 1.0
                offMaxZ = 1.0
            }
            5 -> {
                offMinX = 0.0
                offMinZ = 0.0
                offMaxX = f
                offMaxZ = 1.0
            }
            else -> {
                offMinX = 0.0
                offMinZ = 1.0
                offMaxX = 1.0
                offMaxZ = 1.0
            }
        }
    }

    @Override
    override fun setDamage(meta: Int) {
        super.setDamage(meta)
        calculateOffsets()
    }

    @get:Override
    override val minX: Double
        get() = this.x + offMinX

    @get:Override
    override val minZ: Double
        get() = this.z + offMinZ

    @get:Override
    override val maxX: Double
        get() = this.x + offMaxX

    @get:Override
    override val maxZ: Double
        get() = this.z + offMaxZ

    @Override
    protected override fun recalculateCollisionBoundingBox(): AxisAlignedBB {
        return super.recalculateBoundingBox()
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (face.getHorizontalIndex() === -1 || !isSupportValid(target, face)) {
            return false
        }
        setDamage(face.getIndex())
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    private fun isSupportValid(support: Block, face: BlockFace): Boolean {
        return when (support.getId()) {
            GLASS, GLASS_PANE, STAINED_GLASS, STAINED_GLASS_PANE, BEACON -> false
            else -> BlockLever.isSupportValid(support, face)
        }
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val faces = intArrayOf(
                    0,  //never use
                    1,  //never use
                    3,
                    2,
                    5,
                    4
            )
            val face: BlockFace = BlockFace.fromIndex(faces[this.getDamage()])
            if (!isSupportValid(this.getSide(face), face.getOpposite())) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        }
        return 0
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                Item.get(Item.LADDER, 0, 1)
        )
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES
    }

    init {
        calculateOffsets()
    }
}