package cn.nukkit.resourcepacks

import cn.nukkit.Server

@Log4j2
class ZippedResourcePack @PowerNukkitDifference(info = "Accepts resource packs with subfolder structure", since = "1.4.0.0-PN") constructor(file: File) : AbstractResourcePack() {
    private val file: File

    @get:Override
    override var sha256: ByteArray? = null
        get() {
            if (field == null) {
                try {
                    field = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file.toPath()))
                } catch (e: Exception) {
                    log.error("Failed to parse the SHA-256 of the resource pack {}", file, e)
                }
            }
            return field
        }
        private set

    @get:Override
    override val packSize: Int
        get() = file.length()

    @Override
    override fun getPackChunk(off: Int, len: Int): ByteArray {
        val chunk: ByteArray
        chunk = if (packSize - off > len) {
            ByteArray(len)
        } else {
            ByteArray(packSize - off)
        }
        try {
            FileInputStream(file).use { fis ->
                fis.skip(off)
                fis.read(chunk)
            }
        } catch (e: Exception) {
            log.error("An error occurred while processing the resource pack {} at offset:{} and length:{}", file, off, len, e)
        }
        return chunk
    }

    init {
        if (!file.exists()) {
            throw IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.not-found", file.getName()))
        }
        this.file = file
        try {
            ZipFile(file).use { zip ->
                var entry: ZipEntry = zip.getEntry("manifest.json")
                if (entry == null) {
                    entry = zip.stream()
                            .filter { e -> e.getName().toLowerCase().endsWith("manifest.json") && !e.isDirectory() }
                            .filter { e ->
                                val fe = File(e.getName())
                                if (!fe.getName().equalsIgnoreCase("manifest.json")) {
                                    return@filter false
                                }
                                fe.getParent() == null || fe.getParentFile().getParent() == null
                            }
                            .findFirst()
                            .orElseThrow {
                                IllegalArgumentException(
                                        Server.getInstance().getLanguage().translateString("nukkit.resources.zip.no-manifest"))
                            }
                }
                this.manifest = JsonParser()
                        .parse(InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8))
                        .getAsJsonObject()
            }
        } catch (e: IOException) {
            log.error("An error occurred while loading the zipped resource pack {}", file, e)
        }
        if (!this.verifyManifest()) {
            throw IllegalArgumentException(Server.getInstance().getLanguage()
                    .translateString("nukkit.resources.zip.invalid-manifest"))
        }
    }
}