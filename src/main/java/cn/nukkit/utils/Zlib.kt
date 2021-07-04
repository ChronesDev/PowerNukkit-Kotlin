package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitDifference

@Log4j2
object Zlib {
    private var providers: Array<ZlibProvider?>
    private var provider: ZlibProvider? = null
    fun setProvider(providerIndex: Int) {
        log.info("Selected Zlib Provider: {} ({})", providerIndex, provider.getClass().getCanonicalName())
        when (providerIndex) {
            0 -> if (providers[providerIndex] == null) providers[providerIndex] = ZlibOriginal()
            1 -> if (providers[providerIndex] == null) providers[providerIndex] = ZlibSingleThreadLowMem()
            2 -> if (providers[providerIndex] == null) providers[providerIndex] = ZlibThreadLocal()
            else -> throw UnsupportedOperationException("Invalid provider: $providerIndex")
        }
        if (providerIndex != 2) {
            log.warn(" - This Zlib will negatively affect performance")
        }
        provider = providers[providerIndex]
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    @Throws(IOException::class)
    fun deflate(data: ByteArray?): ByteArray {
        return deflate(data, Deflater.DEFAULT_COMPRESSION)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    @Throws(IOException::class)
    fun deflate(data: ByteArray?, level: Int): ByteArray {
        return provider!!.deflate(data, level)
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Throws IOException instead of Exception")
    @Throws(IOException::class)
    fun deflate(data: Array<ByteArray?>?, level: Int): ByteArray {
        return provider!!.deflate(data, level)
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun inflate(data: ByteArray?, maxSize: Int = -1): ByteArray {
        return provider!!.inflate(data, maxSize)
    }

    init {
        providers = arrayOfNulls<ZlibProvider>(3)
        providers[2] = ZlibThreadLocal()
        provider = providers[2]
    }
}