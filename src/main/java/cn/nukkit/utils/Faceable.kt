package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitOnly

interface Faceable {
    // Does nothing by default
    @set:Since("1.3.0.0-PN")
    @set:PowerNukkitOnly
    var blockFace: BlockFace?
        set(face) {
            // Does nothing by default
        }
}