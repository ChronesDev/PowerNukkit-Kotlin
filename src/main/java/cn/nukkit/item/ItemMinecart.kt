package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemMinecart @JvmOverloads constructor(meta: Integer? = 0, count: Int = 1) : Item(MINECART, meta, count, "Minecart") {
    constructor(meta: Integer?) : this(meta, 1) {}

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block?, target: Block, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        if (Rail.isRailBlock(target)) {
            val type: Rail.Orientation = (target as BlockRail).getOrientation()
            var adjacent = 0.0
            if (type.isAscending()) {
                adjacent = 0.5
            }
            val minecart: EntityMinecartEmpty = Entity.createEntity("MinecartRideable",
                    level.getChunk(target.getFloorX() shr 4, target.getFloorZ() shr 4), CompoundTag("")
                    .putList(ListTag("Pos")
                            .add(DoubleTag("", target.getX() + 0.5))
                            .add(DoubleTag("", target.getY() + 0.0625 + adjacent))
                            .add(DoubleTag("", target.getZ() + 0.5)))
                    .putList(ListTag("Motion")
                            .add(DoubleTag("", 0))
                            .add(DoubleTag("", 0))
                            .add(DoubleTag("", 0)))
                    .putList(ListTag("Rotation")
                            .add(FloatTag("", 0))
                            .add(FloatTag("", 0)))
            ) as EntityMinecartEmpty ?: return false
            if (player.isAdventure() || player.isSurvival()) {
                val item: Item = player.getInventory().getItemInHand()
                item.setCount(item.getCount() - 1)
                player.getInventory().setItemInHand(item)
            }
            minecart.spawnToAll()
            return true
        }
        return false
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}