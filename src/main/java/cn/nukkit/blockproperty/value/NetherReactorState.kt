package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class NetherReactorState {
    READY, INITIALIZED, FINISHED;

    companion object {
        private val values = values()
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        fun getFromData(data: Int): NetherReactorState {
            return values[data]
        }
    }
}