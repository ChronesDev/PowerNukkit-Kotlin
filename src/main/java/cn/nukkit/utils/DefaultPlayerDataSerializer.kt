package cn.nukkit.utils

import cn.nukkit.Server

class DefaultPlayerDataSerializer @Since("1.4.0.0-PN") constructor(dataPath: String?) : PlayerDataSerializer {
    private val dataPath: String? = null

    @Since("1.4.0.0-PN")
    constructor(server: Server) : this(server.getDataPath()) {
    }

    @Override
    @Throws(IOException::class)
    fun read(name: String, uuid: UUID?): Optional<InputStream> {
        val path = dataPath.toString() + "players/" + name + ".dat"
        val file = File(path)
        return if (!file.exists()) {
            Optional.empty()
        } else Optional.of(FileInputStream(file))
    }

    @Override
    @Throws(IOException::class)
    fun write(name: String, uuid: UUID?): OutputStream {
        Preconditions.checkNotNull(name, "name")
        val path = dataPath.toString() + "players/" + name + ".dat"
        val file = File(path)
        return FileOutputStream(file)
    }

    init {
        this.dataPath = dataPath
    }
}