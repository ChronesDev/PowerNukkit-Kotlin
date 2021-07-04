package cn.nukkit.network.protocol

import cn.nukkit.resourcepacks.ResourcePack

@ToString
class ResourcePackStackPacket : DataPacket() {
    var mustAccept = false
    var behaviourPackStack: Array<ResourcePack> = ResourcePack.EMPTY_ARRAY
    var resourcePackStack: Array<ResourcePack> = ResourcePack.EMPTY_ARRAY
    var isExperimental = false
    var gameVersion: String = ProtocolInfo.MINECRAFT_VERSION_NETWORK

    @Override
    override fun decode() {
    }

    @Override
    override fun encode() {
        this.reset()
        this.putBoolean(mustAccept)
        this.putUnsignedVarInt(behaviourPackStack.size)
        for (entry in behaviourPackStack) {
            this.putString(entry.getPackId().toString())
            this.putString(entry.getPackVersion())
            this.putString("") //TODO: subpack name
        }
        this.putUnsignedVarInt(resourcePackStack.size)
        for (entry in resourcePackStack) {
            this.putString(entry.getPackId().toString())
            this.putString(entry.getPackVersion())
            this.putString("") //TODO: subpack name
        }
        this.putString(gameVersion)
        this.putLInt(0) // Experiments length
        this.putBoolean(false) // Were experiments previously toggled
    }

    @Override
    override fun pid(): Byte {
        return NETWORK_ID
    }

    companion object {
        val NETWORK_ID: Byte = ProtocolInfo.RESOURCE_PACK_STACK_PACKET
    }
}