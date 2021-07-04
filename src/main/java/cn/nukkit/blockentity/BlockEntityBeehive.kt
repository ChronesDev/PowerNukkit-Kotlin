package cn.nukkit.blockentity

import cn.nukkit.Player

@PowerNukkitOnly
class BlockEntityBeehive @PowerNukkitOnly constructor(chunk: FullChunk?, nbt: CompoundTag) : BlockEntity(chunk, nbt) {
    private var occupants: List<Occupant>? = null
    @Override
    protected override fun initBlockEntity() {
        occupants = ArrayList(4)
        if (!this.namedTag.contains("ShouldSpawnBees")) {
            this.namedTag.putByte("ShouldSpawnBees", 0)
        }
        if (!this.namedTag.contains("Occupants")) {
            this.namedTag.putList(ListTag("Occupants"))
        } else {
            val occupantsTag: ListTag<CompoundTag> = namedTag.getList("Occupants", CompoundTag::class.java)
            for (i in 0 until occupantsTag.size()) {
                occupants.add(Occupant(occupantsTag.get(i)))
            }
        }

        // Backward compatibility
        if (this.namedTag.contains("HoneyLevel")) {
            var faceHorizontalIndex = 0
            val block: Block = getBlock()
            if (block is BlockBeehive) {
                faceHorizontalIndex = block.getDamage() and 3
                val honeyLevel: Int = this.namedTag.getByte("HoneyLevel")
                val beehive: BlockBeehive = block as BlockBeehive
                beehive.setBlockFace(BlockFace.fromHorizontalIndex(faceHorizontalIndex))
                beehive.setHoneyLevel(honeyLevel)
                beehive.getLevel().setBlock(beehive, beehive, true, true)
            }
            this.namedTag.remove("HoneyLevel")
        }
        if (!isEmpty) {
            scheduleUpdate()
        }
    }

    @Override
    override fun saveNBT() {
        super.saveNBT()
        val occupantsTag: ListTag<CompoundTag> = ListTag("Occupants")
        for (occupant in occupants!!) {
            occupantsTag.add(occupant.saveNBT())
        }
        this.namedTag.putList(occupantsTag)
    }

    @get:PowerNukkitOnly
    @set:PowerNukkitOnly
    var honeyLevel: Int
        get() {
            val block: Block = getBlock()
            return if (block is BlockBeehive) {
                (block as BlockBeehive).getHoneyLevel()
            } else {
                0
            }
        }
        set(honeyLevel) {
            val block: Block = getBlock()
            if (block is BlockBeehive) {
                (block as BlockBeehive).setHoneyLevel(honeyLevel)
                block.getLevel().setBlock(block, block, true, true)
            }
        }

    @PowerNukkitOnly
    fun addOccupant(occupant: Occupant): Boolean {
        occupants.add(occupant)
        val occupants: ListTag<CompoundTag> = this.namedTag.getList("Occupants", CompoundTag::class.java)
        occupants.add(occupant.saveNBT())
        this.namedTag.putList(occupants)
        scheduleUpdate()
        return true
    }

    @PowerNukkitOnly
    fun addOccupant(entity: Entity): Occupant? {
        return if (entity is EntityBee) {
            val bee: EntityBee = entity as EntityBee
            val hasNectar: Boolean = bee.getHasNectar()
            addOccupant(bee, if (hasNectar) 2400 else 600, hasNectar, true)
        } else {
            addOccupant(entity, 600, false, true)
        }
    }

    @PowerNukkitOnly
    fun addOccupant(entity: Entity, ticksLeftToStay: Int): Occupant? {
        return addOccupant(entity, ticksLeftToStay, false, true)
    }

    @PowerNukkitOnly
    fun addOccupant(entity: Entity, ticksLeftToStay: Int, hasNectar: Boolean): Occupant? {
        return addOccupant(entity, ticksLeftToStay, hasNectar, true)
    }

