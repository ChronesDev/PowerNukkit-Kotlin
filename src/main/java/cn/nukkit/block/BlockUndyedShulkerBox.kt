/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.nukkit.block

import cn.nukkit.Player

/**
 *
 * @author Reece Mackie
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockUndyedShulkerBox : BlockTransparent(), BlockEntityHolder<BlockEntityShulkerBox?> {
    @get:Override
    override val id: Int
        get() = UNDYED_SHULKER_BOX

    @get:Override
    override val name: String
        get() = "Shulker Box"

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityShulkerBox?>
        get() = BlockEntityShulkerBox::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.SHULKER_BOX

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 10

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_PICKAXE

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun toItem(): Item {
        val item = ItemBlock(this, this.getDamage(), 1)
        val tile: BlockEntityShulkerBox = getBlockEntity() ?: return item
        val inv: ShulkerBoxInventory = tile.getRealInventory()
        if (!inv.isEmpty()) {
            var nbt: CompoundTag? = item.getNamedTag()
            if (nbt == null) {
                nbt = CompoundTag("")
            }
            val items: ListTag<CompoundTag> = ListTag()
            for (it in 0 until inv.getSize()) {
                if (!inv.getItem(it).isNull()) {
                    val d: CompoundTag = NBTIO.putItemHelper(inv.getItem(it), it)
                    items.add(d)
                }
            }
            nbt.put("Items", items)
            item.setCompoundTag(nbt)
        }
        if (tile.hasName()) {
            item.setCustomName(tile.getName())
        }
        return item
    }

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        val nbt: CompoundTag = CompoundTag().putByte("facing", face.getIndex())
        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName())
        }
        val t: CompoundTag = item.getNamedTag()

        // This code gets executed when the player has broken the shulker box and placed it back (©Kevims 2020)
        if (t != null && t.contains("Items")) {
            nbt.putList(t.getList("Items"))
        }

        // This code gets executed when the player has copied the shulker box in creative mode (©Kevims 2020)
        if (item.hasCustomBlockData()) {
            val customData: Map<String, Tag> = item.getCustomBlockData().getTags()
            for (tag in customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue())
            }
        }
        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null
    }

    @Override
    override fun canHarvestWithHand(): Boolean {
        return false
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, @Nullable player: Player?): Boolean {
        if (player == null) {
            return false
        }
        val box: BlockEntityShulkerBox = getOrCreateBlockEntity()
        val block: Block = this.getSide(BlockFace.fromIndex(box.namedTag.getByte("facing")))
        if (block !is BlockAir && block !is BlockLiquid && block !is BlockFlowable) {
            return false
        }
        player.addWindow(box.getInventory())
        return true
    }

    @Override
    override fun hasComparatorInputOverride(): Boolean {
        return true
    }

    @get:Override
    override val comparatorInputOverride: Int
        get() {
            val be: BlockEntityShulkerBox = getBlockEntity() ?: return 0
            return ContainerInventory.calculateRedstone(be.getInventory())
        }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.PURPLE_BLOCK_COLOR

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @get:Override
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val itemMaxStackSize: Int
        get() = 1
}