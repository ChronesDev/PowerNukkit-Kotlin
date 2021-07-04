package cn.nukkit.item.food

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient
import kotlin.Throws

/**
 * @author Snake1999
 * @since 2016/1/13
 */
class FoodNormal(restoreFood: Int, restoreSaturation: Float) : Food() {
    init {
        this.setRestoreFood(restoreFood)
        this.setRestoreSaturation(restoreSaturation)
    }
}