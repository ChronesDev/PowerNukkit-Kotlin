package cn.nukkit.utils.functional

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
interface BooleanConsumer {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun accept(value: Boolean)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun andThen(after: BooleanConsumer): BooleanConsumer? {
        Objects.requireNonNull(after)
        return BooleanConsumer { t: Boolean ->
            accept(t)
            after.accept(t)
        }
    }
}