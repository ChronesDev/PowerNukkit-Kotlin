package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class ItemSpawnEgg : Item {
    constructor(meta: Integer?) : this(meta, 1) {}

    @JvmOverloads
    constructor(meta: Integer? = 0, count: Int = 1) : super(SPAWN_EGG, meta, count, "Spawn EntityEgg") {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected constructor(id: Int, meta: Integer?, count: Int, name: String?) : super(id, meta, count, name) {
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        if (player.isAdventure()) {
            return false
        }
        val chunk: FullChunk = level.getChunk(block.getX() as Int shr 4, block.getZ() as Int shr 4)
                ?: return false
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", block.getX() + 0.5))
                        .add(DoubleTag("", if (target.getBoundingBox() == null) block.getY() else target.getBoundingBox().getMaxY() + 0.0001f))
                        .add(DoubleTag("", block.getZ() + 0.5)))
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
        val networkId = getEntityNetworkId()
        val ev = CreatureSpawnEvent(networkId, block, nbt, SpawnReason.SPAWN_EGG)
        level.getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return false
        }
        val entity: Entity = Entity.createEntity(networkId, chunk, nbt)
        if (entity != null) {
            if (player.isSurvival()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex())
            }
            entity.spawnToAll()
            return true
        }
        return false
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    fun getLegacySpawnEgg(): Item? {
        return Item.get(SPAWN_EGG, getEntityNetworkId(), getCount(), getCompoundTag())
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun getEntityNetworkId(): Int {
        return this.meta
    }
}