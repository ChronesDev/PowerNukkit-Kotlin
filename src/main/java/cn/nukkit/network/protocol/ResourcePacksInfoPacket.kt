package cn.nukkit.network.protocol

import cn.nukkit.resourcepacks.ResourcePack

@ToString
class ResourcePacksInfoPacket : DataPacket() {
    var mustAccept = false
    var scripting = false
    var behaviourPackEntries: Array<ResourcePack> = ResourcePack.EMPTY_ARRAY
    var resourcePackEntries: Array<ResourcePack> = ResourcePack.EMPTY_ARRAY

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putBoolean(mustAccept)
        this.putBoolean(scripting)
        encodePacks(behaviourPackEntries)
        encodePacks(resourcePackEntries)
    }

    private fun encodePacks(packs: Array<ResourcePack>) {
        this.putLShort(packs.size)
        for (entry in packs) {
            this.putString(entry.getPackId().toString())
            this.putString(entry.getPackVersion())
            this.putLLong(entry.getPackSize())
            this.putString("") // encryption key
            this.putString("") // sub-pack name
            this.putString("") // content identity
            this.putBoolean(false) // scripting
            this.putBoolean(false) // raytracing capable
        }
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.RESOURCE_PACKS_INFO_PACKET
    }
}