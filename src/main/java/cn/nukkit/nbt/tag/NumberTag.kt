package cn.nukkit.nbt.tag

import java.util.Objects

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract class NumberTag<T : Number?> protected constructor(name: String?) : Tag(name) {
    abstract var data: T

    @Override
    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), data)
    }
}