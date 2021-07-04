package cn.nukkit.block

import cn.nukkit.Player

@PowerNukkitOnly
class BlockBamboo @PowerNukkitOnly constructor(meta: Int) : BlockTransparentMeta(meta) {
    @PowerNukkitOnly
    constructor() : this(0) {
    }

    @get:Override
    override val id: Int
        get() = BAMBOO

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val name: String
        get() = "Bamboo"

    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid) {
                level.scheduleUpdate(this, 0)
            }
            return type
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            level.useBreakOn(this, null, null, true)
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            val up: Block = up()
            if (age == 0 && up.getId() === AIR && level.getFullLight(up) >= BlockCrops.MINIMUM_LIGHT_LEVEL && ThreadLocalRandom.current().nextInt(3) === 0) {
                grow(up)
            }
            return type
        }
        return 0
    }

    @PowerNukkitOnly
    fun grow(up: Block): Boolean {
        val newState = BlockBamboo()
        if (isThick) {
            newState.isThick = true
            newState.leafSize = LEAF_SIZE_LARGE
        } else {
            newState.leafSize = LEAF_SIZE_SMALL
        }
        val blockGrowEvent = BlockGrowEvent(up, newState)
        level.getServer().getPluginManager().callEvent(blockGrowEvent)
        if (!blockGrowEvent.isCancelled()) {
            val newState1: Block = blockGrowEvent.getNewState()
            newState1.x = x
            newState1.y = up.y
            newState1.z = z
            newState1.level = level
            newState1.place(toItem(), up, this, BlockFace.DOWN, 0.5, 0.5, 0.5, null)
            return true
        }
        return false
    }

    @PowerNukkitOnly
    fun countHeight(): Int {
        var count = 0
        var opt: Optional<Block?>
        var down: Block = this
        while (down.down().firstInLayers { b -> b.getId() === BAMBOO }.also { opt = it }.isPresent()) {
            down = opt.get()
            if (++count >= 16) {
                break
            }
        }
        return count
    }

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        var down: Block = down()
        val downId: Int = down.getId()
        if (downId != BAMBOO && downId != BAMBOO_SAPLING) {
            val sampling = BlockBambooSapling()
            sampling.x = x
            sampling.y = y
            sampling.z = z
            sampling.level = level
            return sampling.place(item, block, target, face, fx, fy, fz, player)
        }
        var canGrow = true
        if (downId == BAMBOO_SAPLING) {
            if (player != null) {
                val animatePacket = AnimatePacket()
                animatePacket.action = AnimatePacket.Action.SWING_ARM
                animatePacket.eid = player.getId()
                this.getLevel().addChunkPacket(player.getChunkX(), player.getChunkZ(), animatePacket)
            }
            leafSize = LEAF_SIZE_SMALL
        }
        if (down is BlockBamboo) {
            var bambooDown = down
            canGrow = bambooDown.age == 0
            val thick = bambooDown.isThick
            if (!thick) {
                var setThick = true
                for (i in 2..3) {
                    if (getSide(BlockFace.DOWN, i).getId() !== BAMBOO) {
                        setThick = false
                    }
                }
                if (setThick) {
                    isThick = true
                    leafSize = LEAF_SIZE_LARGE
                    bambooDown.leafSize = LEAF_SIZE_SMALL
                    bambooDown.isThick = true
                    bambooDown.age = 1
                    this.level.setBlock(bambooDown, bambooDown, false, true)
                    while (down.down().also { down = it } is BlockBamboo) {
                        bambooDown = down as BlockBamboo
                        bambooDown.isThick = true
                        bambooDown.leafSize = LEAF_SIZE_NONE
                        bambooDown.age = 1
                        this.level.setBlock(bambooDown, bambooDown, false, true)
                    }
                } else {
                    leafSize = LEAF_SIZE_SMALL
                    bambooDown.age = 1
                    this.level.setBlock(bambooDown, bambooDown, false, true)
                }
            } else {
                isThick = true
                leafSize = LEAF_SIZE_LARGE
                age = 0
                bambooDown.leafSize = LEAF_SIZE_LARGE
                bambooDown.age = 1
                this.level.setBlock(bambooDown, bambooDown, false, true)
                down = bambooDown.down()
                if (down is BlockBamboo) {
                    bambooDown = down as BlockBamboo
                    bambooDown.leafSize = LEAF_SIZE_SMALL
                    bambooDown.age = 1
                    this.level.setBlock(bambooDown, bambooDown, false, true)
                    down = bambooDown.down()
                    if (down is BlockBamboo) {
                        bambooDown = down as BlockBamboo
                        bambooDown.leafSize = LEAF_SIZE_NONE
                        bambooDown.age = 1
                        this.level.setBlock(bambooDown, bambooDown, false, true)
                    }
                }
            }
        } else if (isSupportInvalid) {
            return false
        }
        val height = if (canGrow) countHeight() else 0
        if (!canGrow || height >= 15 || height >= 11 && ThreadLocalRandom.current().nextFloat() < 0.25f) {
            age = 1
        }
        this.level.setBlock(this, this, false, true)
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        val down: Optional<Block> = down().firstInLayers { b -> b is BlockBamboo }
        if (down.isPresent()) {
            val bambooDown = down.get() as BlockBamboo
            val height = bambooDown.countHeight()
            if (height < 15 && (height < 11 || ThreadLocalRandom.current().nextFloat() >= 0.25f)) {
                bambooDown.age = 0
                this.level.setBlock(bambooDown, bambooDown.layer, bambooDown, false, true)
            }
        }
        return super.onBreak(item)
    }

    @Override
    override fun canPassThrough(): Boolean {
        return true
    }

    private val isSupportInvalid: Boolean
        private get() {
            val downId: Int = down().getId()
            return downId != BAMBOO && downId != DIRT && downId != GRASS && downId != SAND && downId != GRAVEL && downId != PODZOL && downId != BAMBOO_SAPLING
        }

    @Override
    override fun toItem(): Item {
        return ItemBlock(BlockBamboo())
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    @get:Override
    override val hardness: Double
        get() = 2

    @get:Override
    override val resistance: Double
        get() = 5

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var isThick: Boolean
        get() = bambooStalkThickness.equals(BambooStalkThickness.THICK)
        set(thick) {
            bambooStalkThickness = if (thick) BambooStalkThickness.THICK else BambooStalkThickness.THIN
        }

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.5.0.0-PN")
    @set:PowerNukkitOnly
    var bambooStalkThickness: BambooStalkThickness
        get() = getPropertyValue(STALK_THICKNESS)
        set(value) {
            setPropertyValue(STALK_THICKNESS, value)
        }

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_AXE

    @get:DeprecationDetails(by = "PowerNukkit", since = "1.5.0.0-PN", replaceWith = "getBambooLeafSize", reason = "magic values")
    @get:Deprecated
    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    @set:DeprecationDetails(by = "PowerNukkit", since = "1.5.0.0-PN", replaceWith = "getBambooLeafSize", reason = "magic values")
    @set:Deprecated
    var leafSize: Int
        get() = bambooLeafSize.ordinal()
        set(leafSize) {
            var leafSize = leafSize
            leafSize = MathHelper.clamp(leafSize, LEAF_SIZE_NONE, LEAF_SIZE_LARGE) and 3
            setDamage(getDamage() and (DATA_MASK xor 6) or (leafSize shl 1))
        }

    @get:Since("1.5.0.0-PN")
    @get:PowerNukkitOnly
    val bambooLeafSize: BambooLeafSize
        get() = getPropertyValue(LEAF_SIZE)

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @Override
    override fun onActivate(@Nonnull item: Item, player: Player?): Boolean {
        val itemIsBoneMeal: Boolean = item.isFertilizer() //Bonemeal
        if (itemIsBoneMeal || item.getBlock() != null && item.getBlockId() === BlockID.BAMBOO) {
            var top = y as Int
            var count = 1
            for (i in 1..16) {
                val id: Int = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ())
                if (id == BAMBOO) {
                    count++
                } else {
                    break
                }
            }
            for (i in 1..16) {
                val id: Int = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() + i, this.getFloorZ())
                if (id == BAMBOO) {
                    top++
                    count++
                } else {
                    break
                }
            }
            if (itemIsBoneMeal && count >= 15) {
                return false
            }
            var success = false
            val block: Block = this.up(top - y as Int + 1)
            if (block.getId() === BlockID.AIR) {
                success = grow(block)
            }
            if (success) {
                if (player != null && player.isSurvival()) {
                    item.count--
                }
                if (itemIsBoneMeal) {
                    level.addParticle(BoneMealParticle(this))
                } else {
                    level.addSound(block, Sound.BLOCK_BAMBOO_PLACE, 0.8f, 1.0f)
                }
            }
            return true
        }
        return false
    }

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var age: Int
        get() = if (getBooleanValue(AGED)) 1 else 0
        set(age) {
            var age = age
            age = MathHelper.clamp(age, 0, 1)
            setBooleanValue(AGED, age == 1)
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val STALK_THICKNESS: ArrayBlockProperty<BambooStalkThickness> = ArrayBlockProperty(
                "bamboo_stalk_thickness", false, BambooStalkThickness::class.java
        )

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val LEAF_SIZE: ArrayBlockProperty<BambooLeafSize> = ArrayBlockProperty(
                "bamboo_leaf_size", false, BambooLeafSize::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(STALK_THICKNESS, LEAF_SIZE, AGED)

        @PowerNukkitOnly
        val LEAF_SIZE_NONE = 0

        @PowerNukkitOnly
        val LEAF_SIZE_SMALL = 1

        @PowerNukkitOnly
        val LEAF_SIZE_LARGE = 2
    }
}