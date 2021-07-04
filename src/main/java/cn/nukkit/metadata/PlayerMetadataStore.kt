package cn.nukkit.metadata

import cn.nukkit.IPlayer

/**
 * @author MagicDroidX (Nukkit Project)
 */
class PlayerMetadataStore : MetadataStore() {
    @Override
    protected fun disambiguate(player: Metadatable, metadataKey: String): String {
        if (player !is IPlayer) {
            throw IllegalArgumentException("Argument must be an IPlayer instance")
        }
        return ((player as IPlayer).getName().toString() + ":" + metadataKey).toLowerCase()
    }
}