package cn.nukkit.utils

import java.io.IOException

interface PlayerDataSerializer {
    /**
     * Reads player data from [InputStream] if the file exists otherwise it will create the default data.
     *
     * @param name name of player or [UUID] as [String]
     * @param uuid uuid of player. Could be null if name is used.
     * @return [InputStream] if the player data exists
     */
    @Throws(IOException::class)
    fun read(name: String?, uuid: UUID?): Optional<InputStream?>?

    /**
     * Writes player data to given [OutputStream].
     *
     * @param name name of player or [UUID] as [String]
     * @param uuid uuid of player. Could be null if name is used.
     * @return stream to write player data
     */
    @Throws(IOException::class)
    fun write(name: String?, uuid: UUID?): OutputStream?
}