package cn.nukkit.blockentity

import cn.nukkit.Player

/**
 * @author CreeperFace (Nukkit Project)
 */
class BlockEntityCauldron(chunk: FullChunk?, nbt: CompoundTag) : BlockEntitySpawnable(chunk, nbt) {
    @Override
    protected override fun initBlockEntity() {
        val potionId: Int
        if (!namedTag.contains("PotionId")) {
            namedTag.putShort("PotionId", 0xffff)
        }
        potionId = namedTag.getShort("PotionId")
        var potionType = if (potionId and 0xFFFF == 0xFFFF) POTION_TYPE_EMPTY else POTION_TYPE_NORMAL
        if (namedTag.getBoolean("SplashPotion")) {
            potionType = POTION_TYPE_SPLASH
            namedTag.remove("SplashPotion")
        }
        if (!namedTag.contains("PotionType")) {
            namedTag.putShort("PotionType", potionType)
        }
        super.initBlockEntity()
    }

    var potionId: Int
        get() = namedTag.getShort("PotionId")
        set(potionId) {
            namedTag.putShort("PotionId", potionId)
            spawnToAll()
        }

    fun hasPotion(): Boolean {
        return potionId and 0xffff != 0xffff
    }

    var potionType: Int
        get() = ((this.namedTag.getShort("PotionType") and 0xFFFF) as Short).toInt()
        set(potionType) {
            this.namedTag.putShort("PotionType", (potionType and 0xFFFF).toShort())
        }

    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    @set:Since("1.4.0.0-PN")
    @set:PowerNukkitOnly
    var type: PotionType
        get() = PotionType.getByTypeData(potionType)
        set(type) {
            potionType = type.potionTypeData
        }

    @set:Deprecated("Use {@link #setPotionType(int)} instead.")
    @set:Deprecated
    var isSplashPotion: Boolean
        get() = namedTag.getShort("PotionType") === POTION_TYPE_SPLASH
        set(value) {
            namedTag.putShort("PotionType", if (value) 1 else 0)
        }

    var customColor: BlockColor?
        get() {
            if (isCustomColor()) {
                val color: Int = namedTag.getInt("CustomColor")
                val red = color shr 16 and 0xff
                val green = color shr 8 and 0xff
                val blue = color and 0xff
                return BlockColor(red, green, blue)
            }
            return null
        }
        set(color) {
            setCustomColor(color.getRed(), color.getGreen(), color.getBlue())
        }

    fun isCustomColor(): Boolean {
        return namedTag.contains("CustomColor")
    }

    fun setCustomColor(r: Int, g: Int, b: Int) {
        val color = r shl 16 or (g shl 8) or b and 0xffffff
        namedTag.putInt("CustomColor", color)
        spawnToAll()
    }

    fun clearCustomColor() {
        namedTag.remove("CustomColor")
        spawnToAll()
    }

    @Override
    override fun spawnToAll() {
        val block: BlockCauldron = getBlock() as BlockCauldron
        val viewers: Array<Player> = this.level.getChunkPlayers(getChunkX(), getChunkZ()).values().toArray(Player.EMPTY_ARRAY)
        this.level.sendBlocks(viewers, arrayOf<Vector3>(block))
        super.spawnToAll()
        val location: Location = getLocation()
        Server.getInstance().getScheduler().scheduleTask(null) {
            if (isValid()) {
                val cauldron: BlockEntity = this.level.getBlockEntity(location)
                if (cauldron === this@BlockEntityCauldron) {
                    this.level.sendBlocks(viewers, arrayOf<Vector3>(location))
                    super.spawnToAll()
                }
            }
        }
    }

    @get:Override
    override val isBlockEntityValid: Boolean
        get() {
            val id: Int = getBlock().getId()
            return id == Block.CAULDRON_BLOCK || id == Block.LAVA_CAULDRON
        }

    @get:Override
    override val spawnCompound: CompoundTag
        get() {
            val compoundTag: CompoundTag = CompoundTag()
                    .putString("id", BlockEntity.CAULDRON)
                    .putInt("x", this.x as Int)
                    .putInt("y", this.y as Int)
                    .putInt("z", this.z as Int)
                    .putBoolean("isMovable", isMovable())
                    .putList(ListTag("Items"))
                    .putShort("PotionId", namedTag.getShort("PotionId") as Short)
                    .putShort("PotionType", namedTag.getShort("PotionType") as Short)
            if (namedTag.contains("CustomColor")) {
                compoundTag.putInt("CustomColor", namedTag.getInt("CustomColor") shl 8 shr 8)
            }
            return compoundTag
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @RequiredArgsConstructor
    enum class PotionType {
        EMPTY(POTION_TYPE_EMPTY), NORMAL(POTION_TYPE_NORMAL), SPLASH(POTION_TYPE_SPLASH), LINGERING(POTION_TYPE_LINGERING), LAVA(POTION_TYPE_LAVA), UNKNOWN(-2);

        val potionTypeData = 0

        companion object {
            private val BY_DATA: Int2ObjectMap<PotionType>? = null
            @PowerNukkitOnly
            @Since("1.4.0.0-PN")
            @Nonnull
            fun getByTypeData(typeData: Int): PotionType {
                return BY_DATA.getOrDefault(typeData, UNKNOWN)
            }

            init {
                val types = values()
                BY_DATA = Int2ObjectOpenHashMap(cn.nukkit.blockentity.types.size)
                for (type in cn.nukkit.blockentity.types) {
                    BY_DATA.put(cn.nukkit.blockentity.type.potionTypeData, cn.nukkit.blockentity.type)
                }
            }
        }
    }

    companion object {
        @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Using -1 instead of the overflown 0xFFFF")
        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Magic value", replaceWith = "PotionType")
        val POTION_TYPE_EMPTY = -1

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Magic value", replaceWith = "PotionType")
        val POTION_TYPE_NORMAL = 0

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Magic value", replaceWith = "PotionType")
        val POTION_TYPE_SPLASH = 1

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Magic value", replaceWith = "PotionType")
        val POTION_TYPE_LINGERING = 2

        @Deprecated
        @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "Magic value", replaceWith = "PotionType")
        val POTION_TYPE_LAVA = 0xF19B
    }
}