    @PowerNukkitOnly
    fun addOccupant(entity: Entity, ticksLeftToStay: Int, hasNectar: Boolean, playSound: Boolean): Occupant? {
        entity.saveNBT()
        val occupant = Occupant(ticksLeftToStay, entity.getSaveId(), entity.namedTag.clone())
        if (!addOccupant(occupant)) {
            return null
        }
        entity.close()
        if (playSound) {
            entity.level.addSound(this, Sound.BLOCK_BEEHIVE_ENTER)
            if (entity.level != null && (entity.level !== level || distanceSquared(this) >= 4)) {
                entity.level.addSound(entity, Sound.BLOCK_BEEHIVE_ENTER)
            }
        }
        return occupant
    }

    @PowerNukkitOnly
    fun getOccupants(): Array<Occupant> {
        return occupants.toArray(Occupant.EMPTY_ARRAY)
    }

    @PowerNukkitOnly
    fun removeOccupant(occupant: Occupant?): Boolean {
        return occupants.remove(occupant)
    }

    @get:PowerNukkitOnly
    val isHoneyEmpty: Boolean
        get() = honeyLevel == BlockBeehive.HONEY_LEVEL.getMinValue()

    @get:PowerNukkitOnly
    val isHoneyFull: Boolean
        get() = honeyLevel == BlockBeehive.HONEY_LEVEL.getMaxValue()

    @get:PowerNukkitOnly
    val isEmpty: Boolean
        get() = occupants!!.isEmpty()

    @get:PowerNukkitOnly
    val occupantsCount: Int
        get() = occupants!!.size()

    @PowerNukkitOnly
    fun isSpawnFaceValid(face: BlockFace?): Boolean {
        val side: Block = getSide(face).getLevelBlock()
        return side.canPassThrough() && side !is BlockLiquid
    }

    @PowerNukkitOnly
    fun scanValidSpawnFaces(): List<BlockFace> {
        return scanValidSpawnFaces(false)
    }

    @PowerNukkitOnly
    fun scanValidSpawnFaces(preferFront: Boolean): List<BlockFace> {
        if (preferFront) {
            val block: Block = getBlock()
            if (block is BlockBeehive) {
                val beehiveFace: BlockFace = (block as BlockBeehive).getBlockFace()
                if (isSpawnFaceValid(beehiveFace)) {
                    return Collections.singletonList(beehiveFace)
                }
            }
        }
        val validFaces: List<BlockFace> = ArrayList(4)
        for (faceIndex in 0..3) {
            val face: BlockFace = BlockFace.fromHorizontalIndex(faceIndex)
            if (isSpawnFaceValid(face)) {
                validFaces.add(face)
            }
        }
        return validFaces
    }

