package cn.nukkit.entity

import cn.nukkit.Player

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
interface EntityOwnable {
    var ownerName: String?
    val owner: Player?
}