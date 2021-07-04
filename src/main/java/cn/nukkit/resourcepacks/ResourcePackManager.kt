package cn.nukkit.resourcepacks

import cn.nukkit.Server

@Log4j2
class ResourcePackManager(path: File) {
    private val resourcePacksById: Map<UUID, ResourcePack> = HashMap()
    private val resourcePacks: Array<ResourcePack>
    val resourceStack: Array<cn.nukkit.resourcepacks.ResourcePack>
        get() = resourcePacks

    fun getPackById(id: UUID): ResourcePack? {
        return resourcePacksById[id]
    }

    init {
        if (!path.exists()) {
            path.mkdirs()
        } else if (!path.isDirectory()) {
            throw IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.invalid-path", path.getName()))
        }
        val loadedResourcePacks: List<ResourcePack> = ArrayList()
        for (pack in path.listFiles()) {
            try {
                var resourcePack: ResourcePack? = null
                if (!pack.isDirectory()) { //directory resource packs temporarily unsupported
                    when (Files.getFileExtension(pack.getName())) {
                        "zip", "mcpack" -> resourcePack = ZippedResourcePack(pack)
                        else -> log.warn(Server.getInstance().getLanguage()
                                .translateString("nukkit.resources.unknown-format", pack.getName()))
                    }
                }
                if (resourcePack != null) {
                    loadedResourcePacks.add(resourcePack)
                    resourcePacksById.put(resourcePack.getPackId(), resourcePack)
                }
            } catch (e: IllegalArgumentException) {
                log.warn(Server.getInstance().getLanguage().translateString("nukkit.resources.fail", pack.getName(), e.getMessage()), e)
            }
        }
        resourcePacks = loadedResourcePacks.toArray(ResourcePack.EMPTY_ARRAY)
        log.info(Server.getInstance().getLanguage()
                .translateString("nukkit.resources.success", String.valueOf(resourcePacks.size)))
    }
}