    @PowerNukkitOnly
    fun spawnOccupant(occupant: Occupant, validFaces: List<BlockFace>?): Entity? {
        if (validFaces != null && validFaces.isEmpty()) {
            return null
        }
        val saveData: CompoundTag = occupant.saveData.clone()
        val lookAt: Position
        val spawnPosition: Position
        if (validFaces != null) {
            val face: BlockFace = validFaces[RANDOM.nextInt(validFaces.size())]
            spawnPosition = add(
                    face.getXOffset() * 0.25 - face.getZOffset() * 0.5,
                    face.getYOffset() + if (face.getYOffset() < 0) -0.4 else 0.2,
                    face.getZOffset() * 0.25 - face.getXOffset() * 0.5
            )
            saveData.putList(ListTag<DoubleTag>("Pos")
                    .add(DoubleTag("0", spawnPosition.x))
                    .add(DoubleTag("1", spawnPosition.y))
                    .add(DoubleTag("2", spawnPosition.z))
            )
            saveData.putList(ListTag<DoubleTag>("Motion")
                    .add(DoubleTag("0", 0))
                    .add(DoubleTag("1", 0))
                    .add(DoubleTag("2", 0))
            )
            lookAt = getSide(face, 2)
        } else {
            spawnPosition = add(RANDOM.nextDouble(), 0.2, RANDOM.nextDouble())
            lookAt = spawnPosition.add(RANDOM.nextDouble(), 0, RANDOM.nextDouble())
        }
        val dx: Double = lookAt.getX() - spawnPosition.getX()
        val dz: Double = lookAt.getZ() - spawnPosition.getZ()
        var yaw = 0f
        if (dx != 0.0) {
            yaw = if (dx < 0) {
                (1.5 * Math.PI) as Float
            } else {
                (0.5 * Math.PI) as Float
            }
            yaw = yaw - Math.atan(dz / dx) as Float
        } else if (dz < 0) {
            yaw = Math.PI
        }
        yaw = -yaw * 180f / Math.PI as Float
        saveData.putList(ListTag<FloatTag>("Rotation")
                .add(FloatTag("0", yaw))
                .add(FloatTag("1", 0))
        )
        val entity: Entity = Entity.createEntity(occupant.actorIdentifier, spawnPosition.getChunk(), saveData)
        if (entity != null) {
            removeOccupant(occupant)
            level.addSound(this, Sound.BLOCK_BEEHIVE_EXIT)
        }
        val bee: EntityBee? = if (entity is EntityBee) entity as EntityBee else null
        if (occupant.hasNectar && occupant.ticksLeftToStay <= 0) {
            if (!isHoneyFull) {
                honeyLevel = honeyLevel + 1
            }
            if (bee != null) {
                bee.nectarDelivered(this)
            }
        } else {
            if (bee != null) {
                bee.leftBeehive(this)
            }
        }
        if (entity != null) {
            entity.spawnToAll()
        }
        return entity
    }

    @Override
    override fun onBreak() {
        if (!isEmpty) {
            for (occupant in getOccupants()) {
                val entity: Entity? = spawnOccupant(occupant, null)
                if (level == null || level.getBlock(down()).getId() !== BlockID.CAMPFIRE_BLOCK) {
                    if (entity is EntityBee) {
                        (entity as EntityBee?).setAngry(true)
                    } else {
                        // TODO attack nearest player
                    }
                }
            }
        }
    }

    @Override
    override fun onBreak(isSilkTouch: Boolean) {
        if (!isSilkTouch) {
            onBreak()
        }
    }

    @PowerNukkitOnly
    fun angerBees(player: Player?) {
        if (!isEmpty) {
            val validFaces: List<BlockFace> = scanValidSpawnFaces()
            if (isSpawnFaceValid(BlockFace.UP)) {
                validFaces.add(BlockFace.UP)
            }
            if (isSpawnFaceValid(BlockFace.DOWN)) {
                validFaces.add(BlockFace.DOWN)
            }
            for (occupant in getOccupants()) {
                val entity: Entity? = spawnOccupant(occupant, validFaces)
                if (entity is EntityBee) {
                    val bee: EntityBee? = entity as EntityBee?
                    if (player != null) {
                        bee.setAngry(player)
                    } else {
                        bee.setAngry(true)
                    }
                } else {
                    // TODO attack player
                }
            }
        }
    }

