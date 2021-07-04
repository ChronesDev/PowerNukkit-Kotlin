package cn.nukkit.item

import cn.nukkit.Player

class ItemEndCrystal @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(END_CRYSTAL, meta, count, "End Crystal") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        if (target !is BlockBedrock && target !is BlockObsidian) return false
        val chunk: FullChunk = level.getChunk(block.getX() as Int shr 4, block.getZ() as Int shr 4)
        val entities: Array<Entity> = level.getNearbyEntities(SimpleAxisAlignedBB(target.x, target.y, target.z, target.x + 1, target.y + 2, target.z + 1))
        val up: Block = target.up()
        if (chunk == null || up.getId() !== BlockID.AIR || up.up().getId() !== BlockID.AIR || entities.size != 0) {
            return false
        }
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", target.x + 0.5))
                        .add(DoubleTag("", up.y))
                        .add(DoubleTag("", target.z + 0.5)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("", 0))
                        .add(DoubleTag("", 0))
                        .add(DoubleTag("", 0)))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("", Random().nextFloat() * 360))
                        .add(FloatTag("", 0)))
        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName())
        }
        val entity: Entity = Entity.createEntity("EndCrystal", chunk, nbt)
        if (entity != null) {
            if (player.isAdventure() || player.isSurvival()) {
                val item: Item = player.getInventory().getItemInHand()
                item.setCount(item.getCount() - 1)
                player.getInventory().setItemInHand(item)
            }
            entity.spawnToAll()
            return true
        }
        return false
    }
}