package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
class BlockTrappedChest @JvmOverloads constructor(meta: Int = 0) : BlockChest(meta) {
    @get:Override
    override val id: Int
        get() = TRAPPED_CHEST

    @get:Override
    override val name: String
        get() = "Trapped Chest"

    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val faces = intArrayOf(2, 5, 3, 4)
        var chest: BlockEntityChest? = null
        this.setDamage(faces.get(if (player != null) player.getDirection().getHorizontalIndex() else 0))
        for (side in Plane.HORIZONTAL) {
            if ((this.getDamage() === 4 || this.getDamage() === 5) && (side === BlockFace.WEST || side === BlockFace.EAST)) {
                continue
            } else if ((this.getDamage() === 3 || this.getDamage() === 2) && (side === BlockFace.NORTH || side === BlockFace.SOUTH)) {
                continue
            }
            val c: Block = this.getSide(side)
            if (c is BlockTrappedChest && c.getDamage() === this.getDamage()) {
                val blockEntity: BlockEntity = this.getLevel().getBlockEntity(c)
                if (blockEntity is BlockEntityChest && !(blockEntity as BlockEntityChest).isPaired()) {
                    chest = blockEntity as BlockEntityChest
                    break
                }
            }
        }
        this.getLevel().setBlock(block, this, true, true)
        val nbt: CompoundTag = CompoundTag("")
                .putList(ListTag("Items"))
                .putString("id", BlockEntity.CHEST)
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName())
        }
        if (item.hasCustomBlockData()) {
            val customData: Map<String, Tag> = item.getCustomBlockData().getTags()
            for (tag in customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue())
            }
        }
        val blockEntity: BlockEntityChest = BlockEntity.createBlockEntity(BlockEntity.CHEST, this.getLevel().getChunk(this.x as Int shr 4, this.z as Int shr 4), nbt) as BlockEntityChest
                ?: return false
        if (chest != null) {
            chest.pairWith(blockEntity)
            blockEntity.pairWith(chest)
        }
        return true
    }

    @Override
    override fun getWeakPower(face: BlockFace?): Int {
        var playerCount = 0
        val blockEntity: BlockEntity = this.level.getBlockEntity(this)
        if (blockEntity is BlockEntityChest) {
            playerCount = (blockEntity as BlockEntityChest).getInventory().getViewers().size()
        }
        return Math.min(playerCount, 15)
    }

    @Override
    override fun getStrongPower(side: BlockFace): Int {
        return if (side === BlockFace.UP) getWeakPower(side) else 0
    }

    @get:Override
    override val isPowerSource: Boolean
        get() = true
}