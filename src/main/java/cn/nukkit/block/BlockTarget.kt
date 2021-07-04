package cn.nukkit.block

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
class BlockTarget @PowerNukkitOnly @Since("1.4.0.0-PN") constructor() : BlockTransparent(), RedstoneComponent, BlockEntityHolder<BlockEntityTarget?> {
    @get:Override
    override val id: Int
        get() = TARGET

    @get:Override
    override val name: String
        get() = "Target"

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityTarget?>
        get() = BlockEntityTarget::class.java

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityType: String
        get() = BlockEntity.TARGET

    @get:Override
    override val isPowerSource: Boolean
        get() = true

    @Override
    override fun getWeakPower(face: BlockFace?): Int {
        val target: BlockEntityTarget = getBlockEntity()
        return if (target == null) 0 else target.getActivePower()
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun activatePower(power: Int): Boolean {
        return activatePower(power, 4 * 2)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun activatePower(power: Int, ticks: Int): Boolean {
        val level: Level = getLevel()
        if (power <= 0 || ticks <= 0) {
            return deactivatePower()
        }
        if (!level.getServer().isRedstoneEnabled()) {
            return false
        }
        val target: BlockEntityTarget = getOrCreateBlockEntity()
        val previous: Int = target.getActivePower()
        level.cancelSheduledUpdate(this, this)
        level.scheduleUpdate(this, ticks)
        target.setActivePower(power)
        if (previous != power) {
            updateAroundRedstone()
        }
        return true
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun deactivatePower(): Boolean {
        val target: BlockEntityTarget = getBlockEntity()
        if (target != null) {
            val currentPower: Int = target.getActivePower()
            target.setActivePower(0)
            target.close()
            if (currentPower != 0 && level.getServer().isRedstoneEnabled()) {
                updateAroundRedstone()
            }
            return true
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            deactivatePower()
            return type
        }
        return 0
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun onProjectileHit(@Nonnull projectile: Entity?, @Nonnull position: Position, @Nonnull motion: Vector3): Boolean {
        var ticks = 8
        if (projectile is EntityArrow || projectile is EntityThrownTrident) {
            ticks = 20
        }
        val intercept: MovingObjectPosition = calculateIntercept(position, position.add(motion.multiply(2)))
                ?: return false
        val faceHit: BlockFace = intercept.getFaceHit() ?: return false
        val hitVector: Vector3 = intercept.hitVector.subtract(x * 2, y * 2, z * 2)
        val axes: List<Axis> = ArrayList(Arrays.asList(Axis.values()))
        axes.remove(faceHit.getAxis())
        val coords = doubleArrayOf(hitVector.getAxis(axes[0]), hitVector.getAxis(axes[1]))
        for (i in 0..1) {
            if (coords[i] == 0.5) {
                coords[i] = 1
            } else if (coords[i] <= 0 || coords[i] >= 1) {
                coords[i] = 0
            } else if (coords[i] < 0.5) {
                coords[i] *= 2
            } else {
                coords[i] = coords[i] / -0.5 + 2
            }
        }
        val scale = (coords[0] + coords[1]) / 2
        activatePower(NukkitMath.ceilDouble(16 * scale), ticks)
        return true
    }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_HOE

    @get:Override
    override val hardness: Double
        get() = 0.5

    @get:Override
    override val resistance: Double
        get() = 0.5

    @get:Override
    override val burnAbility: Int
        get() = 15

    @get:Override
    override val burnChance: Int
        get() = 0

    @get:Override
    override val color: BlockColor
        get() = BlockColor.QUARTZ_BLOCK_COLOR
}