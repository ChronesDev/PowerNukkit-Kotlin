package cn.nukkit.network.protocol

import cn.nukkit.entity.data.Skin

@ToString
class PlayerSkinPacket : DataPacket() {
    var uuid: UUID? = null
    var skin: Skin? = null
    var newSkinName: String? = null
    var oldSkinName: String? = null

    @Override
    override fun pid(): Byte {
        return ProtocolInfo.PLAYER_SKIN_PACKET
    }

    @Override
    override fun decode() {
        uuid = getUUID()
        skin = getSkin()
        newSkinName = getString()
        oldSkinName = getString()
        if (!feof()) { // -facepalm-
            skin.setTrusted(getBoolean())
        }
    }

    @Override
    override fun encode() {
        reset()
        putUUID(uuid)
        putSkin(skin)
        putString(newSkinName)
        putString(oldSkinName)
        putBoolean(skin.isTrusted())
    }
}