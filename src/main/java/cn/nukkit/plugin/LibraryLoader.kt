package cn.nukkit.plugin

import java.io.File

/**
 * @since 15-12-13
 */
object LibraryLoader {
    private val BASE_FOLDER: File = File("./libraries")
    private val LOGGER: Logger = Logger.getLogger("LibraryLoader")
    private const val SUFFIX = ".jar"
    fun load(library: String) {
        val split: Array<String> = library.split(":")
        if (split.size != 3) {
            throw IllegalArgumentException(library)
        }
        load(object : Library() {
            override fun getGroupId(): String {
                return split[0]
            }

            override fun getArtifactId(): String {
                return split[1]
            }

            override fun getVersion(): String {
                return split[2]
            }
        })
    }

    fun load(library: Library) {
        val filePath: String = library.getGroupId().replace('.', '/') + '/' + library.getArtifactId() + '/' + library.getVersion()
        val fileName: String = library.getArtifactId() + '-' + library.getVersion() + SUFFIX
        val folder = File(BASE_FOLDER, filePath)
        if (folder.mkdirs()) {
            LOGGER.info("Created " + folder.getPath() + '.')
        }
        val file = File(folder, fileName)
        if (!file.isFile()) try {
            val url = URL("https://repo1.maven.org/maven2/$filePath/$fileName")
            LOGGER.info("Get library from $url.")
            Files.copy(url.openStream(), file.toPath())
            LOGGER.info("Get library $fileName done!")
        } catch (e: IOException) {
            throw LibraryLoadException(library)
        }
        try {
            val method: Method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
            val accessible: Boolean = method.isAccessible()
            if (!accessible) {
                method.setAccessible(true)
            }
            val classLoader: URLClassLoader = Thread.currentThread().getContextClassLoader() as URLClassLoader
            val url: URL = file.toURI().toURL()
            method.invoke(classLoader, url)
            method.setAccessible(accessible)
        } catch (e: NoSuchMethodException) {
            throw LibraryLoadException(library)
        } catch (e: MalformedURLException) {
            throw LibraryLoadException(library)
        } catch (e: IllegalAccessException) {
            throw LibraryLoadException(library)
        } catch (e: InvocationTargetException) {
            throw LibraryLoadException(library)
        }
        LOGGER.info("Load library $fileName done!")
    }

    fun getBaseFolder(): File {
        return BASE_FOLDER
    }

    init {
        if (BASE_FOLDER.mkdir()) {
            LOGGER.info("Created libraries folder.")
        }
    }
}