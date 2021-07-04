package cn.nukkit.entity.item

import cn.nukkit.Player

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityPainting(chunk: FullChunk?, nbt: CompoundTag?) : EntityHanging(chunk, nbt) {
    private var motive: Motive? = null

    @get:Override
    var width = 0f
        private set

    @get:Override
    var length = 0f
        private set

    @get:Override
    var height = 0f
        private set

    @Override
    protected fun initEntity() {
        motive = getMotive(this.namedTag.getString("Motive"))
        if (motive != null) {
            val face: BlockFace = getHorizontalFacing()
            val size: Vector3 = Vector3(motive!!.width, motive!!.height, motive!!.width).multiply(0.5)
            if (face.getAxis() === Axis.Z) {
                size.z = 0.5
            } else {
                size.x = 0.5
            }
            width = size.x
            length = size.z
            height = size.y
            this.boundingBox = SimpleAxisAlignedBB(
                    this.x - size.x,
                    this.y - size.y,
                    this.z - size.z,
                    this.x + size.x,
                    this.y + size.y,
                    this.z + size.z
            )
        } else {
            width = 0f
            height = 0f
            length = 0f
        }
        super.initEntity()
    }

    @Override
    fun createAddEntityPacket(): DataPacket {
        val addPainting = AddPaintingPacket()
        addPainting.entityUniqueId = this.getId()
        addPainting.entityRuntimeId = this.getId()
        addPainting.x = this.x as Float
        addPainting.y = this.y as Float
        addPainting.z = this.z as Float
        addPainting.direction = this.getDirection().getHorizontalIndex()
        addPainting.title = this.namedTag.getString("Motive")
        return addPainting
    }

    @Override
    fun attack(source: EntityDamageEvent): Boolean {
        return if (super.attack(source)) {
            if (source is EntityDamageByEntityEvent) {
                val damager: Entity = (source as EntityDamageByEntityEvent).getDamager()
                if (damager is Player && ((damager as Player).isAdventure() || (damager as Player).isSurvival()) && this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
                    this.level.dropItem(this, ItemPainting())
                }
            }
            this.close()
            true
        } else {
            false
        }
    }

    @Override
    fun saveNBT() {
        super.saveNBT()
        this.namedTag.putString("Motive", motive!!.title)
    }

    @Override
    fun onPushByPiston(piston: BlockEntityPistonArm?) {
        if (this.level.getGameRules().getBoolean(GameRule.DO_ENTITY_DROPS)) {
            this.level.dropItem(this, ItemPainting())
        }
        this.close()
    }

    val art: Motive?
        get() = getMotive()

    fun getMotive(): Motive? {
        return Motive.BY_NAME[namedTag.getString("Motive")]
    }

    enum class Motive(val title: String, val width: Int, val height: Int) {
        KEBAB("Kebab", 1, 1), AZTEC("Aztec", 1, 1), ALBAN("Alban", 1, 1), AZTEC2("Aztec2", 1, 1), BOMB("Bomb", 1, 1), PLANT("Plant", 1, 1), WASTELAND("Wasteland", 1, 1), WANDERER("Wanderer", 1, 2), GRAHAM("Graham", 1, 2), POOL("Pool", 2, 1), COURBET("Courbet", 2, 1), SUNSET("Sunset", 2, 1), SEA("Sea", 2, 1), CREEBET("Creebet", 2, 1), MATCH("Match", 2, 2), BUST("Bust", 2, 2), STAGE("Stage", 2, 2), VOID("Void", 2, 2), SKULL_AND_ROSES("SkullAndRoses", 2, 2), WITHER("Wither", 2, 2), FIGHTERS("Fighters", 4, 2), SKELETON("Skeleton", 4, 3), DONKEY_KONG("DonkeyKong", 4, 3), POINTER("Pointer", 4, 4), PIG_SCENE("Pigscene", 4, 4), FLAMING_SKULL("Flaming Skull", 4, 4);

        companion object {
            val BY_NAME: Map<String, Motive> = HashMap()

            init {
                for (motive in values()) {
                    BY_NAME.put(cn.nukkit.entity.item.motive.title, cn.nukkit.entity.item.motive)
                }
            }
        }
    }

    companion object {
        @get:Override
        val networkId = 83
            get() = Companion.field
        val motives = Motive.values()
        fun getMotive(name: String?): Motive {
            return Motive.BY_NAME.getOrDefault(name, Motive.KEBAB)
        }
    }
}