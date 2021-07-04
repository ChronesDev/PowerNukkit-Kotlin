package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockGrindstone @PowerNukkitOnly constructor(meta: Int) : BlockTransparentMeta(meta), Faceable {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = GRINDSTONE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Grindstone"

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockGrindstone())
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val color: BlockColor
        get() = BlockColor.IRON_BLOCK_COLOR

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 6

    @get:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    @set:Override
    var blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(getDamage() and 3)
        set(face) {
            if (face.getHorizontalIndex() === -1) {
                return
            }
            setDamage(getDamage() and (DATA_MASK xor 3) or face.getHorizontalIndex())
        }

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var attachmentType: Int
        get() = getDamage() and 12 shr 2 and 3
        set(attachmentType) {
            var attachmentType = attachmentType
            attachmentType = attachmentType and 3
            setDamage(getDamage() and (DATA_MASK xor 12) or (attachmentType shl 2))
        }

    private fun isConnectedTo(connectedFace: BlockFace, attachmentType: Int, blockFace: BlockFace): Boolean {
        val faceAxis: BlockFace.Axis = connectedFace.getAxis()
        when (attachmentType) {
            TYPE_ATTACHMENT_STANDING -> return if (faceAxis === BlockFace.Axis.Y) {
                connectedFace === BlockFace.DOWN
            } else {
                false
            }
            TYPE_ATTACHMENT_HANGING -> return connectedFace === BlockFace.UP
            TYPE_ATTACHMENT_SIDE, TYPE_ATTACHMENT_MULTIPLE -> return connectedFace === blockFace.getOpposite()
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!checkSupport()) {
                this.level.useBreakOn(this, Item.get(Item.DIAMOND_PICKAXE))
            }
            return type
        }
        return 0
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, player: Player): Boolean {
        var face: BlockFace = face
        if (block.getId() !== AIR && block.canBeReplaced()) {
            face = BlockFace.UP
        }
        when (face) {
            UP -> {
                attachmentType = TYPE_ATTACHMENT_STANDING
                blockFace = player.getDirection().getOpposite()
            }
            DOWN -> {
                attachmentType = TYPE_ATTACHMENT_HANGING
                blockFace = player.getDirection().getOpposite()
            }
            else -> {
                blockFace = face
                attachmentType = TYPE_ATTACHMENT_SIDE
            }
        }
        if (!checkSupport()) {
            return false
        }
        this.level.setBlock(this, this, true, true)
        return true
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private fun checkSupport(): Boolean {
        when (attachmentType) {
            TYPE_ATTACHMENT_STANDING -> if (checkSupport(down())) {
                return true
            }
            TYPE_ATTACHMENT_HANGING -> if (checkSupport(up())) {
                return true
            }
            TYPE_ATTACHMENT_SIDE -> {
                val blockFace: BlockFace = blockFace
                if (checkSupport(getSide(blockFace.getOpposite()))) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkSupport(support: Block): Boolean {
        val id: Int = support.getId()
        return id != AIR && id != BUBBLE_COLUMN && support !is BlockLiquid
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        val attachmentType = attachmentType
        val blockFace: BlockFace = blockFace
        val south = isConnectedTo(BlockFace.SOUTH, attachmentType, blockFace)
        val north = isConnectedTo(BlockFace.NORTH, attachmentType, blockFace)
        val west = isConnectedTo(BlockFace.WEST, attachmentType, blockFace)
        val east = isConnectedTo(BlockFace.EAST, attachmentType, blockFace)
        val up = isConnectedTo(BlockFace.UP, attachmentType, blockFace)
        val down = isConnectedTo(BlockFace.DOWN, attachmentType, blockFace)
        val pixels = 2.0 / 16
        val n: Double = if (north) 0 else pixels
        val s: Double = if (south) 1 else 1 - pixels
        val w: Double = if (west) 0 else pixels
        val e: Double = if (east) 1 else 1 - pixels
        val d: Double = if (down) 0 else pixels
        val u: Double = if (up) 1 else 1 - pixels
        return SimpleAxisAlignedBB(
                this.x + w,
                this.y + d,
                this.z + n,
                this.x + e,
                this.y + u,
                this.z + s
        )
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        if (player != null) {
            player.addWindow(GrindstoneInventory(player.getUIInventory(), this), Player.GRINDSTONE_WINDOW_ID)
        }
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, ATTACHMENT_TYPE)

        @PowerNukkitOnly
        val TYPE_ATTACHMENT_STANDING = 0

        @PowerNukkitOnly
        val TYPE_ATTACHMENT_HANGING = 1

        @PowerNukkitOnly
        val TYPE_ATTACHMENT_SIDE = 2

        @PowerNukkitOnly
        val TYPE_ATTACHMENT_MULTIPLE = 3
    }
}