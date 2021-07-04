package cn.nukkit.utils

import cn.nukkit.api.PowerNukkitOnly

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
object Utils {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    val EMPTY_INTEGERS: Array<Integer?> = arrayOfNulls<Integer>(0)

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    val random: SplittableRandom = SplittableRandom()
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(IOException::class)
    fun safeWrite(currentFile: File, operation: Consumer<File?>) {
        val parent: File = currentFile.getParentFile()
        val newFile = File(parent, currentFile.getName().toString() + "_new")
        val oldFile = File(parent, currentFile.getName().toString() + "_old")
        val olderFile = File(parent, currentFile.getName().toString() + "_older")
        if (olderFile.isFile() && !olderFile.delete()) {
            log.fatal("Could not delete the file {}", olderFile.getAbsolutePath())
        }
        if (newFile.isFile() && !newFile.delete()) {
            log.fatal("Could not delete the file {}", newFile.getAbsolutePath())
        }
        try {
            operation.accept(newFile)
        } catch (e: Exception) {
            throw IOException(e)
        }
        if (oldFile.isFile()) {
            if (olderFile.isFile()) {
                copyFile(oldFile, olderFile)
            } else if (!oldFile.renameTo(olderFile)) {
                throw IOException("Could not rename the $oldFile to $olderFile")
            }
        }
        if (currentFile.isFile() && !currentFile.renameTo(oldFile)) {
            throw IOException("Could not rename the $currentFile to $oldFile")
        }
        if (!newFile.renameTo(currentFile)) {
            throw IOException("Could not rename the $newFile to $currentFile")
        }
    }

