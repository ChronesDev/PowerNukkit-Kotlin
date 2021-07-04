package cn.nukkit.utils

import cn.nukkit.api.Since

/**
 * @author CreeperFace
 */
interface LoginChainData {
    val username: String?
    val clientUUID: UUID?
    val identityPublicKey: String?
    val clientId: Long
    val serverAddress: String?
    val deviceModel: String?
    val deviceOS: Int
    val deviceId: String?
    val gameVersion: String?
    val guiScale: Int
    val languageCode: String?
    val xUID: String?
    val isXboxAuthed: Boolean
    val currentInputMode: Int
    val defaultInputMode: Int
    val capeData: String?
    val uIProfile: Int

    @get:Since("1.2.1.0-PN")
    val rawData: JsonObject?
}