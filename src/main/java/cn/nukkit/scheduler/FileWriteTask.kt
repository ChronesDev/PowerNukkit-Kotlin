package cn.nukkit.scheduler

import cn.nukkit.utils.Utils

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
class FileWriteTask : AsyncTask {
    private val file: File
    private val contents: InputStream

    constructor(path: String?, contents: String?) : this(File(path), contents) {}
    constructor(path: String?, contents: ByteArray?) : this(File(path), contents) {}
    constructor(path: String?, contents: InputStream) {
        file = File(path)
        this.contents = contents
    }

    constructor(file: File, contents: String) {
        this.file = file
        this.contents = ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8))
    }

    constructor(file: File, contents: ByteArray?) {
        this.file = file
        this.contents = ByteArrayInputStream(contents)
    }

    constructor(file: File, contents: InputStream) {
        this.file = file
        this.contents = contents
    }

    @Override
    override fun onRun() {
        try {
            Utils.writeFile(file, contents)
        } catch (e: IOException) {
            log.fatal("An error occurred while writing the file {}", file, e)
        }
    }
}