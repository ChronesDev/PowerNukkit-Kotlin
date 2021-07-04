package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 7.8.2017
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockJukebox : BlockSolid(), BlockEntityHolder<BlockEntityJukebox?> {
    @get:Override
    override val name: String
        get() = "Jukebox"

    @get:Override
    override val id: Int
        get() = JUKEBOX

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityJukebox?>
        get() = BlockEntityJukebox::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.JUKEBOX

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @get:Override
    override val hardness: Double
        get() = 1

    @Override
    override fun onActivate(@Nonnull item: Item, @Nullable player: Player?): Boolean {
        val jukebox: BlockEntityJukebox = getOrCreateBlockEntity()
        if (jukebox.getRecordItem().getId() !== 0) {
            jukebox.dropItem()
            return true
        }
        if (!item.isNull() && item is ItemRecord) {
            val record: Item = item.clone()
            record.count = 1
            item.count--
            jukebox.setRecordItem(record)
            jukebox.play()
            return true
        }
        return false
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.DIRT_BLOCK_COLOR
}