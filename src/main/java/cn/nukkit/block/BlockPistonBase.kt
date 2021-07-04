package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@Log4j2
abstract class BlockPistonBase @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta), RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityPistonArm?> {
    var sticky = false

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.PISTON_ARM

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityPistonArm?>
        get() = BlockEntityPistonArm::class.java

    @get:Override
    override val resistance: Double
        get() = 2.5

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                val y: Double = player.y + player.getEyeHeight()
                if (y - y > 2) {
                    this.setDamage(BlockFace.UP.getIndex())
                } else if (y - y > 0) {
                    this.setDamage(BlockFace.DOWN.getIndex())
                } else {
                    this.setDamage(player.getHorizontalFacing().getIndex())
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getIndex())
            }
        }
        if (this.level.getBlockEntity(this) != null) {
            val blockEntity: BlockEntity = this.level.getBlockEntity(this)
            log.warn("Found unused BlockEntity at world={} x={} y={} z={} whilst attempting to place piston, closing it.", blockEntity.getLevel().getName(), blockEntity.getX(), blockEntity.getY(), blockEntity.getZ())
            blockEntity.saveNBT()
            blockEntity.close()
        }
        val nbt: CompoundTag = CompoundTag()
                .putInt("facing", blockFace.getIndex())
                .putBoolean("Sticky", sticky)
                .putBoolean("powered", isGettingPower)
        val piston: BlockEntityPistonArm = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt)
                ?: return false
        checkState(piston.powered)
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        this.level.setBlock(this, BlockAir(), true, true)
        val block: Block = this.getSide(blockFace)
        if (block is BlockPistonHead && block.getBlockFace() === blockFace) {
            block.onBreak(item)
        }
        return true
    }

    val isExtended: Boolean
        get() {
            val face: BlockFace = blockFace
            val block: Block = getSide(face)
            return block is BlockPistonHead && block.getBlockFace() === face
        }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered + update all around redstone torches, " +
            "even if the piston can't move.", since = "1.4.0.0-PN")
    override fun onUpdate(type: Int): Int {
        return if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE && type != Level.BLOCK_UPDATE_SCHEDULED) {
            0
        } else {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0
            }

            // We can't use getOrCreateBlockEntity(), because the update method is called on block place,
            // before the "real" BlockEntity is set. That means, if we'd use the other method here,
            // it would create two BlockEntities.
            val arm: BlockEntityPistonArm = this.getBlockEntity()
            val powered = isGettingPower
            updateAroundRedstoneTorches(powered)
            if (arm == null || !arm.finished) return 0
            if (arm.state % 2 === 0 && arm.powered !== powered && checkState(powered)) {
                arm.powered = powered
                if (arm.chunk != null) {
                    arm.chunk.setChanged()
                }
            }
            type
        }
    }

    private fun updateAroundRedstoneTorches(powered: Boolean) {
        for (side in BlockFace.values()) {
            if (getSide(side) is BlockRedstoneTorch && powered
                    || getSide(side) is BlockRedstoneTorchUnlit && !powered) {
                val torch: BlockTorch = getSide(side) as BlockTorch
                val torchAttachment: BlockTorch.TorchAttachment = torch.getTorchAttachment()
                val support: Block = torch.getSide(torchAttachment.getAttachedFace())
                if (support.getLocation().equals(this.getLocation())) {
                    torch.onUpdate(Level.BLOCK_UPDATE_REDSTONE)
                }
            }
        }
    }

    private fun checkState(isPowered: Boolean): Boolean {
        var isPowered: Boolean? = isPowered
        if (!this.level.getServer().isRedstoneEnabled()) {
            return false
        }
        if (isPowered == null) {
            isPowered = isGettingPower
        }
        if (isPowered && !isExtended) {
            if (!doMove(true)) {
                return false
            }
            this.getLevel().addSound(this, Sound.TILE_PISTON_OUT)
            return true
        } else if (!isPowered && isExtended) {
            if (!doMove(false)) {
                return false
            }
            this.getLevel().addSound(this, Sound.TILE_PISTON_IN)
            return true
        }
        return false
    }

    @get:Override
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val isGettingPower: Boolean
        get() {
            val face: BlockFace = blockFace
            for (side in BlockFace.values()) {
                if (side === face) {
                    continue
                }
                val b: Block = this.getSide(side)
                if (b.getId() === Block.REDSTONE_WIRE && b.getDamage() > 0 && b.y >= this.getY()) {
                    return true
                }
                if (this.level.isSidePowered(b, side)) {
                    return true
                }
            }
            return false
        }

    private fun doMove(extending: Boolean): Boolean {
        val direction: BlockFace = blockFace
        val calculator: BlocksCalculator = BlocksCalculator(level, this, blockFace, extending, sticky)
        val canMove = calculator.canMove()
        if (!canMove && extending) {
            return false
        }
        var attached: List<BlockVector3?> = Collections.emptyList()
        val event = BlockPistonEvent(this, direction, calculator.blocksToMove, calculator.blocksToDestroy, extending)
        this.level.getServer().getPluginManager().callEvent(event)
        if (event.isCancelled()) {
            return false
        }
        if (canMove && (sticky || extending)) {
            val destroyBlocks: List<Block> = calculator.blocksToDestroy
            for (i in destroyBlocks.size() - 1 downTo 0) {
                val block: Block = destroyBlocks[i]
                this.level.useBreakOn(block, null, null, false)
            }
            val newBlocks: List<Block?> = calculator.blocksToMove
            attached = newBlocks.stream().map(Vector3::asBlockVector3).collect(Collectors.toList())
            val side: BlockFace = if (extending) direction else direction.getOpposite()
            val tags: List<CompoundTag> = ArrayList()
            for (oldBlock in newBlocks) {
                var tag: CompoundTag? = CompoundTag()
                val be: BlockEntity = this.level.getBlockEntity(oldBlock)
                if (be != null && be !is BlockEntityMovingBlock) {
                    be.saveNBT()
                    tag = CompoundTag(be.namedTag.getTags())
                    be.close()
                }
                tags.add(tag)
            }
            var i = 0
            for (newBlock in newBlocks) {
                val oldPos: Vector3 = newBlock.add(0)
                newBlock!!.position(newBlock.add(0).getSide(side))
                val nbt: CompoundTag = CompoundTag()
                        .putInt("pistonPosX", this.getFloorX())
                        .putInt("pistonPosY", this.getFloorY())
                        .putInt("pistonPosZ", this.getFloorZ())
                        .putCompound("movingBlock", CompoundTag()
                                .putInt("id", newBlock.getId()) //only for nukkit purpose
                                .putInt("meta", newBlock.getDamage()) //only for nukkit purpose
                                .putShort("val", newBlock.getDamage())
                                .putString("name", BlockStateRegistry.getPersistenceName(newBlock.getId()))
                        )
                if (!tags[i].isEmpty()) {
                    nbt.putCompound("movingEntity", tags[i])
                }
                BlockEntityHolder.setBlockAndCreateEntity(BlockState.of(BlockID.MOVING_BLOCK).getBlock(newBlock) as BlockEntityHolder<*>,
                        true, true, nbt)
                if (this.level.getBlockIdAt(oldPos.getFloorX(), oldPos.getFloorY(), oldPos.getFloorZ()) !== BlockID.MOVING_BLOCK) {
                    this.level.setBlock(oldPos, Block.get(BlockID.AIR))
                }
                i++
            }
        }
        if (extending) {
            this.level.setBlock(this.getSide(direction), createHead(this.getDamage()))
        }
        val blockEntity: BlockEntityPistonArm = getOrCreateBlockEntity()
        blockEntity.move(extending, attached)
        return true
    }

    protected fun createHead(damage: Int): BlockPistonHead {
        return Block.get(pistonHeadBlockId, damage) as BlockPistonHead
    }

    abstract val pistonHeadBlockId: Int

    inner class BlocksCalculator @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(level: Level?, pos: Block, face: BlockFace, extending: Boolean, sticky: Boolean) {
        private val pistonPos: Vector3
        private var armPos: Vector3? = null
        private var blockToMove: Block? = null
        private var moveDirection: BlockFace? = null
        private val extending: Boolean
        private val sticky: Boolean
        private val toMove: List<Block?> = ArrayList()
        private val toDestroy: List<Block> = ArrayList()

        /**
         * @param level Unused, needed for compatibility with Cloudburst Nukkit plugins
         */
        constructor(level: Level?, block: Block, facing: BlockFace, extending: Boolean) : this(level, block, facing, extending, false) {}

        fun canMove(): Boolean {
            if (!sticky && !extending) {
                return true
            }
            toMove.clear()
            toDestroy.clear()
            val block: Block? = blockToMove
            if (!canPush(block, moveDirection, true, extending)) {
                return false
            }
            if (block!!.breaksWhenMoved()) {
                if (extending || block!!.sticksToPiston()) {
                    toDestroy.add(blockToMove)
                }
                return true
            }
            if (!addBlockLine(blockToMove, blockToMove!!.getSide(moveDirection.getOpposite()), true)) {
                return false
            }
            for (i in 0 until toMove.size()) {
                val b: Block? = toMove[i]
                val blockId: Int = b.getId()
                if ((blockId == SLIME_BLOCK || blockId == HONEY_BLOCK) && !addBranchingBlocks(b)) {
                    return false
                }
            }
            return true
        }

        private fun addBlockLine(origin: Block?, from: Block?, mainBlockLine: Boolean): Boolean {
            var block: Block = origin!!.clone()
            if (block.getId() === AIR) {
                return true
            }
            if (!mainBlockLine && (block.getId() === SLIME_BLOCK && from.getId() === HONEY_BLOCK
                            || block.getId() === HONEY_BLOCK && from.getId() === SLIME_BLOCK)) {
                return true
            }
            if (!canPush(origin, moveDirection, false, extending)) {
                return true
            }
            if (origin!!.equals(pistonPos)) {
                return true
            }
            if (toMove.contains(origin)) {
                return true
            }
            if (toMove.size() >= 12) {
                return false
            }
            toMove.add(block)
            var count = 1
            val sticked: List<Block> = ArrayList()
            while (block.getId() === SLIME_BLOCK || block.getId() === HONEY_BLOCK) {
                val oldBlock: Block = block.clone()
                block = origin!!.getSide(moveDirection.getOpposite(), count)
                if (!extending && (block.getId() === SLIME_BLOCK && oldBlock.getId() === HONEY_BLOCK
                                || block.getId() === HONEY_BLOCK && oldBlock.getId() === SLIME_BLOCK)) {
                    break
                }
                if (block.getId() === AIR || !canPush(block, moveDirection, false, extending) || block.equals(pistonPos)) {
                    break
                }
                if (block.breaksWhenMoved() && block.sticksToPiston()) {
                    toDestroy.add(block)
                    break
                }
                if (count + toMove.size() > 12) {
                    return false
                }
                count++
                sticked.add(block)
            }
            var stickedCount: Int = sticked.size()
            if (stickedCount > 0) {
                toMove.addAll(Lists.reverse(sticked))
            }
            var step = 1
            while (true) {
                val nextBlock: Block = origin!!.getSide(moveDirection, step)
                val index = toMove.indexOf(nextBlock)
                if (index > -1) {
                    reorderListAtCollision(stickedCount, index)
                    for (i in 0..index + stickedCount) {
                        val b: Block? = toMove[i]
                        if ((b.getId() === SLIME_BLOCK || b.getId() === HONEY_BLOCK) && !addBranchingBlocks(b)) {
                            return false
                        }
                    }
                    return true
                }
                if (nextBlock.getId() === AIR || nextBlock.equals(armPos)) {
                    return true
                }
                if (!canPush(nextBlock, moveDirection, true, extending) || nextBlock.equals(pistonPos)) {
                    return false
                }
                if (nextBlock.breaksWhenMoved()) {
                    toDestroy.add(nextBlock)
                    return true
                }
                if (toMove.size() >= 12) {
                    return false
                }
                toMove.add(nextBlock)
                ++stickedCount
                ++step
            }
        }

        private fun reorderListAtCollision(count: Int, index: Int) {
            val list: List<Block> = ArrayList(toMove.subList(0, index))
            val list1: List<Block> = ArrayList(toMove.subList(toMove.size() - count, toMove.size()))
            val list2: List<Block> = ArrayList(toMove.subList(index, toMove.size() - count))
            toMove.clear()
            toMove.addAll(list)
            toMove.addAll(list1)
            toMove.addAll(list2)
        }

        private fun addBranchingBlocks(block: Block?): Boolean {
            for (face in BlockFace.values()) {
                if (face.getAxis() !== moveDirection.getAxis() && !addBlockLine(block!!.getSide(face), block, false)) {
                    return false
                }
            }
            return true
        }

        val blocksToMove: List<cn.nukkit.block.Block?>
            get() = toMove
        val blocksToDestroy: List<cn.nukkit.block.Block>
            get() = toDestroy

        /**
         * @param level Unused, needed for compatibility with Cloudburst Nukkit plugins
         */
        init {
            pistonPos = pos.getLocation()
            this.extending = extending
            this.sticky = sticky
            if (!extending) {
                armPos = pistonPos.getSide(face)
            }
            if (extending) {
                moveDirection = face
                blockToMove = pos.getSide(face)
            } else {
                moveDirection = face.getOpposite()
                if (sticky) {
                    blockToMove = pos.getSide(face, 2)
                } else {
                    blockToMove = null
                }
            }
        }
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @get:Override
    val blockFace: BlockFace
        get() {
            val face: BlockFace = BlockFace.fromIndex(this.getDamage())
            return if (face.getHorizontalIndex() >= 0) face.getOpposite() else face
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES
        fun canPush(block: Block?, face: BlockFace?, destroyBlocks: Boolean, extending: Boolean): Boolean {
            if (block.getY() >= 0 && (face !== BlockFace.DOWN || block.getY() !== 0) && block.getY() <= 255 && (face !== BlockFace.UP || block.getY() !== 255)) {
                if (extending && !block!!.canBePushed() || !extending && !block!!.canBePulled()) {
                    return false
                }
                if (block!!.breaksWhenMoved()) {
                    return destroyBlocks || block!!.sticksToPiston()
                }
                val be: BlockEntity = block.level.getBlockEntity(block)
                return be == null || be.isMovable()
            }
            return false
        }
    }
}