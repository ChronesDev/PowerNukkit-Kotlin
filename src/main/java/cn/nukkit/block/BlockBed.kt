package cn.nukkit.block

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@Log4j2
class BlockBed @JvmOverloads constructor(meta: Int = 0) : BlockTransparentMeta(meta), Faceable, BlockEntityHolder<BlockEntityBed?> {
    @get:Override
    override val id: Int
        get() = BED_BLOCK

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val properties: BlockProperties
        get() = PROPERTIES

    @get:Override
    @get:Nonnull
    @get:PowerNukkitOnly
    @get:Since("1.4.0.0-PN")
    override val blockEntityClass: Class<out BlockEntityBed?>
        get() = BlockEntityBed::class.java

    @get:Override
    @get:Nonnull
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    override val blockEntityType: String
        get() = BlockEntity.BED

    @Override
    override fun canBeActivated(): Boolean {
        return true
    }

    @get:Override
    override val resistance: Double
        get() = 1

    @get:Override
    override val hardness: Double
        get() = 0.2

    @get:Override
    override val name: String
        get() = dyeColor.getName().toString() + " Bed Block"

    @get:Override
    override val maxY: Double
        get() = this.y + 0.5625

    @get:Override
    @get:PowerNukkitOnly
    override val waterloggingLevel: Int
        get() = 1

    @Override
    override fun onActivate(@Nonnull item: Item?): Boolean {
        return this.onActivate(item, null)
    }

    @Override
    override fun onActivate(@Nonnull item: Item?, player: Player?): Boolean {
        val dir: BlockFace = blockFace
        val shouldExplode = this.level.getDimension() !== Level.DIMENSION_OVERWORLD
        val willExplode = shouldExplode && this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)
        val head: Block
        if (isHeadPiece) {
            head = this
        } else {
            head = getSide(dir)
            if (head.getId() !== id || !(head as BlockBed).isHeadPiece || !head.blockFace.equals(dir)) {
                if (player != null && !willExplode) {
                    player.sendMessage(TranslationContainer(TextFormat.GRAY.toString() + "%tile.bed.notValid"))
                }
                if (!shouldExplode) {
                    return true
                }
            }
        }
        val footPart: BlockFace = dir.getOpposite()
        if (shouldExplode) {
            if (!willExplode) {
                return true
            }
            val event = BlockExplosionPrimeEvent(this, 5)
            event.setIncendiary(true)
            if (event.isCancelled()) {
                return true
            }
            level.setBlock(this, get(AIR), false, false)
            onBreak(Item.getBlock(BlockID.AIR))
            level.updateAround(this)
            val explosion = Explosion(this, event.getForce(), this)
            explosion.setFireChance(event.getFireChance())
            if (event.isBlockBreaking()) {
                explosion.explodeA()
            }
            explosion.explodeB()
            return true
        }
        if (player == null || !player.hasEffect(Effect.CONDUIT_POWER) && getLevelBlockAtLayer(1) is BlockWater) {
            return true
        }
        val accessArea: AxisAlignedBB = SimpleAxisAlignedBB(head.x - 2, head.y - 5.5, head.z - 2, head.x + 3, head.y + 2.5, head.z + 3)
                .addCoord(footPart.getXOffset(), 0, footPart.getZOffset())
        if (!accessArea.isVectorInside(player)) {
            player.sendMessage(TranslationContainer(TextFormat.GRAY.toString() + "%tile.bed.tooFar"))
            return true
        }
        val spawn: Location = Location.fromObject(head.add(0.5, 0.5, 0.5), player.getLevel(), player.getYaw(), player.getPitch())
        if (!player.getSpawn().equals(spawn)) {
            player.setSpawn(spawn)
        }
        player.sendMessage(TranslationContainer(TextFormat.GRAY.toString() + "%tile.bed.respawnSet"))
        val time: Int = this.getLevel().getTime() % Level.TIME_FULL
        val isNight = time >= Level.TIME_NIGHT && time < Level.TIME_SUNRISE
        if (!isNight) {
            player.sendMessage(TranslationContainer(TextFormat.GRAY.toString() + "%tile.bed.noSleep"))
            return true
        }
        if (!player.isCreative()) {
            val checkMonsterArea: AxisAlignedBB = SimpleAxisAlignedBB(head.x - 8, head.y - 6.5, head.z - 8, head.x + 9, head.y + 5.5, head.z + 9)
                    .addCoord(footPart.getXOffset(), 0, footPart.getZOffset())
            for (entity in this.level.getCollidingEntities(checkMonsterArea)) {
                if (!entity.isClosed() && entity.isPreventingSleep(player)) {
                    player.sendTranslation(TextFormat.GRAY.toString() + "%tile.bed.notSafe")
                    return true
                }
                // TODO: Check Chicken Jockey, Spider Jockey
            }
        }
        if (!player.sleepOn(head)) {
            player.sendMessage(TranslationContainer(TextFormat.GRAY.toString() + "%tile.bed.occupied"))
        }
        return true
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed support logic")
    @Override
    override fun place(@Nonnull item: Item, @Nonnull block: Block?, @Nonnull target: Block?, @Nonnull face: BlockFace?, fx: Double, fy: Double, fz: Double, @Nullable player: Player?): Boolean {
        val down: Block = this.down()
        if (!(BlockLever.isSupportValid(down, BlockFace.UP) || down is BlockCauldron)) {
            return false
        }
        val direction: BlockFace = if (player == null) BlockFace.NORTH else player.getDirection()
        val next: Block = this.getSide(direction)
        val downNext: Block = next.down()
        if (!next.canBeReplaced() || !(BlockLever.isSupportValid(downNext, BlockFace.UP) || downNext is BlockCauldron)) {
            return false
        }
        val thisLayer0: Block = level.getBlock(this, 0)
        val thisLayer1: Block = level.getBlock(this, 1)
        val nextLayer0: Block = level.getBlock(next, 0)
        val nextLayer1: Block = level.getBlock(next, 1)
        blockFace = direction
        level.setBlock(block, this, true, true)
        if (next is BlockLiquid && next.usesWaterLogging()) {
            level.setBlock(next, 1, next, true, false)
        }
        val head = clone()
        head.isHeadPiece = true
        level.setBlock(next, head, true, true)
        var thisBed: BlockEntityBed? = null
        try {
            thisBed = createBlockEntity(CompoundTag().putByte("color", item.getDamage()))
            val nextBlock: BlockEntityHolder<*> = next.getLevelBlock() as BlockEntityHolder<*>
            nextBlock.createBlockEntity(CompoundTag().putByte("color", item.getDamage()))
        } catch (e: Exception) {
            log.warn("Failed to create the block entity {} at {} and {}", blockEntityType, getLocation(), next.getLocation(), e)
            if (thisBed != null) {
                thisBed.close()
            }
            level.setBlock(thisLayer0, 0, thisLayer0, true)
            level.setBlock(thisLayer1, 1, thisLayer1, true)
            level.setBlock(nextLayer0, 0, nextLayer0, true)
            level.setBlock(nextLayer1, 1, nextLayer1, true)
            return false
        }
        return true
    }

