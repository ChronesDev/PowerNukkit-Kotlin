package cn.nukkit.utils

import cn.nukkit.network.protocol.LoginPacket

/**
 * ClientChainData is a container of chain data sent from clients.
 *
 *
 * Device information such as client UUID, xuid and serverAddress, can be
 * read from instances of this object.
 *
 *
 * To get chain data, you can use player.getLoginChainData() or read(loginPacket)
 *
 *
 * ===============
 * @author boybook (Nukkit Project)
 * ===============
 */
class ClientChainData private constructor(buffer: ByteArray) : LoginChainData {
    companion object {
        private const val MOJANG_PUBLIC_KEY_BASE64 = "MHYwEAYHKoZIzj0CAQYFK4EEACIDYgAE8ELkixyLcwlZryUQcu1TvPOmI2B7vX83ndnWRUaXm74wFfa5f/lwQNTfrLVHa2PmenpGI6JhIMUJaWZrjmMj90NoKNFSNBuKdm8rYiXsfaz3K36x/1U26HpG0ZxK/V1V"
        private val MOJANG_PUBLIC_KEY: PublicKey? = null
        fun of(buffer: ByteArray): ClientChainData {
            return ClientChainData(buffer)
        }

        fun read(pk: LoginPacket): ClientChainData {
            return of(pk.getBuffer())
        }

        const val UI_PROFILE_CLASSIC = 0
        const val UI_PROFILE_POCKET = 1
        @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
        private fun generateKey(base64: String): PublicKey {
            return KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(base64)))
        }

        init {
            try {
                MOJANG_PUBLIC_KEY = generateKey(MOJANG_PUBLIC_KEY_BASE64)
            } catch (e: InvalidKeySpecException) {
                throw AssertionError(e)
            } catch (e: NoSuchAlgorithmException) {
                throw AssertionError(e)
            }
        }
    }

    @Override
    fun getClientUUID(): UUID? {
        return clientUUID
    }

    @get:Override
    override var isXboxAuthed = false
        private set

    @Override
    fun getRawData(): JsonObject? {
        return rawData
    }

    ///////////////////////////////////////////////////////////////////////////
    // Override
    ///////////////////////////////////////////////////////////////////////////
    @Override
    override fun equals(obj: Object): Boolean {
        return obj is ClientChainData && Objects.equals(bs, (obj as ClientChainData).bs)
    }

    @Override
    override fun hashCode(): Int {
        return bs.hashCode()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal
    ///////////////////////////////////////////////////////////////////////////
    @get:Override
    override var username: String? = null
        private set
    private override var clientUUID: UUID? = null

    @get:Override
    override var xUID: String? = null
        private set

    @get:Override
    override var identityPublicKey: String? = null
        private set

    @get:Override
    override var clientId: Long = 0
        private set

    @get:Override
    override var serverAddress: String? = null
        private set

    @get:Override
    override var deviceModel: String? = null
        private set

    @get:Override
    override var deviceOS = 0
        private set

    @get:Override
    override var deviceId: String? = null
        private set

    @get:Override
    override var gameVersion: String? = null
        private set

    @get:Override
    override var guiScale = 0
        private set

    @get:Override
    override var languageCode: String? = null
        private set

    @get:Override
    override var currentInputMode = 0
        private set

    @get:Override
    override var defaultInputMode = 0
        private set

    @get:Override
    override var uIProfile = 0
        private set

    @get:Override
    override var capeData: String? = null
        private set
    private override var rawData: JsonObject? = null
    private val bs: BinaryStream = BinaryStream()
    private fun decodeSkinData() {
        val skinToken: JsonObject = decodeToken(String(bs.get(bs.getLInt()))) ?: return
        if (skinToken.has("ClientRandomId")) clientId = skinToken.get("ClientRandomId").getAsLong()
        if (skinToken.has("ServerAddress")) serverAddress = skinToken.get("ServerAddress").getAsString()
        if (skinToken.has("DeviceModel")) deviceModel = skinToken.get("DeviceModel").getAsString()
        if (skinToken.has("DeviceOS")) deviceOS = skinToken.get("DeviceOS").getAsInt()
        if (skinToken.has("DeviceId")) deviceId = skinToken.get("DeviceId").getAsString()
        if (skinToken.has("GameVersion")) gameVersion = skinToken.get("GameVersion").getAsString()
        if (skinToken.has("GuiScale")) guiScale = skinToken.get("GuiScale").getAsInt()
        if (skinToken.has("LanguageCode")) languageCode = skinToken.get("LanguageCode").getAsString()
        if (skinToken.has("CurrentInputMode")) currentInputMode = skinToken.get("CurrentInputMode").getAsInt()
        if (skinToken.has("DefaultInputMode")) defaultInputMode = skinToken.get("DefaultInputMode").getAsInt()
        if (skinToken.has("UIProfile")) uIProfile = skinToken.get("UIProfile").getAsInt()
        if (skinToken.has("CapeData")) capeData = skinToken.get("CapeData").getAsString()
        rawData = skinToken
    }

    private fun decodeToken(token: String): JsonObject? {
        val base: Array<String> = token.split("\\.")
        if (base.size < 2) return null
        val json = String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8)
        //Server.getInstance().getLogger().debug(json);
        return Gson().fromJson(json, JsonObject::class.java)
    }

    private fun decodeChainData() {
        val map: Map<String, List<String>> = Gson().fromJson(String(bs.get(bs.getLInt()), StandardCharsets.UTF_8),
                object : TypeToken<Map<String?, List<String?>?>?>() {}.getType())
        if (map.isEmpty() || !map.containsKey("chain") || map["chain"]!!.isEmpty()) return
        val chains = map["chain"]!!

        // Validate keys
        try {
            isXboxAuthed = verifyChain(chains)
        } catch (e: Exception) {
            isXboxAuthed = false
        }
        for (c in chains) {
            val chainMap: JsonObject = decodeToken(c) ?: continue
            if (chainMap.has("extraData")) {
                val extra: JsonObject = chainMap.get("extraData").getAsJsonObject()
                if (extra.has("displayName")) username = extra.get("displayName").getAsString()
                if (extra.has("identity")) clientUUID = UUID.fromString(extra.get("identity").getAsString())
                if (extra.has("XUID")) xUID = extra.get("XUID").getAsString()
            }
            if (chainMap.has("identityPublicKey")) identityPublicKey = chainMap.get("identityPublicKey").getAsString()
        }
        if (!isXboxAuthed) {
            xUID = null
        }
    }

    @Throws(Exception::class)
    private fun verifyChain(chains: List<String>): Boolean {
        var lastKey: PublicKey? = null
        var mojangKeyVerified = false
        for (chain in chains) {
            val jws: JWSObject = JWSObject.parse(chain)
            if (!mojangKeyVerified) {
                // First chain should be signed using Mojang's private key. We'd be in big trouble if it leaked...
                mojangKeyVerified = verify(MOJANG_PUBLIC_KEY, jws)
            }
            if (lastKey != null) {
                if (!verify(lastKey, jws)) {
                    throw JOSEException("Unable to verify key in chain.")
                }
            }
            val payload: JSONObject = jws.getPayload().toJSONObject()
            val base64key: String = payload.getAsString("identityPublicKey") ?: throw RuntimeException("No key found")
            lastKey = generateKey(base64key)
        }
        return mojangKeyVerified
    }

    @Throws(JOSEException::class)
    private fun verify(key: PublicKey?, `object`: JWSObject): Boolean {
        val verifier: JWSVerifier = DefaultJWSVerifierFactory().createJWSVerifier(`object`.getHeader(), key)
        return `object`.verify(verifier)
    }

    init {
        bs.setBuffer(buffer, 0)
        decodeChainData()
        decodeSkinData()
    }
}