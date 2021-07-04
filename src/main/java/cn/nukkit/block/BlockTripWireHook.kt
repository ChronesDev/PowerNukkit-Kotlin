package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
class BlockTripWireHook @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), RedstoneComponent {
    @get:Override
    override val name: String
        get() = "Tripwire Hook"

    @get:Override
    override val id: Int
        get() = TRIPWIRE_HOOK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES
    val facing: BlockFace
        get() = BlockFace.fromHorizontalIndex(getDamage() and 3)

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.getSide(facing.getOpposite()).isNormalBlock()) {
                this.level.useBreakOn(this)
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            calculateState(false, true, -1, null)
            return type
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (!this.getSide(face.getOpposite()).isNormalBlock() || face === BlockFace.DOWN || face === BlockFace.UP) {
            return false
        }
        if (face.getAxis().isHorizontal()) {
            setFace(face)
        }
        this.level.setBlock(this, this)
        if (player != null) {
            calculateState(false, false, -1, null)
        }
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        super.onBreak(item)
        val attached = isAttached
        val powered = isPowered
        if (attached || powered) {
            calculateState(true, false, -1, null)
        }
        if (powered) {
            updateAroundRedstone()
            RedstoneComponent.updateAroundRedstone(this.getSide(facing.getOpposite()))
        }
        return true
    }

    fun calculateState(onBreak: Boolean, updateAround: Boolean, pos: Int, block: Block?) {
        var block: Block? = block
        if (!this.level.getServer().isRedstoneEnabled()) {
            return
        }
        val facing: BlockFace = facing
        val position: Position = this.getLocation()
        val attached = isAttached
        val powered = isPowered
        var canConnect = !onBreak
        var nextPowered = false
        var distance = 0
        val blocks: Array<Block?> = arrayOfNulls<Block>(42)
        for (i in 1..41) {
            val vector: Vector3 = position.getSide(facing, i)
            var b: Block = this.level.getBlock(vector)
            if (b is BlockTripWireHook) {
                if (b.facing === facing.getOpposite()) {
                    distance = i
                }
                break
            }
            if (b.getId() !== Block.TRIPWIRE && i != pos) {
                blocks[i] = null
                canConnect = false
            } else {
                if (i == pos) {
                    b = if (block != null) block else b
                }
                if (b is BlockTripWire) {
                    val disarmed: Boolean = !b.isDisarmed()
                    val wirePowered: Boolean = b.isPowered()
                    nextPowered = nextPowered or (disarmed && wirePowered)
                    if (i == pos) {
                        this.level.scheduleUpdate(this, 10)
                        canConnect = canConnect and disarmed
                    }
                }
                blocks[i] = b
            }
        }
        canConnect = canConnect and distance > 1
        nextPowered = nextPowered and canConnect
        val hook = Block.get(BlockID.TRIPWIRE_HOOK) as BlockTripWireHook
        hook.isAttached = canConnect
        hook.isPowered = nextPowered
        if (distance > 0) {
            val p: Position = position.getSide(facing, distance)
            val face: BlockFace = facing.getOpposite()
            hook.setFace(face)
            this.level.setBlock(p, hook, true, false)
            RedstoneComponent.updateAroundRedstone(p)
            RedstoneComponent.updateAroundRedstone(p.getSide(face.getOpposite()))
            addSound(p, canConnect, nextPowered, attached, powered)
        }
        addSound(position, canConnect, nextPowered, attached, powered)
        if (!onBreak) {
            hook.setFace(facing)
            this.level.setBlock(position, hook, true, false)
            if (updateAround) {
                updateAroundRedstone()
                RedstoneComponent.updateAroundRedstone(position.getSide(facing.getOpposite()))
            }
        }
        if (attached != canConnect) {
            for (i in 1 until distance) {
                val vc: Vector3 = position.getSide(facing, i)
                block = blocks[i]
                if (block != null && this.level.getBlockIdAt(vc.getFloorX(), vc.getFloorY(), vc.getFloorZ()) !== Block.AIR) {
                    if (canConnect xor (block.getDamage() and 0x04 > 0)) {
                        block.setDamage(block.getDamage() xor 0x04)
                    }
                    this.level.setBlock(vc, block, true, false)
                }
            }
        }
    }

    private fun addSound(pos: Vector3, canConnect: Boolean, nextPowered: Boolean, attached: Boolean, powered: Boolean) {
        if (nextPowered && !powered) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_POWER_ON)
            this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 0, 15))
        } else if (!nextPowered && powered) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_POWER_OFF)
            this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, 15, 0))
        } else if (canConnect && !attached) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_ATTACH)
        } else if (!canConnect && attached) {
            this.level.addLevelSoundEvent(pos, LevelSoundEventPacket.SOUND_DETACH)
        }
    }

    var isAttached: Boolean
        get() = getDamage() and 0x04 > 0
        set(value) {
            if (value xor isAttached) {
                this.setDamage(this.getDamage() xor 0x04)
            }
        }
    var isPowered: Boolean
        get() = this.getDamage() and 0x08 > 0
        set(value) {
            if (value xor isPowered) {
                this.setDamage(this.getDamage() xor 0x08)
            }
        }

    fun setFace(face: BlockFace) {
        this.setDamage(this.getDamage() - this.getDamage() % 4)
        this.setDamage(this.getDamage() or face.getHorizontalIndex())
    }

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    @Override
    override fun getWeakPower(face: BlockFace?): Int {
        return if (isPowered) 15 else 0
    }

    @Override
    override fun getStrongPower(side: BlockFace): Int {
        return if (!isPowered) 0 else if (facing === side) 15 else 0
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canBeFlowedInto(): Boolean {
        return false
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, ATTACHED, POWERED)
    }
}