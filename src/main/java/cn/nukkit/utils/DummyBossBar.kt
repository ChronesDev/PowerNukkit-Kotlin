package cn.nukkit.utils

import cn.nukkit.Player

/**
 * @author boybook (Nukkit Project)
 */
class DummyBossBar private constructor(builder: Builder) {
    private val player: Player
    val bossBarId: Long
    private var text: String
    private var length: Float
    private var color: BlockColor?

    class Builder(player: Player) {
        val player: Player
        val bossBarId: Long
        var text = ""
        var length = 100f
        var color: BlockColor? = null
        fun text(text: String): Builder {
            this.text = text
            return this
        }

        fun length(length: Float): Builder {
            if (length >= 0 && length <= 100) this.length = length
            return this
        }

        fun color(color: BlockColor?): Builder {
            this.color = color
            return this
        }

        fun color(red: Int, green: Int, blue: Int): Builder {
            return color(BlockColor(red, green, blue))
        }

        fun build(): DummyBossBar {
            return DummyBossBar(this)
        }

        init {
            this.player = player
            bossBarId = 1095216660480L + ThreadLocalRandom.current().nextLong(0, 0x7fffffffL)
        }
    }

    fun getPlayer(): Player {
        return player
    }

    fun getText(): String {
        return text
    }

    fun setText(text: String) {
        if (!this.text.equals(text)) {
            this.text = text
            updateBossEntityNameTag()
            sendSetBossBarTitle()
        }
    }

    fun getLength(): Float {
        return length
    }

    fun setLength(length: Float) {
        if (this.length != length) {
            this.length = length
            sendAttributes()
            sendSetBossBarLength()
        }
    }

    /**
     * Color is not working in the current version. We are keep waiting for client support.
     * @param color the boss bar color
     */
    fun setColor(color: BlockColor) {
        if (this.color == null || !this.color.equals(color)) {
            this.color = color
            sendSetBossBarTexture()
        }
    }

    fun setColor(red: Int, green: Int, blue: Int) {
        this.setColor(BlockColor(red, green, blue))
    }

    //(this.color.getRed() << 16 | this.color.getGreen() << 8 | this.color.getBlue()) & 0xffffff;
    val mixedColor: Int
        get() = color.getRGB() //(this.color.getRed() << 16 | this.color.getGreen() << 8 | this.color.getBlue()) & 0xffffff;

    fun getColor(): BlockColor? {
        return color
    }

    private fun createBossEntity() {
        val pkAdd = AddEntityPacket()
        pkAdd.type = EntityCreeper.NETWORK_ID
        pkAdd.entityUniqueId = bossBarId
        pkAdd.entityRuntimeId = bossBarId
        pkAdd.x = player.x as Float
        pkAdd.y = (-10).toFloat() // Below the bedrock
        pkAdd.z = player.z as Float
        pkAdd.speedX = 0
        pkAdd.speedY = 0
        pkAdd.speedZ = 0
        pkAdd.metadata = EntityMetadata() // Default Metadata tags
                .putLong(Entity.DATA_FLAGS, 0)
                .putShort(Entity.DATA_AIR, 400)
                .putShort(Entity.DATA_MAX_AIR, 400)
                .putLong(Entity.DATA_LEAD_HOLDER_EID, -1)
                .putString(Entity.DATA_NAMETAG, text) // Set the entity name
                .putFloat(Entity.DATA_SCALE, 0) // And make it invisible
        player.dataPacket(pkAdd)
    }

    private fun sendAttributes() {
        val pkAttributes = UpdateAttributesPacket()
        pkAttributes.entityId = bossBarId
        val attr: Attribute = Attribute.getAttribute(Attribute.MAX_HEALTH)
        attr.setMaxValue(100) // Max value - We need to change the max value first, or else the "setValue" will return a IllegalArgumentException
        attr.setValue(length) // Entity health
        pkAttributes.entries = arrayOf<Attribute>(attr)
        player.dataPacket(pkAttributes)
    }

    private fun sendShowBossBar() {
        val pkBoss = BossEventPacket()
        pkBoss.bossEid = bossBarId
        pkBoss.type = BossEventPacket.TYPE_SHOW
        pkBoss.title = text
        pkBoss.healthPercent = length / 100
        player.dataPacket(pkBoss)
    }

    private fun sendHideBossBar() {
        val pkBoss = BossEventPacket()
        pkBoss.bossEid = bossBarId
        pkBoss.type = BossEventPacket.TYPE_HIDE
        player.dataPacket(pkBoss)
    }

    private fun sendSetBossBarTexture() {
        val pk = BossEventPacket()
        pk.bossEid = bossBarId
        pk.type = BossEventPacket.TYPE_TEXTURE
        pk.color = mixedColor
        player.dataPacket(pk)
    }

    private fun sendSetBossBarTitle() {
        val pkBoss = BossEventPacket()
        pkBoss.bossEid = bossBarId
        pkBoss.type = BossEventPacket.TYPE_TITLE
        pkBoss.title = text
        pkBoss.healthPercent = length / 100
        player.dataPacket(pkBoss)
    }

    private fun sendSetBossBarLength() {
        val pkBoss = BossEventPacket()
        pkBoss.bossEid = bossBarId
        pkBoss.type = BossEventPacket.TYPE_HEALTH_PERCENT
        pkBoss.healthPercent = length / 100
        player.dataPacket(pkBoss)
    }

    /**
     * Don't let the entity go too far from the player, or the BossBar will disappear.
     * Update boss entity's position when teleport and each 5s.
     */
    fun updateBossEntityPosition() {
        val pk = MoveEntityAbsolutePacket()
        pk.eid = bossBarId
        pk.x = player.x
        pk.y = -10
        pk.z = player.z
        pk.headYaw = 0
        pk.yaw = 0
        pk.pitch = 0
        player.dataPacket(pk)
    }

    private fun updateBossEntityNameTag() {
        val pk = SetEntityDataPacket()
        pk.eid = bossBarId
        pk.metadata = EntityMetadata().putString(Entity.DATA_NAMETAG, text)
        player.dataPacket(pk)
    }

    private fun removeBossEntity() {
        val pkRemove = RemoveEntityPacket()
        pkRemove.eid = bossBarId
        player.dataPacket(pkRemove)
    }

    fun create() {
        createBossEntity()
        sendAttributes()
        sendShowBossBar()
        sendSetBossBarLength()
        if (color != null) sendSetBossBarTexture()
    }

    /**
     * Once the player has teleported, resend Show BossBar
     */
    fun reshow() {
        updateBossEntityPosition()
        sendShowBossBar()
        sendSetBossBarLength()
    }

    fun destroy() {
        sendHideBossBar()
        removeBossEntity()
    }

    init {
        player = builder.player
        bossBarId = builder.bossBarId
        text = builder.text
        length = builder.length
        color = builder.color
    }
}