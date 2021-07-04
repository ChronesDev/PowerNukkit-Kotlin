package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author yescallop
 * @since 2016/2/13
 */
class ItemBoat : Item {
    constructor(meta: Integer?) : this(meta, 1) {}

    @JvmOverloads
    constructor(meta: Integer? = 0, count: Int = 1) : super(BOAT, meta, count, "Boat") {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {
    }

    @Override
    override fun getDamage(): Int {
        return super.getDamage()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getLegacyBoatDamage(): OptionalInt {
        return if (getId() === BOAT) {
            OptionalInt.of(super.getDamage())
        } else {
            OptionalInt.empty()
        }
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block?, face: BlockFace, fx: Double, fy: Double, fz: Double): Boolean {
        if (face !== BlockFace.UP && block !is BlockWater) return false
        val boat: EntityBoat = Entity.createEntity("Boat",
                level.getChunk(block.getFloorX() shr 4, block.getFloorZ() shr 4), CompoundTag("")
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", block.getX() + 0.5))
                        .add(DoubleTag("", block.getY() - if (target is BlockWater) 0.375 else 0))
                        .add(DoubleTag("", block.getZ() + 0.5)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("", 0))
                        .add(DoubleTag("", 0))
                        .add(DoubleTag("", 0)))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("", ((player.yaw + 90f) % 360) as Float))
                        .add(FloatTag("", 0)))
                .putInt("Variant", getLegacyBoatDamage().orElse(0))
        ) as EntityBoat ?: return false
        if (player.isSurvival() || player.isAdventure()) {
            val item: Item = player.getInventory().getItemInHand()
            item.setCount(item.getCount() - 1)
            player.getInventory().setItemInHand(item)
        }
        boat.spawnToAll()
        return true
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 1
    }
}