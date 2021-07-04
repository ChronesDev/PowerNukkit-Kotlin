package cn.nukkit.item

import cn.nukkit.Player

/**
 * @author CreeperFace
 */
class ItemFirework @PowerNukkitDifference(info = "Will not add compound tag automatically") constructor(meta: Integer?, count: Int) : Item(FIREWORKS, meta, count, "Fireworks") {
    @JvmOverloads
    constructor(meta: Integer? = 0) : this(meta, 1) {
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(level: Level, player: Player, block: Block, target: Block?, face: BlockFace?, fx: Double, fy: Double, fz: Double): Boolean {
        if (player.isAdventure()) {
            return false
        }
        if (block.canPassThrough()) {
            spawnFirework(level, block)
            if (!player.isCreative()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex())
            }
            return true
        }
        return false
    }

    @Override
    override fun onClickAir(player: Player, directionVector: Vector3?): Boolean {
        if (player.getInventory().getChestplate() is ItemElytra && player.isGliding()) {
            spawnFirework(player.getLevel(), player)
            player.setMotion(Vector3(
                    -Math.sin(Math.toRadians(player.yaw)) * Math.cos(Math.toRadians(player.pitch)) * 2,
                    -Math.sin(Math.toRadians(player.pitch)) * 2,
                    Math.cos(Math.toRadians(player.yaw)) * Math.cos(Math.toRadians(player.pitch)) * 2))
            if (!player.isCreative()) {
                this.count--
            }
            return true
        }
        return false
    }

    fun addExplosion(explosion: FireworkExplosion) {
        val colors: List<DyeColor> = explosion.getColors()
        val fades: List<DyeColor> = explosion.getFades()
        if (colors.isEmpty()) {
            return
        }
        val clrs = ByteArray(colors.size())
        for (i in clrs.indices) {
            clrs[i] = colors[i].getDyeData() as Byte
        }
        val fds = ByteArray(fades.size())
        for (i in fds.indices) {
            fds[i] = fades[i].getDyeData() as Byte
        }
        val explosions: ListTag<CompoundTag> = this.getNamedTag().getCompound("Fireworks").getList("Explosions", CompoundTag::class.java)
        val tag: CompoundTag = CompoundTag()
                .putByteArray("FireworkColor", clrs)
                .putByteArray("FireworkFade", fds)
                .putBoolean("FireworkFlicker", explosion.flicker)
                .putBoolean("FireworkTrail", explosion.trail)
                .putByte("FireworkType", explosion.type.ordinal())
        explosions.add(tag)
    }

    fun clearExplosions() {
        this.getNamedTag().getCompound("Fireworks").putList(ListTag<CompoundTag>("Explosions"))
    }

    private fun spawnFirework(level: Level, pos: Vector3) {
        val nbt: CompoundTag = CompoundTag()
                .putList(ListTag<DoubleTag>("Pos")
                        .add(DoubleTag("", pos.x + 0.5))
                        .add(DoubleTag("", pos.y + 0.5))
                        .add(DoubleTag("", pos.z + 0.5)))
                .putList(ListTag<DoubleTag>("Motion")
                        .add(DoubleTag("", 0))
                        .add(DoubleTag("", 0))
                        .add(DoubleTag("", 0)))
                .putList(ListTag<FloatTag>("Rotation")
                        .add(FloatTag("", 0))
                        .add(FloatTag("", 0)))
                .putCompound("FireworkItem", NBTIO.putItemHelper(this))
        val entity: EntityFirework = Entity.createEntity("Firework", level.getChunk(pos.getFloorX() shr 4, pos.getFloorZ() shr 4), nbt) as EntityFirework
        if (entity != null) {
            entity.spawnToAll()
        }
    }

    class FireworkExplosion {
        private val colors: List<DyeColor> = ArrayList()
        private val fades: List<DyeColor> = ArrayList()
        var flicker = false
        var trail = false
        var type = ExplosionType.CREEPER_SHAPED
        fun getColors(): List<DyeColor> {
            return colors
        }

        fun getFades(): List<DyeColor> {
            return fades
        }

        fun hasFlicker(): Boolean {
            return flicker
        }

        fun hasTrail(): Boolean {
            return trail
        }

        fun getType(): ExplosionType {
            return type
        }

        fun setFlicker(flicker: Boolean): FireworkExplosion {
            this.flicker = flicker
            return this
        }

        fun setTrail(trail: Boolean): FireworkExplosion {
            this.trail = trail
            return this
        }

        fun type(type: ExplosionType): FireworkExplosion {
            this.type = type
            return this
        }

        fun addColor(color: DyeColor?): FireworkExplosion {
            colors.add(color)
            return this
        }

        fun addFade(fade: DyeColor?): FireworkExplosion {
            fades.add(fade)
            return this
        }

        enum class ExplosionType {
            SMALL_BALL, LARGE_BALL, STAR_SHAPED, CREEPER_SHAPED, BURST
        }
    }
}