    @Override
    override fun onUpdate(): Boolean {
        if (this.closed || isEmpty) {
            return false
        }
        this.timing.startTiming()
        var validSpawnFaces: List<BlockFace>? = null

        // getOccupants will avoid ConcurrentModificationException if plugins changes the contents while iterating
        for (occupant in getOccupants()) {
            if (--occupant.ticksLeftToStay <= 0) {
                if (validSpawnFaces == null) {
                    validSpawnFaces = scanValidSpawnFaces(true)
                }
                if (spawnOccupant(occupant, validSpawnFaces) == null) {
                    occupant.ticksLeftToStay = 600
                }
            } else if (!occupant.isMuted && RANDOM.nextDouble() < 0.005) {
                level.addSound(add(0.5, 0, 0.5), occupant.workSound, 1f, occupant.workSoundPitch)
            }
        }
        this.timing.stopTiming()
        return true
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() {
            val id: Int = this.getBlock().getId()
            return id == Block.BEEHIVE || id == Block.BEE_NEST
        }

    @PowerNukkitOnly
    class Occupant : Cloneable {
        @get:PowerNukkitOnly
        @set:PowerNukkitOnly
        var ticksLeftToStay: Int

        @get:PowerNukkitOnly
        @set:PowerNukkitOnly
        var actorIdentifier: String
        var saveData: CompoundTag
        var workSound: Sound = Sound.BLOCK_BEEHIVE_WORK

        @get:PowerNukkitOnly
        @set:PowerNukkitOnly
        var workSoundPitch = 1f

        @get:PowerNukkitOnly
        @set:PowerNukkitOnly
        var hasNectar = false

        @get:PowerNukkitOnly
        @set:PowerNukkitOnly
        var isMuted = false

        @PowerNukkitOnly
        constructor(ticksLeftToStay: Int, actorIdentifier: String, saveData: CompoundTag) {
            this.ticksLeftToStay = ticksLeftToStay
            this.actorIdentifier = actorIdentifier
            this.saveData = saveData
        }

        @PowerNukkitOnly
        constructor(saved: CompoundTag) {
            ticksLeftToStay = saved.getInt("TicksLeftToStay")
            actorIdentifier = saved.getString("ActorIdentifier")
            saveData = saved.getCompound("SaveData").clone()
            if (saved.contains("WorkSound")) {
                try {
                    workSound = Sound.valueOf(saved.getString("WorkSound"))
                } catch (ignored: IllegalArgumentException) {
                }
            }
            if (saved.contains("WorkSoundPitch")) {
                workSoundPitch = saved.getFloat("WorkSoundPitch")
            }
            hasNectar = saved.getBoolean("HasNectar")
            isMuted = saved.getBoolean("Muted")
        }

        @PowerNukkitOnly
        fun saveNBT(): CompoundTag {
            val compoundTag = CompoundTag()
            compoundTag.putString("ActorIdentifier", actorIdentifier)
                    .putInt("TicksLeftToStay", ticksLeftToStay)
                    .putCompound("SaveData", saveData)
                    .putString("WorkSound", workSound.name())
                    .putFloat("WorkSoundPitch", workSoundPitch)
                    .putBoolean("HasNectar", hasNectar)
                    .putBoolean("Muted", isMuted)
            return compoundTag
        }

        @PowerNukkitOnly
        fun getSaveData(): CompoundTag {
            return saveData.clone()
        }

        @PowerNukkitOnly
        fun setSaveData(saveData: CompoundTag) {
            this.saveData = saveData.clone()
        }

        @PowerNukkitOnly
        fun getWorkSound(): Sound {
            return workSound
        }

        @PowerNukkitOnly
        fun setWorkSound(workSound: Sound) {
            this.workSound = workSound
        }

        @Override
        override fun toString(): String {
            return "Occupant{" +
                    "ticksLeftToStay=" + ticksLeftToStay +
                    ", actorIdentifier='" + actorIdentifier + '\'' +
                    '}'
        }

        @Override
        override fun equals(o: Object?): Boolean {
            if (this === o) return true
            if (o == null || getClass() !== o.getClass()) return false
            val occupant = o as Occupant
            return ticksLeftToStay == occupant.ticksLeftToStay &&
                    Objects.equals(actorIdentifier, occupant.actorIdentifier) &&
                    Objects.equals(saveData, occupant.saveData)
        }

        @Override
        override fun hashCode(): Int {
            return Objects.hash(ticksLeftToStay, actorIdentifier, saveData)
        }

        @Override
        protected fun clone(): Occupant {
            return try {
                val occupant = super.clone() as Occupant
                occupant.saveData = saveData.clone()
                occupant
            } catch (e: CloneNotSupportedException) {
                throw InternalError("Unexpected exception", e)
            }
        }

        companion object {
            @PowerNukkitOnly
            @Since("1.4.0.0-PN")
            val EMPTY_ARRAY = arrayOfNulls<Occupant>(0)
        }
    }

    companion object {
        private val RANDOM: Random = Random()
    }
}