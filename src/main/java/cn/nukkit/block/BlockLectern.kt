package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockLectern @PowerNukkitOnly constructor(meta: Int) : BlockTransparentMeta(meta), RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityLectern?> {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val name: String
        get() = "Lectern"

    @get:Override
    override val id: Int
        get() = LECTERN

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityLectern?>
        get() = BlockEntityLectern::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.LECTERN

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 12.5

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val maxY: Double
        get() = y + 0.89999

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            var power = 0
            var page = 0
            var maxPage = 0
            val lectern: BlockEntityLectern = getBlockEntity()
            if (lectern != null && lectern.hasBook()) {
                maxPage = lectern.getTotalPages()
                page = lectern.getLeftPage() + 1
                power = (page.toFloat() / maxPage * 16).toInt()
            }
            return power
        }

    @get:Override
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    @set:Override
    var blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(getDamage() and 3)
        set(face) {
            val horizontalIndex: Int = face.getHorizontalIndex()
            if (horizontalIndex >= 0) {
                setDamage(getDamage() and (DATA_MASK xor 3) or (horizontalIndex and 3))
            }
        }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        blockFace = if (player != null) player.getDirection().getOpposite() else BlockFace.SOUTH
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onTouch(@Nullable player: Player?, action: Action): Int {
        if (action === Action.LEFT_CLICK_BLOCK && (player == null || !player.isCreative() && !player.isSpectator())) {
            dropBook(player)
        }
        return super.onTouch(player, action)
    }

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        val lectern: BlockEntityLectern = getOrCreateBlockEntity()
        val currentBook: Item = lectern.getBook()
        if (!currentBook.isNull()) {
            return false
        }
        if (item.getId() !== ItemID.WRITTEN_BOOK && item.getId() !== ItemID.BOOK_AND_QUILL) {
            return false
        }
        if (player == null || !player.isCreative()) {
            item.count--
        }
        val newBook: Item = item.clone()
        newBook.setCount(1)
        lectern.setBook(newBook)
        lectern.spawnToAll()
        this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.ITEM_BOOK_PUT)
        return true
    }

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var isActivated: Boolean
        get() = this.getDamage() and 0x04 === 0x04
        set(activated) {
            if (activated) {
                setDamage(getDamage() or 0x04)
            } else {
                setDamage(getDamage() xor 0x04)
            }
        }

    @PowerNukkitDifference(info = "Down side is strongly powered.", since = "1.4.0.0-PN")
    fun executeRedstonePulse() {
        if (isActivated) {
            level.cancelSheduledUpdate(this, this)
        } else {
            this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 0, 15))
        }
        level.scheduleUpdate(this, this, 4)
        isActivated = true
        level.setBlock(this, this, true, false)
        level.addSound(this.add(0.5, 0.5, 0.5), Sound.ITEM_BOOK_PAGE_TURN)
        updateAroundRedstone()
        RedstoneComponent.updateAroundRedstone(getSide(BlockFace.DOWN), BlockFace.UP)
    }

    @Override
    override fun getWeakPower(face: BlockFace?): Int {
        return if (isActivated) 15 else 0
    }

    @Override
    @PowerNukkitDifference(info = "Down side is strongly powered.", since = "1.4.0.0-PN")
    override fun getStrongPower(face: BlockFace): Int {
        return if (face === BlockFace.DOWN) getWeakPower(face) else 0
    }

    @Override
    @PowerNukkitDifference(info = "Down side is strongly powered.", since = "1.4.0.0-PN")
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (isActivated) {
                this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 15, 0))
                isActivated = false
                level.setBlock(this, this, true, false)
                updateAroundRedstone()
                RedstoneComponent.updateAroundRedstone(getSide(BlockFace.DOWN), BlockFace.UP)
            }
            return Level.BLOCK_UPDATE_SCHEDULED
        }
        return 0
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.WOOD_BLOCK_COLOR

    @PowerNukkitOnly
    fun dropBook(player: Player?) {
        val lectern: BlockEntityLectern = getBlockEntity() ?: return
        val book: Item = lectern.getBook()
        if (book.isNull()) {
            return
        }
        val dropBookEvent = LecternDropBookEvent(player, lectern, book)
        this.getLevel().getServer().getPluginManager().callEvent(dropBookEvent)
        if (dropBookEvent.isCancelled()) {
            return
        }
        lectern.setBook(Item.getBlock(BlockID.AIR))
        lectern.spawnToAll()
        this.level.dropItem(lectern.add(0.5f, 0.6f, 0.5f), dropBookEvent.getBook())
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, POWERED)
    }
}