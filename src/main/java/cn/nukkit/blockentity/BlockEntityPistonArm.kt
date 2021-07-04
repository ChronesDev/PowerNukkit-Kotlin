package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "The piston will work as close as possible to vanilla")
class BlockEntityPistonArm(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    var progress = 0f
    var lastProgress = 1f
    var facing: BlockFace? = null
    var extending = false
    var sticky = false
    var state = 0
    var newState = 1
    var attachedBlocks: List<BlockVector3>? = null
    var powered = false

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    var finished = true
    @Override
    protected override fun initBlockEntity() {
        if (namedTag.contains("Progress")) {
            progress = namedTag.getFloat("Progress")
        }
        if (namedTag.contains("LastProgress")) {
            lastProgress = namedTag.getInt("LastProgress")
        }
        sticky = namedTag.getBoolean("Sticky")
        extending = namedTag.getBoolean("Extending")
        powered = namedTag.getBoolean("powered")
        if (namedTag.contains("facing")) {
            facing = BlockFace.fromIndex(namedTag.getInt("facing"))
        } else {
            val b: Block = this.getLevelBlock()
            if (b is Faceable) {
                facing = (b as Faceable).getBlockFace()
            } else {
                facing = BlockFace.NORTH
            }
        }
        attachedBlocks = ArrayList()
        if (namedTag.contains("AttachedBlocks")) {
            val blocks: ListTag = namedTag.getList("AttachedBlocks", IntTag::class.java)
            if (blocks != null && blocks.size() > 0) {
                var i = 0
                while (i < blocks.size()) {
                    attachedBlocks.add(BlockVector3(
                            (blocks.get(i) as IntTag).data,
                            (blocks.get(i + 1) as IntTag).data,
                            (blocks.get(i + 1) as IntTag).data
                    ))
                    i += 3
                }
            }
        } else {
            namedTag.putList(ListTag("AttachedBlocks"))
        }
        super.initBlockEntity()
    }

    private fun moveCollidedEntities() {
        val pushDir: BlockFace = if (extending) facing else facing.getOpposite()
        for (pos in attachedBlocks!!) {
            val blockEntity: BlockEntity = this.level.getBlockEntity(pos.getSide(pushDir))
            if (blockEntity is BlockEntityMovingBlock) {
                blockEntity.moveCollidedEntities(this, pushDir)
            }
        }
        val bb: AxisAlignedBB = SimpleAxisAlignedBB(0, 0, 0, 1, 1, 1).getOffsetBoundingBox(
                this.x + pushDir.getXOffset() * progress,
                this.y + pushDir.getYOffset() * progress,
                this.z + pushDir.getZOffset() * progress
        )
        val entities: Array<Entity> = this.level.getCollidingEntities(bb)
        for (entity in entities) {
            moveEntity(entity, pushDir)
        }
    }

    fun moveEntity(entity: Entity, moveDirection: BlockFace?) {
        if (!entity.canBePushed()) {
            return
        }
        val event = EntityMoveByPistonEvent(entity, entity.getPosition())
        this.level.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return
        }
        if (entity is Player) {
            return
        }
        entity.onPushByPiston(this)
        if (!entity.closed) {
            val diff: Float = Math.abs(progress - lastProgress)
            entity.move(
                    diff * moveDirection.getXOffset(),
                    diff * moveDirection.getYOffset(),
                    diff * moveDirection.getZOffset()
            )
        }
    }

    @PowerNukkitDifference(info = "Trigger observer (with #setDirty()).", since = "1.4.0.0-PN")
    @PowerNukkitDifference(info = "Add option to see if blockentity is currently handling piston move (var finished)")
    fun move(extending: Boolean, attachedBlocks: List<BlockVector3>?) {
        this.extending = extending
        progress = if (extending) 0 else 1.toFloat()
        lastProgress = progress
        newState = if (extending) 1 else 3
        state = newState
        this.attachedBlocks = attachedBlocks
        this.movable = false
        finished = false
        this.level.addChunkPacket(getChunkX(), getChunkZ(), getSpawnPacket())
        lastProgress = if (extending) -MOVE_STEP else 1 + MOVE_STEP
        this.setDirty()
        moveCollidedEntities()
        this.scheduleUpdate()
    }

    @Override
    @PowerNukkitDifference(info = "Add option to see if blockentity is currently handling piston move (var finished)" +
            "+ update around redstone directly after moved block set", since = "1.4.0.0-PN")
    override fun onUpdate(): Boolean {
        var hasUpdate = true
        if (extending) {
            progress = Math.min(1, progress + MOVE_STEP)
            lastProgress = Math.min(1, lastProgress + MOVE_STEP)
        } else {
            progress = Math.max(0, progress - MOVE_STEP)
            lastProgress = Math.max(0, lastProgress - MOVE_STEP)
        }
        moveCollidedEntities()
        if (progress == lastProgress) {
            newState = if (extending) 2 else 0
            state = newState
            val pushDir: BlockFace = if (extending) facing else facing.getOpposite()
            for (pos in attachedBlocks!!) {
                val movingBlock: BlockEntity = this.level.getBlockEntity(pos.getSide(pushDir))
                if (movingBlock is BlockEntityMovingBlock) {
                    movingBlock.close()
                    val moved: Block = movingBlock.getMovingBlock()
                    val blockEntity: CompoundTag = movingBlock.getMovingBlockEntityCompound()
                    if (blockEntity != null) {
                        blockEntity.putInt("x", movingBlock.getFloorX())
                        blockEntity.putInt("y", movingBlock.getFloorY())
                        blockEntity.putInt("z", movingBlock.getFloorZ())
                        BlockEntity.createBlockEntity(blockEntity.getString("id"), this.level.getChunk(movingBlock.getChunkX(), movingBlock.getChunkZ()), blockEntity)
                    }
                    if (this.level.setBlock(movingBlock, moved)) {
                        moved.onUpdate(Level.BLOCK_UPDATE_MOVED)
                        RedstoneComponent.updateAroundRedstone(moved)
                    }
                }
            }
            if (!extending) {
                if (this.level.getBlock(getSide(facing)).getId() === (if (sticky) BlockID.PISTON_HEAD_STICKY else BlockID.PISTON_HEAD)) {
                    this.level.setBlock(getSide(facing), BlockAir())
                }
                this.movable = true
            }
            this.level.scheduleUpdate(this.getLevelBlock(), 1)
            attachedBlocks.clear()
            hasUpdate = false
            finished = true
        }
        this.level.addChunkPacket(getChunkX(), getChunkZ(), getSpawnPacket())
        return super.onUpdate() || hasUpdate
    }

    private fun getExtendedProgress(progress: Float): Float {
        return if (extending) progress - 1 else 1 - progress
    }

    override val isBlockEntityValid: Boolean
        get() {
            val id: Int = getLevelBlock().getId()
            return id == BlockID.PISTON || id == BlockID.STICKY_PISTON
        }

    override fun saveNBT() {
        super.saveNBT()
        this.namedTag.putByte("State", state)
        this.namedTag.putByte("NewState", newState)
        this.namedTag.putFloat("Progress", progress)
        this.namedTag.putFloat("LastProgress", lastProgress)
        this.namedTag.putBoolean("powered", powered)
        this.namedTag.putList(getAttachedBlocks())
        this.namedTag.putInt("facing", facing.getIndex())
    }

    override val spawnCompound: CompoundTag
        get() = CompoundTag()
                .putString("id", BlockEntity.PISTON_ARM)
                .putInt("x", this.x as Int)
                .putInt("y", this.y as Int)
                .putInt("z", this.z as Int)
                .putFloat("Progress", progress)
                .putFloat("LastProgress", lastProgress)
                .putBoolean("isMovable", this.movable)
                .putList(getAttachedBlocks())
                .putList(ListTag("BreakBlocks"))
                .putBoolean("Sticky", sticky)
                .putByte("State", state)
                .putByte("NewState", newState)

    private fun getAttachedBlocks(): ListTag<IntTag> {
        val attachedBlocks: ListTag<IntTag> = ListTag("AttachedBlocks")
        for (block in this.attachedBlocks!!) {
            attachedBlocks.add(IntTag("", block.x))
            attachedBlocks.add(IntTag("", block.y))
            attachedBlocks.add(IntTag("", block.z))
        }
        return attachedBlocks
    }

    companion object {
        val MOVE_STEP: Float = Float.valueOf(0.5f)
    }
}