package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author Angelic47 (Nukkit Project)
 */
class BlockSponge @JvmOverloads constructor(meta: Int = 0) : BlockSolidMeta(meta) {
    @get:Override
    override val id: Int
        get() = SPONGE

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    override val hardness: Double
        get() = 0.6

    @get:Override
    override val resistance: Double
        get() = 3

    @get:Override
    override val toolType: Int
        get() = ItemTool.TYPE_HOE

    @get:Override
    override val name: String
        get() = NAMES[this.getDamage() and 1]

    @get:Override
    override val color: BlockColor
        get() = BlockColor.YELLOW_BLOCK_COLOR

    @Override
    fun place(@Nonnull item: Item?, @Nonnull block: Block, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, player: Player?): Boolean {
        if (this.getDamage() === cn.nukkit.block.BlockSponge.Companion.WET && level.getDimension() === Level.DIMENSION_NETHER) {
            level.setBlock(block, Block.get(BlockID.SPONGE, DRY), true, true)
            this.getLevel().addLevelEvent(block.add(0.5, 0.875, 0.5), LevelEventPacket.EVENT_SOUND_EXPLODE)
            val random: ThreadLocalRandom = ThreadLocalRandom.current()
            for (i in 0..7) {
                level.addParticle(CloudParticle(block.getLocation().add(random.nextDouble(), 1, random.nextDouble())))
            }
            return true
        } else if (this.getDamage() === cn.nukkit.block.BlockSponge.Companion.DRY && block is BlockWater && performWaterAbsorb(block)) {
            level.setBlock(block, Block.get(BlockID.SPONGE, WET), true, true)
            for (i in 0..3) {
                val packet = LevelEventPacket()
                packet.evid = LevelEventPacket.EVENT_PARTICLE_DESTROY
                packet.x = block.getX() as Float + 0.5f
                packet.y = block.getY() as Float + 1f
                packet.z = block.getZ() as Float + 0.5f
                packet.data = GlobalBlockPalette.getOrCreateRuntimeId(BlockID.WATER, 0)
                level.addChunkPacket(getChunkX(), getChunkZ(), packet)
            }
            return true
        }
        return super.place(item, block, target, face, fx, fy, fz, player)
    }

    private fun performWaterAbsorb(block: Block): Boolean {
        val entries: Queue<Entry> = ArrayDeque()
        entries.add(Entry(block, 0))
        var entry: Entry
        var waterRemoved = 0
        while (waterRemoved < 64 && entries.poll().also { entry = it } != null) {
            for (face in BlockFace.values()) {
                val faceBlock: Block = entry.block.getSideAtLayer(0, face)
                val faceBlock1: Block = faceBlock.getLevelBlockAtLayer(1)
                if (faceBlock is BlockWater) {
                    this.getLevel().setBlockStateAt(faceBlock.getFloorX(), faceBlock.getFloorY(), faceBlock.getFloorZ(), BlockState.AIR)
                    this.getLevel().updateAround(faceBlock)
                    waterRemoved++
                    if (entry.distance < 6) {
                        entries.add(Entry(faceBlock, entry.distance + 1))
                    }
                } else if (faceBlock1 is BlockWater) {
                    if (faceBlock.getId() === BlockID.BLOCK_KELP || faceBlock.getId() === BlockID.SEAGRASS || faceBlock.getId() === BlockID.SEA_PICKLE || faceBlock is BlockCoralFan) {
                        faceBlock.getLevel().useBreakOn(faceBlock)
                    }
                    this.getLevel().setBlockStateAt(faceBlock1.getFloorX(), faceBlock1.getFloorY(), faceBlock1.getFloorZ(), 1, BlockState.AIR)
                    this.getLevel().updateAround(faceBlock1)
                    waterRemoved++
                    if (entry.distance < 6) {
                        entries.add(Entry(faceBlock1, entry.distance + 1))
                    }
                }
            }
        }
        return waterRemoved > 0
    }

    private class Entry(block: Block, distance: Int) {
        val block: Block
        val distance: Int

        init {
            this.block = block
            this.distance = distance
        }
    }

    companion object {
        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val SPONGE_TYPE: ArrayBlockProperty<SpongeType> = ArrayBlockProperty("sponge_type", true, SpongeType::class.java)

        @PowerNukkitOnly
        @Since("1.5.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(SPONGE_TYPE)
        const val DRY = 0
        const val WET = 1
        private val NAMES = arrayOf(
                "Sponge",
                "Wet sponge"
        )
    }
}