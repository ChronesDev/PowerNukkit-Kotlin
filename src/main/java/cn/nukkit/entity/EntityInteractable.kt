package cn.nukkit.entity

import kotlin.jvm.JvmOverloads
import kotlin.jvm.Volatile
import kotlin.jvm.Synchronized

/**
 * @author Adam Matthew
 */
interface EntityInteractable {
    // Todo: Passive entity?? i18n and boat leaving text
    val interactButtonText: String?
    fun canDoInteraction(): Boolean
}