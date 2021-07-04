package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockKelp @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(meta: Int) : BlockFlowable(meta) {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BLOCK_KELP

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Kelp"

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var age: Int
        get() = getIntValue(KELP_AGE)
        set(age) {
            setIntValue(KELP_AGE, age)
        }

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        val down: Block = down()
        val layer1Block: Block = block.getLevelBlockAtLayer(1)
        return if ((down.getId() === BLOCK_KELP || down.isSolid()) && down.getId() !== MAGMA && down.getId() !== ICE && down.getId() !== SOUL_SAND &&
                layer1Block is BlockWater && layer1Block.isSourceOrFlowingDown()) {
            if (layer1Block.isFlowingDown()) {
                this.getLevel().setBlock(this, 1, get(WATER), true, false)
            }
            val maxAge: Int = KELP_AGE.getMaxValue()
            if (down.getId() === BLOCK_KELP && down.getIntValue(KELP_AGE) !== maxAge - 1) {
                down.setIntValue(KELP_AGE, maxAge - 1)
                this.getLevel().setBlock(down, down, true, true)
            }

            //Placing it by hand gives it a random age value between 0 and 24.
            age = ThreadLocalRandom.current().nextInt(maxAge)
            this.getLevel().setBlock(this, this, true, true)
            true
        } else {
            false
        }
    }

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            val blockLayer1: Block = getLevelBlockAtLayer(1)
            if (blockLayer1 !is BlockIceFrosted &&
                    (blockLayer1 !is BlockWater || !blockLayer1.isSourceOrFlowingDown())) {
                this.getLevel().useBreakOn(this)
                return type
            }
            val down: Block = down()
            if (!down.isSolid() && down.getId() !== BLOCK_KELP || down.getId() === MAGMA || down.getId() === ICE || down.getId() === SOUL_SAND) {
                this.getLevel().useBreakOn(this)
                return type
            }
            if (blockLayer1 is BlockWater && blockLayer1.isFlowingDown()) {
                this.getLevel().setBlock(this, 1, get(WATER), true, false)
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(100) <= 14) {
                grow()
            }
            return type
        }
        return super.onUpdate(type)
    }

    @PowerNukkitOnly
    fun grow(): Boolean {
        val age = age
        val maxValue: Int = KELP_AGE.getMaxValue()
        if (age < maxValue) {
            val up: Block = up()
            if (up is BlockWater && up.isSourceOrFlowingDown()) {
                val grown: Block = BlockState.of(BLOCK_KELP, age + 1).getBlock()
                val ev = BlockGrowEvent(this, grown)
                Server.getInstance().getPluginManager().callEvent(ev)
                if (!ev.isCancelled()) {
                    this.age = maxValue
                    this.getLevel().setBlock(this, 0, this, true, true)
                    this.getLevel().setBlock(up, 1, get(WATER), true, false)
                    this.getLevel().setBlock(up, 0, ev.getNewState(), true, true)
                    return true
                }
            }
        }
        return false
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        val down: Block = down()
        if (down.getId() === BLOCK_KELP) {
            this.getLevel().setBlock(down, BlockState.of(BLOCK_KELP, ThreadLocalRandom.current().nextInt(KELP_AGE.getMaxValue())).getBlock(), true, true)
        }
        this.getLevel().setBlock(this, get(AIR), true, true)
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        //Bone meal
        if (item.isFertilizer()) {
            val x = this.x as Int
            val z = this.z as Int
            for (y in this.y as Int + 1..254) {
                val blockStateAbove: BlockState = getLevel().getBlockStateAt(x, y, z)
                val blockIdAbove: Int = blockStateAbove.getBlockId()
                if (blockIdAbove != BLOCK_KELP) {
                    if (blockIdAbove == WATER || blockIdAbove == STILL_WATER) {
                        if ((blockStateAbove.getBlock() as BlockWater).isSourceOrFlowingDown()) {
                            val highestKelp = getLevel().getBlock(x, y - 1, z) as BlockKelp
                            if (highestKelp.grow()) {
                                this.level.addParticle(BoneMealParticle(this))
                                if (player != null && player.gamemode and 0x01 === 0) {
                                    item.count--
                                }
                                return true
                            }
                        }
                    }
                    return false
                }
            }
            return true
        }
        return false
    }

    @Override
    override fun toItem(): Item {
        return ItemKelp()
    }

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 2

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val KELP_AGE: IntBlockProperty = IntBlockProperty("kelp_age", false, 25)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(KELP_AGE)
    }
}