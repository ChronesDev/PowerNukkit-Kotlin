package cn.nukkit.nbt

import cn.nukkit.api.PowerNukkitDifference

/**
 * A Named Binary Tag library for Nukkit Project
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed resource leaks")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "It's the caller responsibility to close the provided streams")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed output streams not being finished correctly")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Added defensive close invocations to byte array streams")
object NBTIO {
    fun putItemHelper(item: Item): CompoundTag {
        return putItemHelper(item, null)
    }

    fun putItemHelper(item: Item, slot: Integer?): CompoundTag {
        val tag: CompoundTag = CompoundTag(null as String?)
                .putShort("id", item.getId())
                .putByte("Count", item.getCount())
                .putShort("Damage", item.getDamage())
        if (slot != null) {
            tag.putByte("Slot", slot)
        }
        if (item.hasCompoundTag()) {
            tag.putCompound("tag", item.getNamedTag())
        }
        return tag
    }

    fun getItemHelper(tag: CompoundTag): Item? {
        if (!tag.contains("id") || !tag.contains("Count")) {
            return Item.get(0)
        }
        val id: Int = tag.getShort("id") as Short.toInt()
        val damage = if (!tag.contains("Damage")) 0 else tag.getShort("Damage")
        val amount: Int = tag.getByte("Count")
        var item: Item? = fixAlphaItem(id, damage, amount)
        if (item == null) {
            try {
                item = Item.get(id, damage, tag.getByte("Count"))
            } catch (e: Exception) {
                item = Item.fromString(tag.getString("id"))
                item.setDamage(damage)
                item.setCount(tag.getByte("Count"))
            }
        }
        val tagTag: Tag = tag.get("tag")
        if (tagTag is CompoundTag) {
            item.setNamedTag(tagTag as CompoundTag)
        }
        return item
    }

    @SuppressWarnings("deprecation")
    private fun fixAlphaItem(id: Int, damage: Int, count: Int): Item? {
        val badAlphaId: PNAlphaItemID = PNAlphaItemID.getBadAlphaId(id) ?: return null
        val recovered: Item = badAlphaId.getMinecraftItemId().get(count)
        if (damage != 0) {
            recovered.setDamage(damage)
        }
        return recovered
    }

    @Throws(IOException::class)
    fun read(file: File?): CompoundTag {
        return read(file, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun read(file: File, endianness: ByteOrder?): CompoundTag? {
        if (!file.exists()) return null
        FileInputStream(file).use { inputStream -> return read(inputStream, endianness) }
    }

    @Throws(IOException::class)
    fun read(inputStream: InputStream?): CompoundTag {
        return read(inputStream, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun read(inputStream: InputStream?, endianness: ByteOrder?): CompoundTag {
        return read(inputStream, endianness, false)
    }

    @Throws(IOException::class)
    fun read(inputStream: InputStream?, endianness: ByteOrder?, network: Boolean): CompoundTag {
        val tag: Tag = Tag.readNamedTag(NBTInputStream(inputStream, endianness, network))
        if (tag is CompoundTag) {
            return tag as CompoundTag
        }
        throw IOException("Root tag must be a named compound tag")
    }

    @Throws(IOException::class)
    fun readTag(inputStream: InputStream?, endianness: ByteOrder?, network: Boolean): Tag {
        return Tag.readNamedTag(NBTInputStream(inputStream, endianness, network))
    }

    @Throws(IOException::class)
    fun read(data: ByteArray?): CompoundTag {
        return read(data, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun read(data: ByteArray?, endianness: ByteOrder?): CompoundTag {
        ByteArrayInputStream(data).use { inputStream -> return read(inputStream, endianness) }
    }

    @Throws(IOException::class)
    fun read(data: ByteArray?, endianness: ByteOrder?, network: Boolean): CompoundTag {
        ByteArrayInputStream(data).use { inputStream -> return read(inputStream, endianness, network) }
    }

    @Throws(IOException::class)
    fun readCompressed(inputStream: InputStream?): CompoundTag {
        return readCompressed(inputStream, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun readCompressed(inputStream: InputStream?, endianness: ByteOrder?): CompoundTag {
        GZIPInputStream(inputStream).use { gzip -> BufferedInputStream(gzip).use { buffered -> return read(buffered, endianness) } }
    }

    @Throws(IOException::class)
    fun readCompressed(data: ByteArray?): CompoundTag {
        return readCompressed(data, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun readCompressed(data: ByteArray?, endianness: ByteOrder?): CompoundTag {
        ByteArrayInputStream(data).use { bytes -> GZIPInputStream(bytes).use { gzip -> BufferedInputStream(gzip).use { buffered -> return read(buffered, endianness, true) } } }
    }

    @Throws(IOException::class)
    fun readNetworkCompressed(inputStream: InputStream?): CompoundTag {
        return readNetworkCompressed(inputStream, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun readNetworkCompressed(inputStream: InputStream?, endianness: ByteOrder?): CompoundTag {
        GZIPInputStream(inputStream).use { gzip -> BufferedInputStream(gzip).use { buffered -> return read(buffered, endianness) } }
    }

    @Throws(IOException::class)
    fun readNetworkCompressed(data: ByteArray?): CompoundTag {
        return readNetworkCompressed(data, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun readNetworkCompressed(data: ByteArray?, endianness: ByteOrder?): CompoundTag {
        ByteArrayInputStream(data).use { bytes -> GZIPInputStream(bytes).use { gzip -> BufferedInputStream(gzip).use { buffered -> return read(buffered, endianness, true) } } }
    }

    @Throws(IOException::class)
    fun write(tag: CompoundTag?): ByteArray {
        return write(tag, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun write(tag: CompoundTag?, endianness: ByteOrder?): ByteArray {
        return write(tag, endianness, false)
    }

    @Throws(IOException::class)
    fun write(tag: CompoundTag?, endianness: ByteOrder?, network: Boolean): ByteArray {
        return write(tag as Tag?, endianness, network)
    }

    @Throws(IOException::class)
    fun write(tag: Tag?, endianness: ByteOrder?, network: Boolean): ByteArray {
        val baos: FastByteArrayOutputStream = ThreadCache.fbaos.get().reset()
        NBTOutputStream(baos, endianness, network).use { stream ->
            Tag.writeNamedTag(tag, stream)
            return baos.toByteArray()
        }
    }

    @Throws(IOException::class)
    fun write(tags: Collection<CompoundTag?>?): ByteArray {
        return write(tags, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun write(tags: Collection<CompoundTag?>?, endianness: ByteOrder?): ByteArray {
        return write(tags, endianness, false)
    }

    @Throws(IOException::class)
    fun write(tags: Collection<CompoundTag?>, endianness: ByteOrder?, network: Boolean): ByteArray {
        val baos: FastByteArrayOutputStream = ThreadCache.fbaos.get().reset()
        NBTOutputStream(baos, endianness, network).use { stream ->
            for (tag in tags) {
                Tag.writeNamedTag(tag, stream)
            }
            return baos.toByteArray()
        }
    }

    @Throws(IOException::class)
    fun write(tag: CompoundTag?, file: File?) {
        write(tag, file, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun write(tag: CompoundTag?, file: File?, endianness: ByteOrder?) {
        FileOutputStream(file).use { outputStream -> write(tag, outputStream, endianness) }
    }

    @Throws(IOException::class)
    fun write(tag: CompoundTag?, outputStream: OutputStream?) {
        write(tag, outputStream, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun write(tag: CompoundTag?, outputStream: OutputStream?, endianness: ByteOrder?) {
        write(tag, outputStream, endianness, false)
    }

    @Throws(IOException::class)
    fun write(tag: CompoundTag?, outputStream: OutputStream?, endianness: ByteOrder?, network: Boolean) {
        Tag.writeNamedTag(tag, NBTOutputStream(outputStream, endianness, network))
    }

    @Throws(IOException::class)
    fun writeNetwork(tag: Tag?): ByteArray {
        val baos: FastByteArrayOutputStream = ThreadCache.fbaos.get().reset()
        NBTOutputStream(baos, ByteOrder.LITTLE_ENDIAN, true).use { stream -> Tag.writeNamedTag(tag, stream) }
        return baos.toByteArray()
    }

    @Throws(IOException::class)
    fun writeGZIPCompressed(tag: CompoundTag?): ByteArray {
        return writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun writeGZIPCompressed(tag: CompoundTag?, endianness: ByteOrder?): ByteArray {
        val baos: FastByteArrayOutputStream = ThreadCache.fbaos.get().reset()
        writeGZIPCompressed(tag, baos, endianness)
        return baos.toByteArray()
    }

    @Throws(IOException::class)
    fun writeGZIPCompressed(tag: CompoundTag?, outputStream: OutputStream?) {
        writeGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun writeGZIPCompressed(tag: CompoundTag?, outputStream: OutputStream?, endianness: ByteOrder?) {
        val gzip = PGZIPOutputStream(outputStream)
        write(tag, gzip, endianness)
        gzip.finish()
    }

    @Throws(IOException::class)
    fun writeNetworkGZIPCompressed(tag: CompoundTag?): ByteArray {
        return writeNetworkGZIPCompressed(tag, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun writeNetworkGZIPCompressed(tag: CompoundTag?, endianness: ByteOrder?): ByteArray {
        val baos: FastByteArrayOutputStream = ThreadCache.fbaos.get().reset()
        writeNetworkGZIPCompressed(tag, baos, endianness)
        return baos.toByteArray()
    }

    @Throws(IOException::class)
    fun writeNetworkGZIPCompressed(tag: CompoundTag?, outputStream: OutputStream?) {
        writeNetworkGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun writeNetworkGZIPCompressed(tag: CompoundTag?, outputStream: OutputStream?, endianness: ByteOrder?) {
        val gzip = PGZIPOutputStream(outputStream)
        write(tag, gzip, endianness, true)
        gzip.finish()
    }

    @Throws(IOException::class)
    fun writeZLIBCompressed(tag: CompoundTag?, outputStream: OutputStream?) {
        writeZLIBCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun writeZLIBCompressed(tag: CompoundTag?, outputStream: OutputStream?, endianness: ByteOrder?) {
        writeZLIBCompressed(tag, outputStream, Deflater.DEFAULT_COMPRESSION, endianness)
    }

    @Throws(IOException::class)
    fun writeZLIBCompressed(tag: CompoundTag?, outputStream: OutputStream?, level: Int) {
        writeZLIBCompressed(tag, outputStream, level, ByteOrder.BIG_ENDIAN)
    }

    @Throws(IOException::class)
    fun writeZLIBCompressed(tag: CompoundTag?, outputStream: OutputStream?, level: Int, endianness: ByteOrder?) {
        val out = DeflaterOutputStream(outputStream, Deflater(level))
        write(tag, out, endianness)
        out.finish()
    }

    @Throws(IOException::class)
    fun safeWrite(tag: CompoundTag?, file: File) {
        val tmpFile = File(file.getAbsolutePath().toString() + "_tmp")
        if (tmpFile.exists()) {
            tmpFile.delete()
        }
        write(tag, tmpFile)
        Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
    }
}