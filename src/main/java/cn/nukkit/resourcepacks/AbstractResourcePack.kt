package cn.nukkit.resourcepacks

import com.google.gson.JsonArray

abstract class AbstractResourcePack : ResourcePack {
    protected var manifest: JsonObject? = null
    private var id: UUID? = null
    protected fun verifyManifest(): Boolean {
        return if (manifest.has("format_version") && manifest.has("header") && manifest.has("modules")) {
            val header: JsonObject = manifest.getAsJsonObject("header")
            header.has("description") &&
                    header.has("name") &&
                    header.has("uuid") &&
                    header.has("version") && header.getAsJsonArray("version").size() === 3
        } else {
            false
        }
    }

    @get:Override
    override val packName: String?
        get() = manifest.getAsJsonObject("header")
                .get("name").getAsString()

    @get:Override
    override val packId: UUID?
        get() {
            if (id == null) {
                id = UUID.fromString(manifest.getAsJsonObject("header").get("uuid").getAsString())
            }
            return id
        }

    @get:Override
    override val packVersion: String?
        get() {
            val version: JsonArray = manifest.getAsJsonObject("header")
                    .get("version").getAsJsonArray()
            return String.join(".", version.get(0).getAsString(),
                    version.get(1).getAsString(),
                    version.get(2).getAsString())
        }
}