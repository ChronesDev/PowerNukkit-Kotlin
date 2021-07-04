package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockSeaPickle @PowerNukkitOnly protected constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = SEA_PICKLE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Sea Pickle"

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var isDead: Boolean
        get() = getDamage() and 0x4 === 0x4
        set(dead) {
            if (dead) {
                setDamage(getDamage() or 0x4)
            } else {
                setDamage(getDamage() xor 0x4)
            }
        }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val down: Block = down()
            if (!down.isSolid() || down.getId() === ICE) {
                this.getLevel().useBreakOn(this)
                return type
            }
            val layer1: Block = getLevelBlockAtLayer(1)
            if (layer1 is BlockWater || layer1.getId() === ICE_FROSTED) {
                if (isDead && (layer1.getId() === ICE_FROSTED || layer1.getDamage() === 0 || layer1.getDamage() === 8)) {
                    val event = BlockFadeEvent(this, BlockSeaPickle(getDamage() xor 0x4))
                    if (!event.isCancelled()) {
                        this.getLevel().setBlock(this, event.getNewState(), true, true)
                    }
                    return type
                }
            } else if (!isDead) {
                val event = BlockFadeEvent(this, BlockSeaPickle(getDamage() xor 0x4))
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(this, event.getNewState(), true, true)
                }
            }
            return type
        }
        return 0
    }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (target.getId() === SEA_PICKLE && target.getDamage() and 3 < 3) {
            target.setDamage(target.getDamage() + 1)
            this.getLevel().setBlock(target, target, true, true)
            return true
        }
        val down: Block = block.down().getLevelBlockAtLayer(0)
        if (down.isSolid() && down.getId() !== ICE) {
            if (down is BlockSlab || down is BlockStairs || block.getId() === BUBBLE_COLUMN) {
                return false
            }
            val layer1: Block = block.getLevelBlockAtLayer(1)
            if (layer1 is BlockWater) {
                if (layer1.getDamage() !== 0 && layer1.getDamage() !== 8) {
                    return false
                }
                if (layer1.getDamage() === 8) {
                    this.getLevel().setBlock(block, 1, BlockWater(), true, false)
                }
            } else {
                isDead = true
            }
            this.getLevel().setBlock(block, 0, this, true, true)
            return true
        }
        return false
    }

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {

        //Bone meal
        if (item.isFertilizer() && down().getId() === CORAL_BLOCK && !isDead) {
            val block = clone() as BlockSeaPickle
            block.setDamage(3)
            val blockGrowEvent = BlockGrowEvent(this, block)
            Server.getInstance().getPluginManager().callEvent(blockGrowEvent)
            if (blockGrowEvent.isCancelled()) {
                return false
            }
            this.getLevel().setBlock(this, blockGrowEvent.getNewState(), false, true)
            this.level.addParticle(BoneMealParticle(this))
            if (player != null && player.gamemode and 0x01 === 0) {
                item.count--
            }
            val random: ThreadLocalRandom = ThreadLocalRandom.current()
            val blocksAround: Array<Block> = this.getLevel().getCollisionBlocks(SimpleAxisAlignedBB(x - 2, y - 2, z - 2, x + 3, y, z + 3))
            for (blockNearby in blocksAround) {
                if (blockNearby.getId() === CORAL_BLOCK) {
                    val up: Block = blockNearby.up()
                    if (up is BlockWater && (up.getDamage() === 0 || up.getDamage() === 8) && random.nextInt(6) === 0 && Vector2(up.x, up.z).distance(Vector2(this.x, this.z)) <= 2) {
                        val blockSpreadEvent = BlockSpreadEvent(up, this, BlockSeaPickle(random.nextInt(3)))
                        if (!blockSpreadEvent.isCancelled()) {
                            this.getLevel().setBlock(up, 1, BlockWater(), true, false)
                            this.getLevel().setBlock(up, blockSpreadEvent.getNewState(), true, true)
                        }
                    }
                }
            }
        }
        return super.onActivate(item, player)
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @get:Override
    override val lightLevel: Int
        get() = if (isDead) {
            0
        } else {
            (getDamage() + 2) * 3
        }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockSeaPickle())
    }

    @Override
    override fun getDrops(item: Item?): Array<Item> {
        return arrayOf<Item>(ItemBlock(BlockSeaPickle(), 0, (getDamage() and 0x3) + 1))
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val DEAD: BooleanBlockProperty = BooleanBlockProperty("dead_bit", false)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val CLUSTER_COUNT: IntBlockProperty = IntBlockProperty("cluster_count", false, 3)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(CLUSTER_COUNT, DEAD)
    }
}