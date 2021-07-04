package cn.nukkit.level

import cn.nukkit.api.PowerNukkitDifference

/**
 * @author Angelic47 (Nukkit Project)
 */
class Explosion @PowerNukkitOnly @Since("1.4.0.0-PN") protected constructor(center: Position, size: Double, what: Object) {
    private val rays = 16 //Rays
    private val level: Level?
    private val source: Position
    private val size: Double

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var fireChance = 0.0
    private var affectedBlocks: Set<Block>? = null
    private var fireIgnitions: Set<Block>? = null
    private val stepLen = 0.3
    private val what: Object
    private var doesDamage = true

    constructor(center: Position?, size: Double, what: Entity?) : this(center, size, what as Object?) {}

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor(center: Position?, size: Double, what: Block?) : this(center, size, what as Object?) {
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isIncendiary: Boolean
        get() = fireChance > 0
        set(incendiary) {
            if (!incendiary) {
                fireChance = 0.0
            } else if (fireChance <= 0) {
                fireChance = 1.0 / 3.0
            }
        }

    /**
     * @return bool
     */
    @Deprecated("")
    fun explode(): Boolean {
        return if (explodeA()) {
            explodeB()
        } else false
    }

    /**
     * @return bool
     */
    fun explodeA(): Boolean {
        if (what is EntityExplosive) {
            val entity: Entity = what as Entity
            var block: Int = level!!.getBlockIdAt(entity.getFloorX(), entity.getFloorY(), entity.getFloorZ())
            if (block == BlockID.WATER || block == BlockID.STILL_WATER || level!!.getBlockIdAt(entity.getFloorX(), entity.getFloorY(), entity.getFloorZ(), 1).also { block = it } == BlockID.WATER || block == BlockID.STILL_WATER) {
                doesDamage = false
                return true
            }
        }
        if (size < 0.1) {
            return false
        }
        if (affectedBlocks == null) {
            affectedBlocks = LinkedHashSet()
        }
        val incendiary = fireChance > 0
        if (incendiary && fireIgnitions == null) {
            fireIgnitions = LinkedHashSet()
        }
        val random: ThreadLocalRandom = ThreadLocalRandom.current()
        val vector = Vector3(0, 0, 0)
        val vBlock = Vector3(0, 0, 0)
        val mRays = rays - 1
        for (i in 0 until rays) {
            for (j in 0 until rays) {
                for (k in 0 until rays) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents(i as Double / mRays.toDouble() * 2.0 - 1, j as Double / mRays.toDouble() * 2.0 - 1, k as Double / mRays.toDouble() * 2.0 - 1)
                        val len: Double = vector.length()
                        vector.setComponents(vector.x / len * stepLen, vector.y / len * stepLen, vector.z / len * stepLen)
                        var pointerX: Double = source.x
                        var pointerY: Double = source.y
                        var pointerZ: Double = source.z
                        var blastForce: Double = size * random.nextInt(700, 1301) / 1000.0
                        while (blastForce > 0) {
                            val x = pointerX.toInt()
                            val y = pointerY.toInt()
                            val z = pointerZ.toInt()
                            vBlock.x = if (pointerX >= x) x else x - 1
                            vBlock.y = if (pointerY >= y) y else y - 1
                            vBlock.z = if (pointerZ >= z) z else z - 1
                            if (vBlock.y < 0 || vBlock.y > 255) {
                                break
                            }
                            val block: Block = level!!.getBlock(vBlock)
                            if (block.getId() !== 0) {
                                val layer1: Block = block.getLevelBlockAtLayer(1)
                                val resistance: Double = Math.max(block.getResistance(), layer1.getResistance())
                                blastForce -= (resistance / 5 + 0.3) * stepLen
                                if (blastForce > 0) {
                                    if (affectedBlocks.add(block)) {
                                        if (incendiary && random.nextDouble() <= fireChance) {
                                            fireIgnitions.add(block)
                                        }
                                        if (layer1.getId() !== BlockID.AIR) {
                                            affectedBlocks.add(layer1)
                                        }
                                    }
                                }
                            }
                            pointerX += vector.x
                            pointerY += vector.y
                            pointerZ += vector.z
                            blastForce -= stepLen * 0.75
                        }
                    }
                }
            }
        }
        return true
    }

    @PowerNukkitDifference(info = "Using new method to play sounds", since = "1.4.0.0-PN")
    fun explodeB(): Boolean {
        val updateBlocks = LongArraySet()
        val send: List<Vector3> = ArrayList()
        val source: Vector3 = Vector3(source.x, source.y, source.z).floor()
        var yield = 1.0 / size * 100.0
        if (affectedBlocks == null) {
            affectedBlocks = LinkedHashSet()
        }
        if (what is Entity) {
            val affectedBlocksList: List<Block> = ArrayList(affectedBlocks)
            val ev = EntityExplodeEvent(what as Entity, this.source, affectedBlocksList, yield)
            ev.setIgnitions(if (fireIgnitions == null) LinkedHashSet(0) else fireIgnitions)
            level!!.getServer().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            } else {
                yield = ev.getYield()
                affectedBlocks.clear()
                affectedBlocks.addAll(ev.getBlockList())
                fireIgnitions = ev.getIgnitions()
            }
        } else if (what is Block) {
            val ev = BlockExplodeEvent(what as Block, this.source, affectedBlocks,
                    if (fireIgnitions == null) LinkedHashSet(0) else fireIgnitions, yield, fireChance)
            level!!.getServer().getPluginManager().callEvent(ev)
            if (ev.isCancelled()) {
                return false
            } else {
                yield = ev.getYield()
                affectedBlocks = ev.getAffectedBlocks()
                fireIgnitions = ev.getIgnitions()
            }
        }
        val explosionSize = size * 2.0
        val minX: Double = NukkitMath.floorDouble(this.source.x - explosionSize - 1)
        val maxX: Double = NukkitMath.ceilDouble(this.source.x + explosionSize + 1)
        val minY: Double = NukkitMath.floorDouble(this.source.y - explosionSize - 1)
        val maxY: Double = NukkitMath.ceilDouble(this.source.y + explosionSize + 1)
        val minZ: Double = NukkitMath.floorDouble(this.source.z - explosionSize - 1)
        val maxZ: Double = NukkitMath.ceilDouble(this.source.z + explosionSize + 1)
        val explosionBB: AxisAlignedBB = SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
        val list: Array<Entity?> = level!!.getNearbyEntities(explosionBB, if (what is Entity) what as Entity else null)
        for (entity in list) {
            val distance: Double = entity.distance(this.source) / explosionSize
            if (distance <= 1) {
                val motion: Vector3 = entity.subtract(this.source).normalize()
                val exposure = 1
                val impact = (1 - distance) * exposure
                val damage = if (doesDamage) ((impact * impact + impact) / 2 * 8 * explosionSize + 1).toInt() else 0
                if (what is Entity) {
                    entity.attack(EntityDamageByEntityEvent(what as Entity, entity, DamageCause.ENTITY_EXPLOSION, damage))
                } else if (what is Block) {
                    entity.attack(EntityDamageByBlockEvent(what as Block, entity, DamageCause.BLOCK_EXPLOSION, damage))
                } else {
                    entity.attack(EntityDamageEvent(entity, DamageCause.BLOCK_EXPLOSION, damage))
                }
                if (!(entity is EntityItem || entity is EntityXPOrb)) {
                    entity.setMotion(motion.multiply(impact))
                }
            }
        }
        val air = ItemBlock(Block.get(BlockID.AIR))
        var container: BlockEntity
        for (block in affectedBlocks!!) {
            if (block.getId() === BlockID.TNT) {
                (block as BlockTNT).prime(NukkitRandom().nextRange(10, 30), if (what is Entity) what as Entity else null)
            } else if (block.getLevel().getBlockEntity(block).also { container = it } is InventoryHolder) {
                if (container is BlockEntityShulkerBox) {
                    level!!.dropItem(block.add(0.5, 0.5, 0.5), block.toItem())
                    (container as InventoryHolder).getInventory().clearAll()
                } else {
                    for (drop in (container as InventoryHolder).getInventory().getContents().values()) {
                        level!!.dropItem(block.add(0.5, 0.5, 0.5), drop)
                    }
                    (container as InventoryHolder).getInventory().clearAll()
                }
            } else if (Math.random() * 100 < yield) {
                for (drop in block.getDrops(air)) {
                    level!!.dropItem(block.add(0.5, 0.5, 0.5), drop)
                }
            }
            level!!.setBlockAtLayer(block.x as Int, block.y as Int, block.z as Int, block.layer, BlockID.AIR)
            if (block.layer !== 0) {
                continue
            }
            val pos = Vector3(block.x, block.y, block.z)
            for (side in BlockFace.values()) {
                val sideBlock: Vector3 = pos.getSide(side)
                val index: Long = Hash.hashBlock(sideBlock.x as Int, sideBlock.y as Int, sideBlock.z as Int)
                if (!affectedBlocks!!.contains(sideBlock) && !updateBlocks.contains(index)) {
                    var ev = BlockUpdateEvent(level!!.getBlock(sideBlock))
                    level!!.getServer().getPluginManager().callEvent(ev)
                    if (!ev.isCancelled()) {
                        ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL)
                    }
                    val layer1: Block = level!!.getBlock(sideBlock, 1)
                    if (layer1.getId() !== BlockID.AIR) {
                        ev = BlockUpdateEvent(layer1)
                        level!!.getServer().getPluginManager().callEvent(ev)
                        if (!ev.isCancelled()) {
                            ev.getBlock().onUpdate(Level.BLOCK_UPDATE_NORMAL)
                        }
                    }
                    updateBlocks.add(index)
                }
            }
            send.add(Vector3(block.x - source.x, block.y - source.y, block.z - source.z))
        }
        for (remainingPos in fireIgnitions!!) {
            val toIgnite: Block = level!!.getBlock(remainingPos)
            if (toIgnite.getId() === BlockID.AIR && toIgnite.down().isSolid(BlockFace.UP)) {
                level!!.setBlock(toIgnite, Block.get(BlockID.FIRE))
            }
        }
        level!!.addParticle(HugeExplodeSeedParticle(this.source))
        level!!.addSound(source, Sound.RANDOM_EXPLODE)
        return true
    }

    init {
        level = center.getLevel()
        source = center
        this.size = Math.max(size, 0)
        this.what = what
    }
}