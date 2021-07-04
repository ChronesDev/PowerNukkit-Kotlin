package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockSapling @JvmOverloads constructor(meta: Int = 0) : BlockFlowable(meta) {
    @get:Override
    override val id: Int
        get() = SAPLING

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var woodType: WoodType
        get() = getPropertyValue(SAPLING_TYPE)
        set(woodType) {
            setPropertyValue(SAPLING_TYPE, woodType)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isAged: Boolean
        get() = getBooleanValue(AGED)
        set(aged) {
            setBooleanValue(AGED, aged)
        }

    @get:Override
    override val name: String
        get() = woodType.getEnglishName().toString() + " Sapling"

    @Override
    override fun place(@Nonnull item: Item?, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (BlockFlower.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true, true)
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
        if (item.isFertilizer()) { // BoneMeal
            if (player != null && !player.isCreative()) {
                item.count--
            }
            this.level.addParticle(BoneMealParticle(this))
            if (ThreadLocalRandom.current().nextFloat() >= 0.45) {
                return true
            }
            grow()
            return true
        }
        return false
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will break on block update if the supporting block is invalid")
    @Override
    override fun onUpdate(type: Int): Int {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockFlower.isSupportValid(down())) {
                this.getLevel().useBreakOn(this)
                return Level.BLOCK_UPDATE_NORMAL
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) { //Growth
            if (ThreadLocalRandom.current().nextInt(1, 8) === 1 && getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                if (isAged) {
                    grow()
                } else {
                    isAged = true
                    this.getLevel().setBlock(this, this, true)
                    return Level.BLOCK_UPDATE_RANDOM
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM
            }
        }
        return Level.BLOCK_UPDATE_NORMAL
    }

    private fun grow() {
        var generator: BasicGenerator? = null
        var bigTree = false
        var vector3 = Vector3()
        when (woodType) {
            JUNGLE -> {
                var vector2: Vector2
                if (findSaplings(WoodType.JUNGLE).also { vector2 = it } != null) {
                    vector3 = this.add(vector2.getFloorX(), 0, vector2.getFloorY())
                    generator = ObjectJungleBigTree(10, 20, Block.get(BlockID.WOOD, BlockWood.JUNGLE), Block.get(BlockID.LEAVES, BlockLeaves.JUNGLE))
                    bigTree = true
                }
                if (!bigTree) {
                    generator = NewJungleTree(4, 7)
                    vector3 = this.add(0, 0, 0)
                }
            }
            ACACIA -> {
                generator = ObjectSavannaTree()
                vector3 = this.add(0, 0, 0)
            }
            DARK_OAK -> {
                if (findSaplings(WoodType.DARK_OAK).also { vector2 = it } != null) {
                    vector3 = this.add(vector2.getFloorX(), 0, vector2.getFloorY())
                    generator = ObjectDarkOakTree()
                    bigTree = true
                }
                if (!bigTree) {
                    return
                }
            }
            else -> {
                val chunkManager = ListChunkManager(this.level)
                ObjectTree.growTree(chunkManager, this.getFloorX(), this.getFloorY(), this.getFloorZ(), NukkitRandom(), woodType, false)
                val ev = StructureGrowEvent(this, chunkManager.getBlocks())
                this.level.getServer().getPluginManager().callEvent(ev)
                if (ev.isCancelled()) {
                    return
                }
                for (block in ev.getBlockList()) {
                    this.level.setBlock(block, block)
                }
                return
            }
        }
        if (bigTree) {
            this.level.setBlock(vector3, get(AIR), true, false)
            this.level.setBlock(vector3.add(1, 0, 0), get(AIR), true, false)
            this.level.setBlock(vector3.add(0, 0, 1), get(AIR), true, false)
            this.level.setBlock(vector3.add(1, 0, 1), get(AIR), true, false)
        } else {
            this.level.setBlock(this, get(AIR), true, false)
        }
        val chunkManager = ListChunkManager(this.level)
        val success: Boolean = generator.generate(chunkManager, NukkitRandom(), vector3)
        val ev = StructureGrowEvent(this, chunkManager.getBlocks())
        this.level.getServer().getPluginManager().callEvent(ev)
        if (ev.isCancelled() || !success) {
            if (bigTree) {
                this.level.setBlock(vector3, this, true, false)
                this.level.setBlock(vector3.add(1, 0, 0), this, true, false)
                this.level.setBlock(vector3.add(0, 0, 1), this, true, false)
                this.level.setBlock(vector3.add(1, 0, 1), this, true, false)
            } else {
                this.level.setBlock(this, this, true, false)
            }
            return
        }
        for (block in ev.getBlockList()) {
            this.level.setBlock(block, block)
        }
    }

    private fun findSaplings(type: WoodType): Vector2? {
        val validVectorsList: List<List<Vector2>> = ArrayList()
        validVectorsList.add(Arrays.asList(Vector2(0, 0), Vector2(1, 0), Vector2(0, 1), Vector2(1, 1)))
        validVectorsList.add(Arrays.asList(Vector2(0, 0), Vector2(-1, 0), Vector2(0, -1), Vector2(-1, -1)))
        validVectorsList.add(Arrays.asList(Vector2(0, 0), Vector2(1, 0), Vector2(0, -1), Vector2(1, -1)))
        validVectorsList.add(Arrays.asList(Vector2(0, 0), Vector2(-1, 0), Vector2(0, 1), Vector2(-1, 1)))
        for (validVectors in validVectorsList) {
            var correct = true
            for (vector2 in validVectors) {
                if (!this.isSameType(this.add(vector2.x, 0, vector2.y), type)) correct = false
            }
            if (correct) {
                var lowestX = 0
                var lowestZ = 0
                for (vector2 in validVectors) {
                    if (vector2.getFloorX() < lowestX) lowestX = vector2.getFloorX()
                    if (vector2.getFloorY() < lowestZ) lowestZ = vector2.getFloorY()
                }
                return Vector2(lowestX, lowestZ)
            }
        }
        return null
    }

    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", reason = "Checking magic value directly is depreacated", replaceWith = "isSameType(Vector3,WoodType)")
    fun isSameType(pos: Vector3?, type: Int): Boolean {
        val block: Block = this.level.getBlock(pos)
        return block.getId() === id && block.getDamage() and 0x07 === type and 0x07
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun isSameType(pos: Vector3?, type: WoodType): Boolean {
        val block: Block = this.level.getBlock(pos)
        return block.getId() === id && (block as BlockSapling).woodType === type
    }

    @get:Override
    override val color: BlockColor
        get() = BlockColor.FOLIAGE_BLOCK_COLOR

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val SAPLING_TYPE: BlockProperty<WoodType> = ArrayBlockProperty("sapling_type", true, WoodType::class.java)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val AGED: BooleanBlockProperty = BooleanBlockProperty("age_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(SAPLING_TYPE, AGED)

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.OAK", by = "PowerNukkit", reason = "Use the new BlockProperty system instead")
        val OAK = 0

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.SPRUCE", by = "PowerNukkit", reason = "Use the new BlockProperty system instead")
        val SPRUCE = 1

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.BIRCH", by = "PowerNukkit", reason = "Use the new BlockProperty system instead")
        val BIRCH = 2

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", by = "PowerNukkit", replaceWith = "ObjectTree.growTree(ChunkManager level, int x, int y, int z, NukkitRandom random, WoodType.BIRCH, true)", reason = "Shouldn't even be here")
        val BIRCH_TALL = 8 or BIRCH

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.JUNGLE", by = "PowerNukkit", reason = "Use the new BlockProperty system instead")
        val JUNGLE = 3

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.ACACIA", by = "PowerNukkit", reason = "Use the new BlockProperty system instead")
        val ACACIA = 4

        @Deprecated
        @DeprecationDetails(since = "1.4.0.0-PN", replaceWith = "WoodType.DARK_OAK", by = "PowerNukkit", reason = "Use the new BlockProperty system instead")
        val DARK_OAK = 5
    }
}