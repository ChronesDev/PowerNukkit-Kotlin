package cn.nukkit.entity.weather

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Volatile
import kotlin.jvm.Synchronized

/**
 * @author funcraft
 * @since 2016/2/27
 */
interface EntityLightningStrike : EntityWeather {
    var isEffect: Boolean
}