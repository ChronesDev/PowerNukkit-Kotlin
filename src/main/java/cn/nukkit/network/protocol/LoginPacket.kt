package cn.nukkit.network.protocol

import cn.nukkit.entity.data.Skin

/**
 * @since on 15-10-13
 */
@ToString
class LoginPacket : DataPacket() {
    var username: String? = null
    var protocol = 0
    var clientUUID: UUID? = null
    var clientId: Long = 0
    var skin: Skin? = null

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    @Override
    override fun decode() {
        protocol = this.getInt()
        if (protocol == 0) {
            setOffset(getOffset() + 2)
            protocol = getInt()
        }
        if (!ProtocolInfo.SUPPORTED_PROTOCOLS.contains(protocol)) {
            // decoding the chain could cause issues on newer or older versions.
            return
        }
        this.setBuffer(this.getByteArray(), 0)
        decodeChainData()
        decodeSkinData()
    }

    @Override
    override fun encode() {
    }

    private fun decodeChainData() {
        val map: Map<String, List<String>> = Gson().fromJson(String(this.get(getLInt()), StandardCharsets.UTF_8),
                object : TypeToken<Map<String?, List<String?>?>?>() {}.getType())
        if (map.isEmpty() || !map.containsKey("chain") || map["chain"]!!.isEmpty()) return
        val chains = map["chain"]!!
        for (c in chains) {
            val chainMap: JsonObject = decodeToken(c) ?: continue
            if (chainMap.has("extraData")) {
                val extra: JsonObject = chainMap.get("extraData").getAsJsonObject()
                if (extra.has("displayName")) username = extra.get("displayName").getAsString()
                if (extra.has("identity")) clientUUID = UUID.fromString(extra.get("identity").getAsString())
            }
        }
    }

    private fun decodeSkinData() {
        val skinToken: JsonObject? = decodeToken(String(this.get(this.getLInt())))
        if (skinToken.has("ClientRandomId")) clientId = skinToken.get("ClientRandomId").getAsLong()
        skin = Skin()
        if (skinToken.has("SkinId")) {
            skin.setSkinId(skinToken.get("SkinId").getAsString())
        }
        if (skinToken.has("PlayFabID")) {
            skin.setPlayFabId(skinToken.get("PlayFabID").getAsString())
        }
        if (skinToken.has("CapeId")) {
            skin.setCapeId(skinToken.get("CapeId").getAsString())
        }
        skin.setSkinData(getImage(skinToken, "Skin"))
        skin.setCapeData(getImage(skinToken, "Cape"))
        if (skinToken.has("PremiumSkin")) {
            skin.setPremium(skinToken.get("PremiumSkin").getAsBoolean())
        }
        if (skinToken.has("PersonaSkin")) {
            skin.setPersona(skinToken.get("PersonaSkin").getAsBoolean())
        }
        if (skinToken.has("CapeOnClassicSkin")) {
            skin.setCapeOnClassic(skinToken.get("CapeOnClassicSkin").getAsBoolean())
        }
        if (skinToken.has("SkinResourcePatch")) {
            skin.setSkinResourcePatch(String(Base64.getDecoder().decode(skinToken.get("SkinResourcePatch").getAsString()), StandardCharsets.UTF_8))
        }
        if (skinToken.has("SkinGeometryData")) {
            skin.setGeometryData(String(Base64.getDecoder().decode(skinToken.get("SkinGeometryData").getAsString()), StandardCharsets.UTF_8))
        }
        if (skinToken.has("AnimationData")) {
            skin.setAnimationData(String(Base64.getDecoder().decode(skinToken.get("AnimationData").getAsString()), StandardCharsets.UTF_8))
        }
        if (skinToken.has("AnimatedImageData")) {
            val array: JsonArray = skinToken.get("AnimatedImageData").getAsJsonArray()
            for (element in array) {
                skin.getAnimations().add(getAnimation(element.getAsJsonObject()))
            }
        }
        if (skinToken.has("SkinColor")) {
            skin.setSkinColor(skinToken.get("SkinColor").getAsString())
        }
        if (skinToken.has("ArmSize")) {
            skin.setArmSize(skinToken.get("ArmSize").getAsString())
        }
        if (skinToken.has("PersonaPieces")) {
            for (`object` in skinToken.get("PersonaPieces").getAsJsonArray()) {
                skin.getPersonaPieces().add(getPersonaPiece(`object`.getAsJsonObject()))
            }
        }
        if (skinToken.has("PieceTintColors")) {
            for (`object` in skinToken.get("PieceTintColors").getAsJsonArray()) {
                skin.getTintColors().add(getTint(`object`.getAsJsonObject()))
            }
        }
    }

    private fun decodeToken(token: String): JsonObject? {
        val base: Array<String> = token.split("\\.")
        return if (base.size < 2) null else Gson().fromJson(String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8), JsonObject::class.java)
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.LOGIN_PACKET
        private fun getAnimation(element: JsonObject): SkinAnimation {
            val frames: Float = element.get("Frames").getAsFloat()
            val type: Int = element.get("Type").getAsInt()
            val data: ByteArray = Base64.getDecoder().decode(element.get("Image").getAsString())
            val width: Int = element.get("ImageWidth").getAsInt()
            val height: Int = element.get("ImageHeight").getAsInt()
            val expression: Int = element.get("AnimationExpression").getAsInt()
            return SkinAnimation(SerializedImage(width, height, data), type, frames, expression)
        }

        private fun getImage(token: JsonObject?, name: String): SerializedImage {
            if (token.has(name + "Data")) {
                val skinImage: ByteArray = Base64.getDecoder().decode(token.get(name + "Data").getAsString())
                return if (token.has(name + "ImageHeight") && token.has(name + "ImageWidth")) {
                    val width: Int = token.get(name + "ImageWidth").getAsInt()
                    val height: Int = token.get(name + "ImageHeight").getAsInt()
                    SerializedImage(width, height, skinImage)
                } else {
                    SerializedImage.fromLegacy(skinImage)
                }
            }
            return SerializedImage.EMPTY
        }

        private fun getPersonaPiece(`object`: JsonObject): PersonaPiece {
            val pieceId: String = `object`.get("PieceId").getAsString()
            val pieceType: String = `object`.get("PieceType").getAsString()
            val packId: String = `object`.get("PackId").getAsString()
            val isDefault: Boolean = `object`.get("IsDefault").getAsBoolean()
            val productId: String = `object`.get("ProductId").getAsString()
            return PersonaPiece(pieceId, pieceType, packId, isDefault, productId)
        }

        fun getTint(`object`: JsonObject): PersonaPieceTint {
            val pieceType: String = `object`.get("PieceType").getAsString()
            val colors: List<String> = ArrayList()
            for (element in `object`.get("Colors").getAsJsonArray()) {
                colors.add(element.getAsString()) // remove #
            }
            return PersonaPieceTint(pieceType, colors)
        }
    }
}