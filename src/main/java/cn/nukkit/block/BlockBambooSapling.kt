package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockBambooSapling @PowerNukkitOnly constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BAMBOO_SAPLING

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Bamboo Sapling"

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid) {
                level.useBreakOn(this, null, null, true)
            } else {
                val up: Block = up()
                if (up.getId() === BAMBOO) {
                    val upperBamboo: BlockBamboo = up as BlockBamboo
                    val newState = BlockBamboo()
                    newState.setThick(upperBamboo.isThick())
                    level.setBlock(this, newState, true, true)
                }
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            val up: Block = up()
            if (age == 0 && up.getId() === AIR && level.getFullLight(up) >= BlockCrops.MINIMUM_LIGHT_LEVEL && ThreadLocalRandom.current().nextInt(3) === 0) {
                val newState = BlockBamboo()
                newState.setLeafSize(BlockBamboo.LEAF_SIZE_SMALL)
                val blockGrowEvent = BlockGrowEvent(up, newState)
                level.getServer().getPluginManager().callEvent(blockGrowEvent)
                if (!blockGrowEvent.isCancelled()) {
                    val newState1: Block = blockGrowEvent.getNewState()
                    newState1.y = up.y
                    newState1.x = x
                    newState1.z = z
                    newState1.level = level
                    newState1.place(toItem(), up, this, BlockFace.DOWN, 0.5, 0.5, 0.5, null)
                }
            }
            return type
        }
        return 0
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (isSupportInvalid) {
            return false
        }
        if (this.getLevelBlock() is BlockLiquid || this.getLevelBlockAtLayer(1) is BlockLiquid) {
            return false
        }
        this.level.setBlock(this, this, true, true)
        return true
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        val isBoneMeal: Boolean = item.isFertilizer() //Bonemeal
        if (isBoneMeal || item.getBlock() != null && item.getBlockId() === BlockID.BAMBOO) {
            var success = false
            val block: Block = this.up()
            if (block.getId() === AIR) {
                success = grow(block)
            }
            if (success) {
                if (player != null && player.gamemode and 0x01 === 0) {
                    item.count--
                }
                if (isBoneMeal) {
                    level.addParticle(BoneMealParticle(this))
                } else {
                    level.addSound(block, Sound.BLOCK_BAMBOO_PLACE, 0.8f, 1.0f)
                }
            }
            return true
        }
        return false
    }

    @PowerNukkitOnly
    fun grow(up: Block): Boolean {
        val bamboo = BlockBamboo()
        bamboo.x = x
        bamboo.y = y
        bamboo.z = z
        bamboo.level = level
        return bamboo.grow(up)
    }

    private val isSupportInvalid: Boolean
        private get() {
            val downId: Int = down().getId()
            return downId != DIRT && downId != GRASS && downId != SAND && downId != GRAVEL && downId != PODZOL
        }

    @get:Override
    override val resistance: Double
        get() = 5

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var age: Int
        get() = getDamage() and 0x1
        set(age) {
            var age = age
            age = MathHelper.clamp(age, 0, 1) and 0x1
            setDamage(getDamage() and (DATA_MASK xor 0x1) or age)
        }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockBamboo())
    }

    @get:Override
    override val minX: Double
        get() = x + 0.125

    @get:Override
    override val maxX: Double
        get() = x + 0.875

    @get:Override
    override val minZ: Double
        get() = z + 0.125

    @get:Override
    override val maxZ: Double
        get() = z + 0.875

    @get:Override
    override val maxY: Double
        get() = y + 0.875

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockSapling.PROPERTIES
    }
}