    @Throws(IOException::class)
    fun writeFile(fileName: String?, content: String) {
        writeFile(fileName, ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)))
    }

    @Throws(IOException::class)
    fun writeFile(fileName: String?, content: InputStream?) {
        writeFile(File(fileName), content)
    }

    @Throws(IOException::class)
    fun writeFile(file: File?, content: String) {
        writeFile(file, ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)))
    }

    @Throws(IOException::class)
    fun writeFile(file: File, content: InputStream?) {
        if (content == null) {
            throw IllegalArgumentException("content must not be null")
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        FileOutputStream(file).use { stream ->
            val buffer = ByteArray(1024)
            var length: Int
            while (content.read(buffer).also { length = it } != -1) {
                stream.write(buffer, 0, length)
            }
        }
        content.close()
    }

    @Throws(IOException::class)
    fun readFile(file: File): String {
        if (!file.exists() || file.isDirectory()) {
            throw FileNotFoundException()
        }
        return readFile(FileInputStream(file))
    }

    @Throws(IOException::class)
    fun readFile(filename: String?): String {
        val file = File(filename)
        if (!file.exists() || file.isDirectory()) {
            throw FileNotFoundException()
        }
        return readFile(FileInputStream(file))
    }

    @Throws(IOException::class)
    fun readFile(inputStream: InputStream?): String {
        return readFile(InputStreamReader(inputStream, StandardCharsets.UTF_8))
    }

    @Throws(IOException::class)
    private fun readFile(reader: Reader): String {
        BufferedReader(reader).use { br ->
            var temp: String
            val stringBuilder = StringBuilder()
            temp = br.readLine()
            while (temp != null) {
                if (stringBuilder.length() !== 0) {
                    stringBuilder.append("\n")
                }
                stringBuilder.append(temp)
                temp = br.readLine()
            }
            return stringBuilder.toString()
        }
    }

    @Throws(IOException::class)
    fun copyFile(from: File, to: File) {
        if (!from.exists()) {
            throw FileNotFoundException()
        }
        if (from.isDirectory() || to.isDirectory()) {
            throw FileNotFoundException()
        }
        var fi: FileInputStream? = null
        var `in`: FileChannel? = null
        var fo: FileOutputStream? = null
        var out: FileChannel? = null
        try {
            if (!to.exists()) {
                to.createNewFile()
            }
            fi = FileInputStream(from)
            `in` = fi.getChannel()
            fo = FileOutputStream(to)
            out = fo.getChannel()
            `in`.transferTo(0, `in`.size(), out)
        } finally {
            if (fi != null) fi.close()
            if (`in` != null) `in`.close()
            if (fo != null) fo.close()
            if (out != null) out.close()
        }
    }

    val allThreadDumps: String
        get() {
            val threads: Array<ThreadInfo> = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)
            val builder = StringBuilder()
            for (info in threads) {
                builder.append('\n').append(info)
            }
            return builder.toString()
        }

    fun getExceptionMessage(e: Throwable): String {
        val stringWriter = StringWriter()
        PrintWriter(stringWriter).use { printWriter ->
            e.printStackTrace(printWriter)
            printWriter.flush()
        }
        return stringWriter.toString()
    }

    fun dataToUUID(vararg params: String?): UUID {
        val builder = StringBuilder()
        for (param in params) {
            builder.append(param)
        }
        return UUID.nameUUIDFromBytes(builder.toString().getBytes(StandardCharsets.UTF_8))
    }

    fun dataToUUID(vararg params: ByteArray?): UUID {
        val stream = ByteArrayOutputStream()
        for (param in params) {
            try {
                stream.write(param)
            } catch (e: IOException) {
                break
            }
        }
        return UUID.nameUUIDFromBytes(stream.toByteArray())
    }

    fun rtrim(s: String, character: Char): String {
        var i: Int = s.length() - 1
        while (i >= 0 && s.charAt(i) === character) {
            i--
        }
        return s.substring(0, i + 1)
    }

    fun isByteArrayEmpty(array: ByteArray): Boolean {
        for (b in array) {
            if (b.toInt() != 0) {
                return false
            }
        }
        return true
    }

    fun toRGB(r: Byte, g: Byte, b: Byte, a: Byte): Long {
        var result = (r.toInt() and 0xff).toLong()
        result = result or (g.toInt() and 0xff shl 8).toLong()
        result = result or (b.toInt() and 0xff shl 16).toLong()
        result = result or (a.toInt() and 0xff shl 24).toLong()
        return result and 0xFFFFFFFFL
    }

    fun toABGR(argb: Int): Long {
        var result = (argb and 0xFF00FF00L).toLong()
        result = result or (argb shl 16 and 0x00FF0000L).toLong() // B to R
        result = result or (argb ushr 16 and 0xFFL).toLong() // R to B
        return result and 0xFFFFFFFFL
    }

    fun splitArray(arrayToSplit: Array<Object?>, chunkSize: Int): Array<Array<Object>>? {
        if (chunkSize <= 0) {
            return null
        }
        val rest = arrayToSplit.size % chunkSize
        val chunks = arrayToSplit.size / chunkSize + if (rest > 0) 1 else 0
        val arrays: Array<Array<Object>> = arrayOfNulls<Array<Object>>(chunks)
        for (i in 0 until if (rest > 0) chunks - 1 else chunks) {
            arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize)
        }
        if (rest > 0) {
            arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest)
        }
        return arrays
    }

    fun <T> reverseArray(data: Array<T>) {
        reverseArray(data, false)
    }

    fun <T> reverseArray(array: Array<T>, copy: Boolean): Array<T> {
        var data = array
        if (copy) {
            data = Arrays.copyOf(array, array.size)
        }
        var left = 0
        var right = data.size - 1
        while (left < right) {

            // swap the values at the left and right indices
            val temp = data[left]
            data[left] = data[right]
            data[right] = temp
            left++
            right--
        }
        return data
    }

    fun <T> clone2dArray(array: Array<Array<T>>): Array<Array<T>> {
        val newArray: Array<Array<T>> = Arrays.copyOf(array, array.size)
        for (i in array.indices) {
            newArray[i] = Arrays.copyOf(array[i], array[i].length)
        }
        return newArray
    }

    fun <T, U, V> getOrCreate(map: Map<T, Map<U, V>?>, key: T): Map<U, V> {
        var existing = map[key]
        if (existing == null) {
            val toPut: ConcurrentHashMap<U, V> = ConcurrentHashMap()
            existing = map.putIfAbsent(key, toPut)
            if (existing == null) {
                existing = toPut
            }
        }
        return existing
    }

    fun <T, U, V : U?> getOrCreate(map: Map<T, U>, clazz: Class<V>, key: T): U? {
        var existing = map[key]
        return existing
                ?: try {
                    val toPut: U = clazz.newInstance()
                    existing = map.putIfAbsent(key, toPut)
                    existing ?: toPut
                } catch (e: InstantiationException) {
                    throw RuntimeException(e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException(e)
                }
    }

    fun toInt(number: Object): Int {
        return if (number is Integer) {
            number as Integer
        } else Math.round(number as Double)
    }

    fun parseHexBinary(s: String): ByteArray {
        val len: Int = s.length()

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) throw IllegalArgumentException("hexBinary needs to be even-length: $s")
        val out = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            val h = hexToBin(s.charAt(i))
            val l = hexToBin(s.charAt(i + 1))
            if (h == -1 || l == -1) throw IllegalArgumentException("contains illegal character for hexBinary: $s")
            out[i / 2] = (h * 16 + l).toByte()
            i += 2
        }
        return out
    }

    private fun hexToBin(ch: Char): Int {
        if ('0' <= ch && ch <= '9') return ch - '0'
        if ('A' <= ch && ch <= 'F') return ch - 'A' + 10
        return if ('a' <= ch && ch <= 'f') ch - 'a' + 10 else -1
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun rand(min: Int, max: Int): Int {
        return if (min == max) {
            max
        } else random.nextInt(max + 1 - min) + min
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun rand(min: Double, max: Double): Double {
        return if (min == max) {
            max
        } else min + random.nextDouble() * (max - min)
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun rand(): Boolean {
        return random.nextBoolean()
    }

    /**
     * A way to tell the java compiler to do not replace the users of a `public static final int` constant
     * with the value defined in it, forcing the JVM to get the value directly from the class, preventing
     * binary incompatible changes.
     * @see [](https://stackoverflow.com/a/12065326/804976>https://stackoverflow.com/a/12065326/804976</a>

    @param value The value to be assigned to the field.
    @return The same value that was passed as parameter
    ) */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun dynamic(value: Int): Int {
        return value
    }

    /**
     * A way to tell the java compiler to do not replace the users of a `public static final` constant
     * with the value defined in it, forcing the JVM to get the value directly from the class, preventing
     * binary incompatible changes.
     * @see [](https://stackoverflow.com/a/12065326/804976>https://stackoverflow.com/a/12065326/804976</a>

    @param value The value to be assigned to the field.
    @return The same value that was passed as parameter
    ) */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    fun <T> dynamic(value: T): T {
        return value
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Throws(IOException::class)
    fun zipFolder(sourceFolderPath: Path, zipPath: Path) {
        ZipOutputStream(FileOutputStream(zipPath.toFile())).use { zos ->
            Files.walkFileTree(sourceFolderPath, object : SimpleFileVisitor<Path?>() {
                @Throws(IOException::class)
                fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                    zos.putNextEntry(ZipEntry(sourceFolderPath.relativize(file).toString()))
                    Files.copy(file, zos)
                    zos.closeEntry()
                    return FileVisitResult.CONTINUE
                }
            })
        }
    }
}