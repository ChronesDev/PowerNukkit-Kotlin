package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class IntIncrementSupplier @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(private var next: Int, private val increment: Int) : IntSupplier {
    val asInt: Int
        @Override get() {
            val current = next
            next = current + increment
            return current
        }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun stream(): IntStream {
        return IntStream.generate(this)
    }
}