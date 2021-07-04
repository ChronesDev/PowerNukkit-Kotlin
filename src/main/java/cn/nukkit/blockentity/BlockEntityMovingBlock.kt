package cn.nukkit.blockentity

import cn.nukkit.api.DeprecationDetails

/**
 * @author CreeperFace
 * @since 11.4.2017
 */
class BlockEntityMovingBlock(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    var movingBlockString: String? = null
        protected set
    protected override var block: Block? = null
    protected var piston: BlockVector3? = null
    @Override
    protected override fun initBlockEntity() {
        if (namedTag.contains("movingBlock")) {
            val blockData: CompoundTag = namedTag.getCompound("movingBlock")
            movingBlockString = blockData.getString("name")
            block = Block.get(blockData.getInt("id"), blockData.getInt("meta"))
        } else {
            this.close()
        }
        if (namedTag.contains("pistonPosX") && namedTag.contains("pistonPosY") && namedTag.contains("pistonPosZ")) {
            piston = BlockVector3(namedTag.getInt("pistonPosX"), namedTag.getInt("pistonPosY"), namedTag.getInt("pistonPosZ"))
        } else {
            piston = BlockVector3(0, -1, 0)
        }
        super.initBlockEntity()
    }

    @get:DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "renamed", replaceWith = "getMovingBlockEntityCompound()")
    @get:Deprecated
    val blockEntity: CompoundTag?
        get() = movingBlockEntityCompound

    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val movingBlockEntityCompound: CompoundTag?
        get() = if (this.namedTag.contains("movingEntity")) {
            this.namedTag.getCompound("movingEntity")
        } else null
    val movingBlock: Block?
        get() = block

    fun moveCollidedEntities(piston: BlockEntityPistonArm, moveDirection: BlockFace?) {
        var bb: AxisAlignedBB = block.getBoundingBox()
        if (bb == null) {
            return
        }
        bb = bb.getOffsetBoundingBox(
                this.x + piston.progress * moveDirection.getXOffset() - moveDirection.getXOffset(),
                this.y + piston.progress * moveDirection.getYOffset() - moveDirection.getYOffset(),
                this.z + piston.progress * moveDirection.getZOffset() - moveDirection.getZOffset()
        )
        val entities: Array<Entity> = this.level.getCollidingEntities(bb)
        for (entity in entities) {
            piston.moveEntity(entity, moveDirection)
        }
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() = this.level.getBlockIdAt(getFloorX(), getFloorY(), getFloorZ()) === BlockID.MOVING_BLOCK
}