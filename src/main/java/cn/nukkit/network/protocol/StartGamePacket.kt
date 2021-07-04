package cn.nukkit.network.protocol

import cn.nukkit.api.Since

/**
 * @since 15-10-13
 */
@Log4j2
@ToString
class StartGamePacket : DataPacket() {
    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    var entityUniqueId: Long = 0
    var entityRuntimeId: Long = 0
    var playerGamemode = 0
    var x = 0f
    var y = 0f
    var z = 0f
    var yaw = 0f
    var pitch = 0f
    var seed = 0
    var dimension: Byte = 0
    var generator = 1
    var worldGamemode = 0
    var difficulty = 0
    var spawnX = 0
    var spawnY = 0
    var spawnZ = 0
    var hasAchievementsDisabled = true
    var dayCycleStopTime = -1 //-1 = not stopped, any positive value = stopped at that time
    var eduEditionOffer = 0
    var hasEduFeaturesEnabled = false
    var rainLevel = 0f
    var lightningLevel = 0f
    var hasConfirmedPlatformLockedContent = false
    var multiplayerGame = true
    var broadcastToLAN = true
    var xblBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC
    var platformBroadcastIntent = GAME_PUBLISH_SETTING_PUBLIC
    var commandsEnabled = false
    var isTexturePacksRequired = false
    var gameRules: GameRules? = null
    var bonusChest = false
    var hasStartWithMapEnabled = false

    @Since("1.3.0.0-PN")
    var trustingPlayers = false
    var permissionLevel = 1
    var serverChunkTickRange = 4
    var hasLockedBehaviorPack = false
    var hasLockedResourcePack = false
    var isFromLockedWorldTemplate = false
    var isUsingMsaGamertagsOnly = false
    var isFromWorldTemplate = false
    var isWorldTemplateOptionLocked = false
    var isOnlySpawningV1Villagers = false
    var vanillaVersion: String = ProtocolInfo.MINECRAFT_VERSION_NETWORK
    var levelId = "" //base64 string, usually the same as world folder name in vanilla
    var worldName: String? = null
    var premiumWorldTemplateId = ""
    var isTrial = false
    var isMovementServerAuthoritative = false

    @Since("1.3.0.0-PN")
    var isInventoryServerAuthoritative = false
    var currentTick: Long = 0
    var enchantmentSeed = 0
    var multiplayerCorrelationId = ""

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putEntityUniqueId(entityUniqueId)
        this.putEntityRuntimeId(entityRuntimeId)
        this.putVarInt(playerGamemode)
        this.putVector3f(x, y, z)
        this.putLFloat(yaw)
        this.putLFloat(pitch)
        this.putVarInt(seed)
        this.putLShort(0x00) // SpawnBiomeType - Default
        this.putString("plains") // UserDefinedBiomeName
        this.putVarInt(dimension)
        this.putVarInt(generator)
        this.putVarInt(worldGamemode)
        this.putVarInt(difficulty)
        this.putBlockVector3(spawnX, spawnY, spawnZ)
        this.putBoolean(hasAchievementsDisabled)
        this.putVarInt(dayCycleStopTime)
        this.putVarInt(eduEditionOffer)
        this.putBoolean(hasEduFeaturesEnabled)
        this.putString("") // Education Edition Product ID
        this.putLFloat(rainLevel)
        this.putLFloat(lightningLevel)
        this.putBoolean(hasConfirmedPlatformLockedContent)
        this.putBoolean(multiplayerGame)
        this.putBoolean(broadcastToLAN)
        this.putVarInt(xblBroadcastIntent)
        this.putVarInt(platformBroadcastIntent)
        this.putBoolean(commandsEnabled)
        this.putBoolean(isTexturePacksRequired)
        this.putGameRules(gameRules)
        this.putLInt(0) // Experiment count
        this.putBoolean(false) // Were experiments previously toggled
        this.putBoolean(bonusChest)
        this.putBoolean(hasStartWithMapEnabled)
        this.putVarInt(permissionLevel)
        this.putLInt(serverChunkTickRange)
        this.putBoolean(hasLockedBehaviorPack)
        this.putBoolean(hasLockedResourcePack)
        this.putBoolean(isFromLockedWorldTemplate)
        this.putBoolean(isUsingMsaGamertagsOnly)
        this.putBoolean(isFromWorldTemplate)
        this.putBoolean(isWorldTemplateOptionLocked)
        this.putBoolean(isOnlySpawningV1Villagers)
        this.putString(vanillaVersion)
        this.putLInt(16) // Limited world width
        this.putLInt(16) // Limited world height
        this.putBoolean(false) // Nether type
        this.putBoolean(false) // Experimental Gameplay
        this.putString(levelId)
        this.putString(worldName)
        this.putString(premiumWorldTemplateId)
        this.putBoolean(isTrial)
        this.putUnsignedVarInt(if (isMovementServerAuthoritative) 1 else 0) // 2 - rewind
        this.putVarInt(0) // RewindHistorySize
        this.putBoolean(false) // isServerAuthoritativeBlockBreaking
        this.putLLong(currentTick)
        this.putVarInt(enchantmentSeed)
        this.putUnsignedVarInt(0) // Custom blocks
        this.put(RuntimeItems.getRuntimeMapping().getItemDataPalette())
        this.putString(multiplayerCorrelationId)
        this.putBoolean(isInventoryServerAuthoritative)
        this.putString("") // Server Engine
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.START_GAME_PACKET
        const val GAME_PUBLISH_SETTING_NO_MULTI_PLAY = 0
        const val GAME_PUBLISH_SETTING_INVITE_ONLY = 1
        const val GAME_PUBLISH_SETTING_FRIENDS_ONLY = 2
        const val GAME_PUBLISH_SETTING_FRIENDS_OF_FRIENDS = 3
        const val GAME_PUBLISH_SETTING_PUBLIC = 4
    }
}