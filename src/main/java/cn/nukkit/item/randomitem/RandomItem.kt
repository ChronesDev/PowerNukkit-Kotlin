package cn.nukkit.item.randomitem

import java.util.HashMap

/**
 * @author Snake1999
 * @since 2016/1/15
 */
object RandomItem {
    private val selectors: Map<Selector, Float> = HashMap()
    val ROOT: Selector = Selector(null)
    fun putSelector(selector: Selector): Selector {
        return putSelector(selector, 1f)
    }

    fun putSelector(selector: Selector, chance: Float): Selector {
        if (selector.getParent() == null) selector.setParent(ROOT)
        selectors.put(selector, chance)
        return selector
    }

    fun selectFrom(selector: Selector?): Object {
        Objects.requireNonNull(selector)
        val child: Map<Selector?, Float> = HashMap()
        selectors.forEach { s, f -> if (s.getParent() === selector) child.put(s, f) }
        return if (child.size() === 0) selector!!.select() else selectFrom(Selector.selectRandom(child))
    }
}