package cn.nukkit.item.randomitem

import java.util.Map

/**
 * @author Snake1999
 * @since 2016/1/15
 */
class Selector(parent: Selector?) {
    private var parent: Selector? = null
    fun setParent(parent: Selector?): Selector? {
        this.parent = parent
        return parent
    }

    fun getParent(): Selector? {
        return parent
    }

    fun select(): Object {
        return this
    }

    companion object {
        fun selectRandom(selectorChanceMap: Map<Selector?, Float?>): Selector? {
            val totalChance = floatArrayOf(0f)
            selectorChanceMap.values().forEach { f -> totalChance[0] += f }
            val resultChance = (Math.random() * totalChance[0]) as Float
            val flag = floatArrayOf(0f)
            val found = booleanArrayOf(false)
            val temp = arrayOf<Selector?>(null)
            selectorChanceMap.forEach { o, f ->
                flag[0] += f
                if (flag[0] > resultChance && !found[0]) {
                    temp[0] = o
                    found[0] = true
                }
            }
            return temp[0]
        }
    }

    init {
        setParent(parent)
    }
}