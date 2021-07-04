package cn.nukkit.math

import cn.nukkit.api.PowerNukkitOnly

@PowerNukkitOnly
@Since("1.4.0.0-PN")
class AtomicIntIncrementSupplier @PowerNukkitOnly @Since("1.4.0.0-PN") constructor(first: Int, increment: Int) : IntSupplier {
    private val next: AtomicInteger
    private val increment: Int
    val asInt: Int
        @Override get() = next.getAndAdd(increment)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun stream(): IntStream {
        return IntStream.generate(this)
    }

    init {
        next = AtomicInteger(first)
        this.increment = increment
    }
}