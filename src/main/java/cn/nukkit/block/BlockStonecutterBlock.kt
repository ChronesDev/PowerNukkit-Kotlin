package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockStonecutterBlock @PowerNukkitOnly constructor(meta: Int) : BlockTransparentMeta(meta), Faceable {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = STONECUTTER_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Stonecutter"

    @get:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    @set:Override
    var blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x7)
        set(face) {
            val horizontalIndex: Int = face.getHorizontalIndex()
            if (horizontalIndex > -1) {
                setDamage(horizontalIndex)
            }
        }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        val faces = intArrayOf(2, 5, 3, 4)
        this.setDamage(faces.get(if (player != null) player.getDirection().getHorizontalIndex() else 0))
        this.getLevel().setBlock(block, this, true, true)
        return true
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, @Nullable player: Player?): Boolean {
        if (player != null) {
            player.addWindow(StonecutterInventory(player.getUIInventory(), this), ContainerIds.NONE)
            player.craftingType = Player.CRAFTING_STONECUTTER
        }
        return true
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.STONE_BLOCK_COLOR

    @get:Override
    override val hardness: Double
        get() = 3.5

    @get:Override
    override val resistance: Double
        get() = 17.5

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    override val toolTier: Int
        get() = ItemTool.TIER_WOODEN

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(toItem())
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockStonecutterBlock())
    }

    @get:Override
    @get:PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the returned value")
    override val maxY: Double
        get() = y + 9 / 16.0

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES
    }
}