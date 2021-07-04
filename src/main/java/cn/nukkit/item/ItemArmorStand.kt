package cn.nukkit.item

import cn.nukkit.Player

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class ItemArmorStand @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Integer?, count: Int) : Item(ARMOR_STAND, meta, count, "Armor Stand") {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(meta: Integer?) : this(meta, 1) {
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun getMaxStackSize(): Int {
        return 64
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block?, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        if (player.isAdventure()) {
            return false
        }
        val chunk: FullChunk = block.getChunk() ?: return false
        if (!block.canBeReplaced() || !block.up().canBeReplaced()) {
            return false
        }
        for (collidingEntity in level.getCollidingEntities(SimpleAxisAlignedBB(block.x, block.y, block.z, block.x + 1, block.y + 1, block.z + 1))) {
            if (collidingEntity is EntityArmorStand) {
                return false
            }
        }
        val direction: CompassRoseDirection = CompassRoseDirection.getClosestFromYaw(player.yaw, PRIMARY_INTER_CARDINAL).getOppositeFace()
        val nbt: CompoundTag = Entity.getDefaultNBT(block.add(0.5, 0, 0.5), Vector3(), direction.getYaw(), 0f)
        if (this.hasCustomName()) {
            nbt.putString("CustomName", this.getCustomName())
        }
        if (!removeForPlacement(block) || !removeForPlacement(block.up())) {
            return false
        }
        val entity: Entity = Entity.createEntity(EntityArmorStand.NETWORK_ID, chunk, nbt) ?: return false
        if (!player.isCreative()) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex())
        }
        entity.spawnToAll()
        player.getLevel().addSound(entity, Sound.MOB_ARMOR_STAND_PLACE)
        return true
    }

    /**
     * @param block The block which is in the same space as the armor stand.
     * @return `true` if the armor stand entity can be placed
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected fun removeForPlacement(block: Block): Boolean {
        when (block.getId()) {
            AIR -> return true
            SNOW_LAYER -> return block.canBeReplaced()
            else -> {
            }
        }
        return block.getLevel().setBlock(block, Block.get(BlockID.AIR))
    }
}