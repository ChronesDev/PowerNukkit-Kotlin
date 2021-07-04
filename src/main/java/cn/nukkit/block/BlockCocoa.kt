package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author CreeperFace
 * @since 27. 10. 2016
 */
class BlockCocoa @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable {
    @get:Override
    override val id: Int
        get() = COCOA

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Cocoa"

    @get:Override
    override val minX: Double
        get() = this.x + relativeBoundingBox.getMinX()

    @get:Override
    override val maxX: Double
        get() = this.x + relativeBoundingBox.getMaxX()

    @get:Override
    override val minY: Double
        get() = this.y + relativeBoundingBox.getMinY()

    @get:Override
    override val maxY: Double
        get() = this.y + relativeBoundingBox.getMaxY()

    @get:Override
    override val minZ: Double
        get() = this.z + relativeBoundingBox.getMinZ()

    @get:Override
    override val maxZ: Double
        get() = this.z + relativeBoundingBox.getMaxZ()
    private val relativeBoundingBox: AxisAlignedBB
        private get() {
            var damage: Int = this.getDamage()
            if (damage > 11) {
                this.setDamage(11.also { damage = it })
            }
            val boundingBox: AxisAlignedBB? = ALL[damage]
            if (boundingBox != null) return boundingBox
            val bbs: Array<AxisAlignedBB>
            bbs = when (getDamage()) {
                1, 5, 9 -> EAST
                2, 6, 10 -> SOUTH
                3, 7, 11 -> WEST
                else -> NORTH
            }
            return bbs[this.getDamage() shr 2].also { ALL[damage] = it }
        }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block, @Nonnull face: BlockFace, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        if (target.getId() === Block.WOOD && target.getDamage() and 0x03 === BlockWood.JUNGLE) {
            if (face !== BlockFace.DOWN && face !== BlockFace.UP) {
                val faces = intArrayOf(
                        0,
                        0,
                        0,
                        2,
                        3,
                        1)
                this.setDamage(faces[face.getIndex()])
                this.level.setBlock(block, this, true, true)
                return true
            }
        }
        return false
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val faces = intArrayOf(
                    3, 4, 2, 5, 3, 4, 2, 5, 3, 4, 2, 5
            )
            val side: Block = this.getSide(BlockFace.fromIndex(faces[this.getDamage()]))
            if (side.getId() !== Block.WOOD && side.getDamage() !== BlockWood.JUNGLE) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) === 1) {
                if (growthStage < 2) {
                    if (!grow()) {
                        return Level.BLOCK_UPDATE_RANDOM
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM
            }
        }
        return 0
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        if (item.isFertilizer()) {
            if (growthStage < 2) {
                if (!grow()) {
                    return false
                }
                this.level.addParticle(BoneMealParticle(this))
                if (player != null && player.gamemode and 0x01 === 0) {
                    item.count--
                }
            }
            return true
        }
        return false
    }

    val growthStage: Int
        get() = this.getDamage() / 4

    fun grow(): Boolean {
        val block: Block = this.clone()
        block.setDamage(block.getDamage() + 4)
        val ev = BlockGrowEvent(this, block)
        Server.getInstance().getPluginManager().callEvent(ev)
        return !ev.isCancelled() && this.getLevel().setBlock(this, ev.getNewState(), true, true)
    }

    @get:Override
    override val resistance: Double
        get() = 15

    @get:Override
    override val hardness: Double
        get() = 0.2

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

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
        return MinecraftItemID.COCOA_BEANS.get(1)
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(
                MinecraftItemID.COCOA_BEANS.get(if (this.getDamage() >= 8) 3 else 1)
        )
    }

    @get:Override
    val blockFace: BlockFace
        get() = BlockFace.fromHorizontalIndex(this.getDamage() and 0x07)

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val AGE: IntBlockProperty = IntBlockProperty("age", false, 2)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, AGE)
        protected val EAST: Array<AxisAlignedBB> = arrayOf<SimpleAxisAlignedBB>(SimpleAxisAlignedBB(0.6875, 0.4375, 0.375, 0.9375, 0.75, 0.625), SimpleAxisAlignedBB(0.5625, 0.3125, 0.3125, 0.9375, 0.75, 0.6875), SimpleAxisAlignedBB(0.5625, 0.3125, 0.3125, 0.9375, 0.75, 0.6875))
        protected val WEST: Array<AxisAlignedBB> = arrayOf<SimpleAxisAlignedBB>(SimpleAxisAlignedBB(0.0625, 0.4375, 0.375, 0.3125, 0.75, 0.625), SimpleAxisAlignedBB(0.0625, 0.3125, 0.3125, 0.4375, 0.75, 0.6875), SimpleAxisAlignedBB(0.0625, 0.3125, 0.3125, 0.4375, 0.75, 0.6875))
        protected val NORTH: Array<AxisAlignedBB> = arrayOf<SimpleAxisAlignedBB>(SimpleAxisAlignedBB(0.375, 0.4375, 0.0625, 0.625, 0.75, 0.3125), SimpleAxisAlignedBB(0.3125, 0.3125, 0.0625, 0.6875, 0.75, 0.4375), SimpleAxisAlignedBB(0.3125, 0.3125, 0.0625, 0.6875, 0.75, 0.4375))
        protected val SOUTH: Array<AxisAlignedBB> = arrayOf<SimpleAxisAlignedBB>(SimpleAxisAlignedBB(0.375, 0.4375, 0.6875, 0.625, 0.75, 0.9375), SimpleAxisAlignedBB(0.3125, 0.3125, 0.5625, 0.6875, 0.75, 0.9375), SimpleAxisAlignedBB(0.3125, 0.3125, 0.5625, 0.6875, 0.75, 0.9375))
        protected val ALL: Array<AxisAlignedBB?> = arrayOfNulls<AxisAlignedBB>(12)
    }
}