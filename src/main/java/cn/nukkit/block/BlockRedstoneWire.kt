package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
class BlockRedstoneWire @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta), RedstoneComponent {
    @get:Override
    override var isPowerSource = true
        private set
    private val blocksNeedingUpdate: Set<Vector3> = HashSet()

    @get:Override
    override val name: String
        get() = "Redstone Wire"

    @get:Override
    override val id: Int
        get() = REDSTONE_WIRE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Removed unneeded replaceable check")
    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (!canBePlacedOn(block.down())) {
            return false
        }
        if (this.level.getServer().isRedstoneEnabled()) {
            this.getLevel().setBlock(block, this, true)
            updateSurroundingRedstone(true)
            val pos: Position = getLocation()
            for (blockFace in Plane.VERTICAL) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(blockFace), blockFace.getOpposite())
            }
            for (blockFace in Plane.VERTICAL) {
                updateAround(pos.getSide(blockFace), blockFace.getOpposite())
            }
            for (blockFace in Plane.HORIZONTAL) {
                val p: Position = pos.getSide(blockFace)
                if (this.level.getBlock(p).isNormalBlock()) {
                    updateAround(p.getSide(BlockFace.UP), BlockFace.DOWN)
                } else {
                    updateAround(p.getSide(BlockFace.DOWN), BlockFace.UP)
                }
            }
        } else {
            this.getLevel().setBlock(block, this, true, true)
        }
        return true
    }

    private fun updateAround(pos: Position, face: BlockFace) {
        if (this.level.getBlock(pos).getId() === Block.REDSTONE_WIRE) {
            updateAroundRedstone(face)
            for (side in BlockFace.values()) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(side), side.getOpposite())
            }
        }
    }

    private fun updateSurroundingRedstone(force: Boolean) {
        calculateCurrentChanges(force)
    }

    @PowerNukkitDifference(info = "Let redstone go down transparent blocks.", since = "1.4.0.0-PN")
    private fun calculateCurrentChanges(force: Boolean) {
        val pos: Vector3 = this.getLocation()
        val meta: Int = this.getDamage()
        var maxStrength = meta
        isPowerSource = false
        val power = indirectPower
        isPowerSource = true
        if (power > 0 && power > maxStrength - 1) {
            maxStrength = power
        }
        var strength = 0
        for (face in Plane.HORIZONTAL) {
            val v: Vector3 = pos.getSide(face)
            if (v.getX() === this.getX() && v.getZ() === this.getZ()) {
                continue
            }
            strength = getMaxCurrentStrength(v, strength)
            if (getMaxCurrentStrength(v.up(), strength) > strength && !this.level.getBlock(pos.up()).isNormalBlock()) {
                strength = getMaxCurrentStrength(v.up(), strength)
            }
            if (getMaxCurrentStrength(v.down(), strength) > strength && !this.level.getBlock(v).isNormalBlock()) {
                strength = getMaxCurrentStrength(v.down(), strength)
            }
        }
        if (strength > maxStrength) {
            maxStrength = strength - 1
        } else if (maxStrength > 0) {
            --maxStrength
        } else {
            maxStrength = 0
        }
        if (power > maxStrength - 1) {
            maxStrength = power
        } else if (power < maxStrength && strength <= maxStrength) {
            maxStrength = Math.max(power, strength - 1)
        }
        if (meta != maxStrength) {
            this.level.getServer().getPluginManager().callEvent(BlockRedstoneEvent(this, meta, maxStrength))
            this.setDamage(maxStrength)
            this.level.setBlock(this, this, false, true)
            updateAllAroundRedstone()
        } else if (force) {
            for (face in BlockFace.values()) {
                RedstoneComponent.updateAroundRedstone(getSide(face), face.getOpposite())
            }
        }
    }

    private fun getMaxCurrentStrength(pos: Vector3, maxStrength: Int): Int {
        return if (this.level.getBlockIdAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ()) !== id) {
            maxStrength
        } else {
            val strength: Int = this.level.getBlockDataAt(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ())
            Math.max(strength, maxStrength)
        }
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        val air: Block = Block.get(BlockID.AIR)
        this.getLevel().setBlock(this, air, true, true)
        val pos: Position = getLocation()
        if (this.level.getServer().isRedstoneEnabled()) {
            updateSurroundingRedstone(false)
            this.getLevel().setBlock(this, air, true, true)
            for (blockFace in BlockFace.values()) {
                RedstoneComponent.updateAroundRedstone(pos.getSide(blockFace))
            }
            for (blockFace in Plane.HORIZONTAL) {
                val p: Position = pos.getSide(blockFace)
                if (this.level.getBlock(p).isNormalBlock()) {
                    updateAround(p.getSide(BlockFace.UP), BlockFace.DOWN)
                } else {
                    updateAround(p.getSide(BlockFace.DOWN), BlockFace.UP)
                }
            }
        }
        return true
    }

    @Override
    override fun toItem(): Item {
        return ItemRedstone()
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.AIR_BLOCK_COLOR

    @Override
    override fun onUpdate(type: Int): Int {
        if (type != Level.BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE) {
            return 0
        }
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0
        }

        // Redstone event
        val ev = RedstoneUpdateEvent(this)
        getLevel().getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled()) {
            return 0
        }
        if (type == Level.BLOCK_UPDATE_NORMAL && !canBePlacedOn(this.down())) {
            this.getLevel().useBreakOn(this)
            return Level.BLOCK_UPDATE_NORMAL
        }
        updateSurroundingRedstone(false)
        return Level.BLOCK_UPDATE_NORMAL
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed placement logic")
    fun canBePlacedOn(support: Block): Boolean {
        return support.isSolid(BlockFace.UP)
    }

    override fun getStrongPower(side: BlockFace): Int {
        return if (!isPowerSource) 0 else getWeakPower(side)
    }

    override fun getWeakPower(side: BlockFace): Int {
        return if (!isPowerSource) {
            0
        } else {
            val power: Int = this.getDamage()
            if (power == 0) {
                0
            } else if (side === BlockFace.UP) {
                power
            } else {
                val faces: EnumSet<BlockFace> = EnumSet.noneOf(BlockFace::class.java)
                for (face in Plane.HORIZONTAL) {
                    if (isPowerSourceAt(face)) {
                        faces.add(face)
                    }
                }
                if (side.getAxis().isHorizontal() && faces.isEmpty()) {
                    power
                } else if (faces.contains(side) && !faces.contains(side.rotateYCCW()) && !faces.contains(side.rotateY())) {
                    power
                } else {
                    0
                }
            }
        }
    }

    private fun isPowerSourceAt(side: BlockFace): Boolean {
        val pos: Vector3 = getLocation()
        val v: Vector3 = pos.getSide(side)
        val block: Block = this.level.getBlock(v)
        val flag: Boolean = block.isNormalBlock()
        val flag1: Boolean = this.level.getBlock(pos.up()).isNormalBlock()
        return !flag1 && flag && canConnectUpwardsTo(this.level, v.up()) || canConnectTo(block, side) || !flag && canConnectUpwardsTo(this.level, block.down())
    }

    private val indirectPower: Int
        private get() {
            var power = 0
            val pos: Vector3 = getLocation()
            for (face in BlockFace.values()) {
                val blockPower = getIndirectPower(pos.getSide(face), face)
                if (blockPower >= 15) {
                    return 15
                }
                if (blockPower > power) {
                    power = blockPower
                }
            }
            return power
        }

    private fun getIndirectPower(pos: Vector3, face: BlockFace): Int {
        val block: Block = this.level.getBlock(pos)
        if (block.getId() === Block.REDSTONE_WIRE) {
            return 0
        }
        return if (block.isNormalBlock()) getStrongPower(pos) else block.getWeakPower(face)
    }

    private override fun getStrongPower(pos: Vector3): Int {
        var i = 0
        for (face in BlockFace.values()) {
            i = Math.max(i, getStrongPower(pos.getSide(face), face))
            if (i >= 15) {
                return i
            }
        }
        return i
    }

    private fun getStrongPower(pos: Vector3, direction: BlockFace): Int {
        val block: Block = this.level.getBlock(pos)
        return if (block.getId() === Block.REDSTONE_WIRE) {
            0
        } else block.getStrongPower(direction)
    }

    companion object {
        @Since("1.5.0.0-PN")
        @PowerNukkitOnly
        val PROPERTIES: BlockProperties = CommonBlockProperties.REDSTONE_SIGNAL_BLOCK_PROPERTY
        protected fun canConnectUpwardsTo(level: Level, pos: Vector3?): Boolean {
            return canConnectUpwardsTo(level.getBlock(pos))
        }

        protected fun canConnectUpwardsTo(block: Block): Boolean {
            return canConnectTo(block, null)
        }

        @PowerNukkitDifference(info = "Can't connect to pistons and bells, but powers them either.", since = "1.4.0.0-PN")
        protected fun canConnectTo(block: Block, side: BlockFace?): Boolean {
            return if (block.getId() === Block.REDSTONE_WIRE) {
                true
            } else if (BlockRedstoneDiode.isDiode(block)) {
                val face: BlockFace = (block as BlockRedstoneDiode).getFacing()
                face === side || face.getOpposite() === side
            } else {
                block.isPowerSource() && side != null
            }
        }
    }
}