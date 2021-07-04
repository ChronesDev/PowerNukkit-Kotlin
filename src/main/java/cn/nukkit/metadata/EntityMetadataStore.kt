package cn.nukkit.metadata

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class EntityMetadataStore : MetadataStore() {
    @Override
    protected fun disambiguate(entity: Metadatable, metadataKey: String): String {
        if (entity !is Entity) {
            throw IllegalArgumentException("Argument must be an Entity instance")
        }
        return (entity as Entity).getId().toString() + ":" + metadataKey
    }
}