package cn.nukkit.blockproperty.value

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
enum class CrackState {
    NO_CRACKS, CRACKED, MAX_CRACKED {
        @Nullable
        @Override
        override fun getNext(): CrackState? {
            return null
        }
    };

    @get:Nullable
    @get:Since("1.4.0.0-PN")
    @get:PowerNukkitOnly
    open val next: CrackState?
        get() = VALUES[ordinal() + 1]

    companion object {
        private val VALUES = values()
    }
}