package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Pub4Game
 * @since 15.01.2016
 */
class BlockVine @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta) {
    @get:Override
    override val name: String
        get() = "Vines"

    @get:Override
    override val id: Int
        get() = VINE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0.2

    @get:Override
    override val resistance: Double
        get() = 1

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    @Override
    override fun hasEntityCollision(): Boolean {
        return true
    }

    @Override
    override fun canBeReplaced(): Boolean {
        return true
    }

    @Override
    override fun canBeClimbed(): Boolean {
        return true
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun canBeFlowedInto(): Boolean {
        return true
    }

    @Override
    override fun onEntityCollide(entity: Entity) {
        entity.resetFallDistance()
        entity.onGround = true
    }

    @get:Override
    override val isSolid: Boolean
        get() = false

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    override fun isSolid(side: BlockFace?): Boolean {
        return false
    }

    @Override
    protected override fun recalculateBoundingBox(): AxisAlignedBB {
        var f1 = 1.0
        var f2 = 1.0
        var f3 = 1.0
        var f4 = 0.0
        var f5 = 0.0
        var f6 = 0.0
        var flag: Boolean = this.getDamage() > 0
        if (this.getDamage() and 0x02 > 0) {
            f4 = Math.max(f4, 0.0625)
            f1 = 0.0
            f2 = 0.0
            f5 = 1.0
            f3 = 0.0
            f6 = 1.0
            flag = true
        }
        if (this.getDamage() and 0x08 > 0) {
            f1 = Math.min(f1, 0.9375)
            f4 = 1.0
            f2 = 0.0
            f5 = 1.0
            f3 = 0.0
            f6 = 1.0
            flag = true
        }
        if (this.getDamage() and 0x01 > 0) {
            f3 = Math.min(f3, 0.9375)
            f6 = 1.0
            f1 = 0.0
            f4 = 1.0
            f2 = 0.0
            f5 = 1.0
            flag = true
        }
        if (!flag && this.up().isSolid()) {
            f2 = Math.min(f2, 0.9375)
            f5 = 1.0
            f1 = 0.0
            f4 = 1.0
            f3 = 0.0
            f6 = 1.0
        }
        return SimpleAxisAlignedBB(
                this.x + f1,
                this.y + f2,
                this.z + f3,
                this.x + f4,
                this.y + f5,
                this.z + f6
        )
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (block.getId() !== VINE && target.isSolid() && face.getHorizontalIndex() !== -1) {
            this.setDamage(getMetaFromFace(face.getOpposite()))
            this.getLevel().setBlock(block, this, true, true)
            return true
        }
        return false
    }

    @Override
    override fun getDrops(item: Item): Array<Item> {
        return if (item.isShears()) {
            arrayOf<Item>(
                    toItem()
            )
        } else {
            Item.EMPTY_ARRAY
        }
    }

    @Override
    override fun toItem(): Item {
        return ItemBlock(this, 0)
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val up: Block = this.up()
            val upFaces: Set<BlockFace>? = if (up is BlockVine) up.faces else null
            val faces: Set<BlockFace> = faces
            for (face in BlockFace.Plane.HORIZONTAL) {
                if (!this.getSide(face).isSolid() && (upFaces == null || !upFaces.contains(face))) {
                    faces.remove(face)
                }
            }
            if (faces.isEmpty() && !up.isSolid()) {
                this.getLevel().useBreakOn(this, null, null, true)
                return Level.BLOCK_UPDATE_NORMAL
            }
            val meta = getMetaFromFaces(faces)
            if (meta != this.getDamage()) {
                this.level.setBlock(this, Block.get(VINE, meta), true)
                return Level.BLOCK_UPDATE_NORMAL
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            val random: Random = ThreadLocalRandom.current()
            if (random.nextInt(4) === 0) {
                val face: BlockFace = BlockFace.random(random)
                val block: Block = this.getSide(face)
                val faceMeta = getMetaFromFace(face)
                var meta: Int = this.getDamage()
                if (this.y < 255 && face === BlockFace.UP && block.getId() === AIR) {
                    if (canSpread()) {
                        for (horizontalFace in BlockFace.Plane.HORIZONTAL) {
                            if (random.nextBoolean() || !this.getSide(horizontalFace).getSide(face).isSolid()) {
                                meta = meta and getMetaFromFace(horizontalFace).inv()
                            }
                        }
                        putVineOnHorizontalFace(block, meta, this)
                    }
                } else if (face.getHorizontalIndex() !== -1 && meta and faceMeta != faceMeta) {
                    if (canSpread()) {
                        if (block.getId() === AIR) {
                            val cwFace: BlockFace = face.rotateY()
                            val ccwFace: BlockFace = face.rotateYCCW()
                            val cwBlock: Block = block.getSide(cwFace)
                            val ccwBlock: Block = block.getSide(ccwFace)
                            val cwMeta = getMetaFromFace(cwFace)
                            val ccwMeta = getMetaFromFace(ccwFace)
                            val onCw = meta and cwMeta == cwMeta
                            val onCcw = meta and ccwMeta == ccwMeta
                            if (onCw && cwBlock.isSolid()) {
                                putVine(block, getMetaFromFace(cwFace), this)
                            } else if (onCcw && ccwBlock.isSolid()) {
                                putVine(block, getMetaFromFace(ccwFace), this)
                            } else if (onCw && cwBlock.getId() === AIR && this.getSide(cwFace).isSolid()) {
                                putVine(cwBlock, getMetaFromFace(face.getOpposite()), this)
                            } else if (onCcw && ccwBlock.getId() === AIR && this.getSide(ccwFace).isSolid()) {
                                putVine(ccwBlock, getMetaFromFace(face.getOpposite()), this)
                            } else if (block.up().isSolid()) {
                                putVine(block, 0, this)
                            }
                        } else if (!block.isTransparent()) {
                            meta = meta or getMetaFromFace(face)
                            putVine(this, meta, null)
                        }
                    }
                } else if (this.y > 0) {
                    val below: Block = this.down()
                    val id: Int = below.getId()
                    if (id == AIR || id == VINE) {
                        for (horizontalFace in BlockFace.Plane.HORIZONTAL) {
                            if (random.nextBoolean()) {
                                meta = meta and getMetaFromFace(horizontalFace).inv()
                            }
                        }
                        putVineOnHorizontalFace(below, below.getDamage() or meta, if (id == AIR) this else null)
                    }
                }
                return Level.BLOCK_UPDATE_RANDOM
            }
        }
        return 0
    }

    private fun canSpread(): Boolean {
        val blockX: Int = this.getFloorX()
        val blockY: Int = this.getFloorY()
        val blockZ: Int = this.getFloorZ()
        var count = 0
        for (x in blockX - 4..blockX + 4) {
            for (z in blockZ - 4..blockZ + 4) {
                for (y in blockY - 1..blockY + 1) {
                    if (this.level.getBlock(x, y, z).getId() === VINE) {
                        if (++count >= 5) return false
                    }
                }
            }
        }
        return true
    }

    private fun putVine(block: Block, meta: Int, source: Block?) {
        if (block.getId() === VINE && block.getDamage() === meta) return
        val vine: Block = get(VINE, meta)
        val event: BlockGrowEvent
        if (source != null) {
            event = BlockSpreadEvent(block, source, vine)
        } else {
            event = BlockGrowEvent(block, vine)
        }
        this.level.getServer().getPluginManager().callEvent(event)
        if (!event.isCancelled()) {
            this.level.setBlock(block, vine, true)
        }
    }

    private fun putVineOnHorizontalFace(block: Block, meta: Int, source: Block?) {
        if (block.getId() === VINE && block.getDamage() === meta) return
        var isOnHorizontalFace = false
        for (face in BlockFace.Plane.HORIZONTAL) {
            val faceMeta = getMetaFromFace(face)
            if (meta and faceMeta == faceMeta) {
                isOnHorizontalFace = true
                break
            }
        }
        if (isOnHorizontalFace) {
            putVine(block, meta, source)
        }
    }

    private val faces: Set<Any>
        private get() {
            val faces: Set<BlockFace> = EnumSet.noneOf(BlockFace::class.java)
            val meta: Int = this.getDamage()
            if (meta and 1 > 0) {
                faces.add(BlockFace.SOUTH)
            }
            if (meta and 2 > 0) {
                faces.add(BlockFace.WEST)
            }
            if (meta and 4 > 0) {
                faces.add(BlockFace.NORTH)
            }
            if (meta and 8 > 0) {
                faces.add(BlockFace.EAST)
            }
            return faces
        }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @Override
    override fun canSilkTouch(): Boolean {
        return true
    }

    companion object {
        private val VINE_DIRECTION_BITS: IntBlockProperty = IntBlockProperty("vine_direction_bits", false, 15)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(VINE_DIRECTION_BITS)
        private fun getMetaFromFaces(faces: Set<BlockFace>): Int {
            var meta = 0
            for (face in faces) {
                meta = meta or getMetaFromFace(face)
            }
            return meta
        }

        private fun getMetaFromFace(face: BlockFace): Int {
            return when (face) {
                SOUTH -> 0x01
                WEST -> 0x02
                NORTH -> 0x04
                EAST -> 0x08
                else -> 0x01
            }
        }
    }
}