package cn.nukkit.level.particle

import cn.nukkit.entity.Entity

/**
 * @author xtypr
 * @since 2015/11/21
 */
class FloatingTextParticle private constructor(level: Level?, pos: Vector3, title: String, text: String) : Particle(pos.x, pos.y, pos.z) {
    companion object {
        private val EMPTY_SKIN: Skin = Skin()
        private val SKIN_DATA: SerializedImage = SerializedImage.fromLegacy(ByteArray(8192))

        init {
            EMPTY_SKIN.setSkinData(SKIN_DATA)
            EMPTY_SKIN.generateSkinId("FloatingText")
        }
    }

    protected var uuid: UUID = UUID.randomUUID()
    protected val level: Level?
    var entityId: Long = -1
        protected set
    var isInvisible = false
    protected var metadata: EntityMetadata = EntityMetadata()

    constructor(location: Location?, title: String?) : this(location, title, null) {}
    constructor(location: Location, title: String, text: String) : this(location.getLevel(), location, title, text) {}
    constructor(pos: Vector3?, title: String?) : this(pos, title, null) {}
    constructor(pos: Vector3, title: String, text: String) : this(null, pos, title, text) {}

    var text: String?
        get() = metadata.getString(Entity.DATA_SCORE_TAG)
        set(text) {
            metadata.putString(Entity.DATA_SCORE_TAG, text)
            sendMetadata()
        }
    var title: String?
        get() = metadata.getString(Entity.DATA_NAMETAG)
        set(title) {
            metadata.putString(Entity.DATA_NAMETAG, title)
            sendMetadata()
        }

    private fun sendMetadata() {
        if (level != null) {
            val packet = SetEntityDataPacket()
            packet.eid = entityId
            packet.metadata = metadata
            level.addChunkPacket(getChunkX(), getChunkZ(), packet)
        }
    }

    fun setInvisible() {
        isInvisible = true
    }

    @Override
    override fun encode(): Array<DataPacket> {
        val packets: ArrayList<DataPacket> = ArrayList()
        if (entityId == -1L) {
            entityId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL)
        } else {
            val pk = RemoveEntityPacket()
            pk.eid = entityId
            packets.add(pk)
        }
        if (!isInvisible) {
            val entry: Array<PlayerListPacket.Entry> = arrayOf<PlayerListPacket.Entry>(Entry(uuid, entityId,
                    metadata.getString(Entity.DATA_NAMETAG), EMPTY_SKIN))
            val playerAdd = PlayerListPacket()
            playerAdd.entries = entry
            playerAdd.type = PlayerListPacket.TYPE_ADD
            packets.add(playerAdd)
            val pk = AddPlayerPacket()
            pk.uuid = uuid
            pk.username = ""
            pk.entityUniqueId = entityId
            pk.entityRuntimeId = entityId
            pk.x = this.x as Float
            pk.y = (this.y - 0.75)
            pk.z = this.z as Float
            pk.speedX = 0
            pk.speedY = 0
            pk.speedZ = 0
            pk.yaw = 0
            pk.pitch = 0
            pk.metadata = metadata
            pk.item = Item.get(Item.AIR)
            packets.add(pk)
            val playerRemove = PlayerListPacket()
            playerRemove.entries = entry
            playerRemove.type = PlayerListPacket.TYPE_REMOVE
            packets.add(playerRemove)
        }
        return packets.toArray(DataPacket.EMPTY_ARRAY)
    }

    init {
        this.level = level
        val flags = 1L shl Entity.DATA_FLAG_NO_AI
        metadata.putLong(Entity.DATA_FLAGS, flags)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putFloat(Entity.DATA_SCALE, 0.01f) //zero causes problems on debug builds?
                .putFloat(Entity.DATA_BOUNDING_BOX_HEIGHT, 0.01f)
                .putFloat(Entity.DATA_BOUNDING_BOX_WIDTH, 0.01f)
        if (!Strings.isNullOrEmpty(title)) {
            metadata.putString(Entity.DATA_NAMETAG, title)
        }
        if (!Strings.isNullOrEmpty(text)) {
            metadata.putString(Entity.DATA_SCORE_TAG, text)
        }
    }
}