    @Override
    override fun onBreak(item: Item?): Boolean {
        val direction: BlockFace = blockFace
        if (isHeadPiece) { //This is the Top part of bed
            val other: Block = getSide(direction.getOpposite())
            if (other.getId() === id && !(other as BlockBed).isHeadPiece && direction.equals(other.blockFace)) {
                getLevel().setBlock(other, Block.get(BlockID.AIR), true, true)
            }
        } else { //Bottom Part of Bed
            val other: Block = getSide(direction)
            if (other.getId() === id && (other as BlockBed).isHeadPiece && direction.equals(other.blockFace)) {
                getLevel().setBlock(other, Block.get(BlockID.AIR), true, true)
            }
        }
        getLevel().setBlock(this, Block.get(BlockID.AIR), true, false) // Do not update both parts to prevent duplication bug if there is two fallable blocks top of the bed
        return true
    }

    @Override
    override fun toItem(): Item {
        return ItemBed(dyeColor.getWoolData())
    }

    @get:Override
    override val color: BlockColor
        get() = dyeColor.getColor()
    val dyeColor: DyeColor
        get() {
            if (this.level != null) {
                val blockEntity: BlockEntityBed = getBlockEntity()
                if (blockEntity != null) {
                    return blockEntity.getDyeColor()
                }
            }
            return DyeColor.WHITE
        }

    @get:Override
    @set:Override
    @set:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    var blockFace: BlockFace
        get() = getPropertyValue(DIRECTION)
        set(face) {
            setPropertyValue(DIRECTION, face)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isHeadPiece: Boolean
        get() = getBooleanValue(HEAD_PIECE)
        set(headPiece) {
            setBooleanValue(HEAD_PIECE, headPiece)
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var isOccupied: Boolean
        get() = getBooleanValue(OCCUPIED)
        set(occupied) {
            setBooleanValue(OCCUPIED, occupied)
        }

    @Override
    override fun breaksWhenMoved(): Boolean {
        return true
    }

    @Override
    override fun sticksToPiston(): Boolean {
        return false
    }

    @PowerNukkitOnly("In Cloudburst Nukkit, the clone returns Object, which is a different signature!")
    @Since("1.4.0.0-PN")
    @Override
    override fun clone(): BlockBed {
        return super.clone() as BlockBed
    }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val isBedValid: Boolean
        get() {
            val dir: BlockFace = blockFace
            val head: Block
            val foot: Block
            if (isHeadPiece) {
                head = this
                foot = getSide(dir.getOpposite())
            } else {
                head = getSide(dir)
                foot = this
            }
            return (head.getId() === foot.getId() && (head as BlockBed).isHeadPiece && head.blockFace.equals(dir)
                    && !(foot as BlockBed).isHeadPiece && foot.blockFace.equals(dir))
        }

    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val headPart: BlockBed?
        get() {
            if (isHeadPiece) {
                return this
            }
            val dir: BlockFace = blockFace
            val head: Block = getSide(dir)
            return if (head.getId() === id && (head as BlockBed).isHeadPiece && head.blockFace.equals(dir)) {
                head
            } else null
        }

    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    val footPart: BlockBed?
        get() {
            if (!isHeadPiece) {
                return this
            }
            val dir: BlockFace = blockFace
            val foot: Block = getSide(dir.getOpposite())
            return if (foot.getId() === id && !(foot as BlockBed).isHeadPiece && foot.blockFace.equals(dir)) {
                foot
            } else null
        }

    companion object {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val HEAD_PIECE: BooleanBlockProperty = BooleanBlockProperty("head_piece_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val OCCUPIED: BooleanBlockProperty = BooleanBlockProperty("occupied_bit", false)

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        val PROPERTIES: BlockProperties = BlockProperties(DIRECTION, OCCUPIED, HEAD_PIECE)
    }
}