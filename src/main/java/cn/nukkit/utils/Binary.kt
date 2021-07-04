package cn.nukkit.utils

import cn.nukkit.entity.Entity

/**
 * @author MagicDroidX (Nukkit Project)
 */
class Binary {
    fun unsignShort(value: Int): Int {
        return value and 0xffff
    }

    companion object {
        fun signByte(value: Int): Int {
            return value shl 56 shr 56
        }

        fun unsignByte(value: Int): Int {
            return value and 0xff
        }

        fun signShort(value: Int): Int {
            return value shl 48 shr 48
        }

        fun signInt(value: Int): Int {
            return value shl 32 shr 32
        }

        fun unsignInt(value: Int): Int {
            return value
        }

        //Triad: {0x00,0x00,0x01}<=>1
        fun readTriad(bytes: ByteArray): Int {
            return readInt(byteArrayOf(
                    0x00.toByte(),
                    bytes[0],
                    bytes[1],
                    bytes[2]
            ))
        }

        fun writeTriad(value: Int): ByteArray {
            return byteArrayOf(
                    (value ushr 16 and 0xFF).toByte(),
                    (value ushr 8 and 0xFF).toByte(),
                    (value and 0xFF).toByte()
            )
        }

        //LTriad: {0x01,0x00,0x00}<=>1
        fun readLTriad(bytes: ByteArray): Int {
            return readLInt(byteArrayOf(
                    bytes[0],
                    bytes[1],
                    bytes[2],
                    0x00.toByte()
            ))
        }

        fun writeLTriad(value: Int): ByteArray {
            return byteArrayOf(
                    (value and 0xFF).toByte(),
                    (value ushr 8 and 0xFF).toByte(),
                    (value ushr 16 and 0xFF).toByte()
            )
        }

        fun readUUID(bytes: ByteArray): UUID {
            return UUID(readLLong(bytes), readLLong(byteArrayOf(
                    bytes[8],
                    bytes[9],
                    bytes[10],
                    bytes[11],
                    bytes[12],
                    bytes[13],
                    bytes[14],
                    bytes[15]
            )))
        }

        fun writeUUID(uuid: UUID): ByteArray {
            return appendBytes(writeLLong(uuid.getMostSignificantBits()), *writeLLong(uuid.getLeastSignificantBits()))
        }

        fun writeMetadata(metadata: EntityMetadata): ByteArray {
            val stream = BinaryStream()
            val map: Map<Integer, EntityData> = metadata.getMap()
            stream.putUnsignedVarInt(map.size().toLong())
            for (id in map.keySet()) {
                val d: EntityData? = map[id]
                stream.putUnsignedVarInt(id.toLong())
                stream.putUnsignedVarInt(d.getType())
                when (d.getType()) {
                    Entity.DATA_TYPE_BYTE -> stream.putByte((d as ByteEntityData?).getData().byteValue())
                    Entity.DATA_TYPE_SHORT -> stream.putLShort((d as ShortEntityData?).getData())
                    Entity.DATA_TYPE_INT -> stream.putVarInt((d as IntEntityData?).getData())
                    Entity.DATA_TYPE_FLOAT -> stream.putLFloat((d as FloatEntityData?).getData())
                    Entity.DATA_TYPE_STRING -> {
                        val s: String = (d as StringEntityData?).getData()
                        stream.putUnsignedVarInt(s.getBytes(StandardCharsets.UTF_8).length)
                        stream.put(s.getBytes(StandardCharsets.UTF_8))
                    }
                    Entity.DATA_TYPE_NBT -> {
                        val slot: NBTEntityData? = d as NBTEntityData?
                        try {
                            stream.put(NBTIO.write(slot.getData(), ByteOrder.LITTLE_ENDIAN, true))
                        } catch (e: IOException) {
                            throw UncheckedIOException(e)
                        }
                    }
                    Entity.DATA_TYPE_POS -> {
                        val pos: IntPositionEntityData? = d as IntPositionEntityData?
                        stream.putVarInt(pos.x)
                        stream.putVarInt(pos.y)
                        stream.putVarInt(pos.z)
                    }
                    Entity.DATA_TYPE_LONG -> stream.putVarLong((d as LongEntityData?).getData())
                    Entity.DATA_TYPE_VECTOR3F -> {
                        val v3data: Vector3fEntityData? = d as Vector3fEntityData?
                        stream.putLFloat(v3data.x)
                        stream.putLFloat(v3data.y)
                        stream.putLFloat(v3data.z)
                    }
                }
            }
            return stream.getBuffer()
        }

        fun readMetadata(payload: ByteArray?): EntityMetadata {
            val stream = BinaryStream()
            stream.setBuffer(payload)
            val count: Long = stream.getUnsignedVarInt()
            val m = EntityMetadata()
            for (i in 0 until count) {
                val key = stream.getUnsignedVarInt() as Int
                val type = stream.getUnsignedVarInt() as Int
                var value: EntityData? = null
                when (type) {
                    Entity.DATA_TYPE_BYTE -> value = ByteEntityData(key, stream.getByte())
                    Entity.DATA_TYPE_SHORT -> value = ShortEntityData(key, stream.getLShort())
                    Entity.DATA_TYPE_INT -> value = IntEntityData(key, stream.getVarInt())
                    Entity.DATA_TYPE_FLOAT -> value = FloatEntityData(key, stream.getLFloat())
                    Entity.DATA_TYPE_STRING -> value = StringEntityData(key, stream.getString())
                    Entity.DATA_TYPE_NBT -> {
                        val offset: Int = stream.getOffset()
                        val fbais = FastByteArrayInputStream(stream.get())
                        try {
                            val tag: CompoundTag = NBTIO.read(fbais, ByteOrder.LITTLE_ENDIAN, true)
                            value = NBTEntityData(key, tag)
                        } catch (e: IOException) {
                            throw UncheckedIOException(e)
                        }
                        stream.setOffset(offset + fbais.position() as Int)
                    }
                    Entity.DATA_TYPE_POS -> {
                        val v3: BlockVector3 = stream.getSignedBlockPosition()
                        value = IntPositionEntityData(key, v3.x, v3.y, v3.z)
                    }
                    Entity.DATA_TYPE_LONG -> value = LongEntityData(key, stream.getVarLong())
                    Entity.DATA_TYPE_VECTOR3F -> value = Vector3fEntityData(key, stream.getVector3f())
                }
                if (value != null) m.put(value)
            }
            return m
        }

        fun readBool(b: Byte): Boolean {
            return b.toInt() == 0
        }

        fun writeBool(b: Boolean): Byte {
            return (if (b) 0x01 else 0x00).toByte()
        }

        fun readSignedByte(b: Byte): Int {
            return b and 0xFF
        }

        fun writeByte(b: Byte): Byte {
            return b
        }

        fun readShort(bytes: ByteArray): Int {
            return (bytes[0] and 0xFF shl 8) + (bytes[1] and 0xFF)
        }

        fun readSignedShort(bytes: ByteArray): Short {
            return readShort(bytes).toShort()
        }

        fun writeShort(s: Int): ByteArray {
            return byteArrayOf(
                    (s ushr 8 and 0xFF).toByte(),
                    (s and 0xFF).toByte()
            )
        }

        fun readLShort(bytes: ByteArray): Int {
            return (bytes[1] and 0xFF shl 8) + (bytes[0] and 0xFF)
        }

        fun readSignedLShort(bytes: ByteArray): Short {
            return readLShort(bytes).toShort()
        }

        fun writeLShort(s: Int): ByteArray {
            var s = s
            s = s and 0xffff
            return byteArrayOf(
                    (s and 0xFF).toByte(),
                    (s ushr 8 and 0xFF).toByte()
            )
        }

        fun readInt(bytes: ByteArray): Int {
            return (bytes[0] and 0xff shl 24) +
                    (bytes[1] and 0xff shl 16) +
                    (bytes[2] and 0xff shl 8) +
                    (bytes[3] and 0xff)
        }

        fun writeInt(i: Int): ByteArray {
            return byteArrayOf(
                    (i ushr 24 and 0xFF).toByte(),
                    (i ushr 16 and 0xFF).toByte(),
                    (i ushr 8 and 0xFF).toByte(),
                    (i and 0xFF).toByte()
            )
        }

        fun readLInt(bytes: ByteArray): Int {
            return (bytes[3] and 0xff shl 24) +
                    (bytes[2] and 0xff shl 16) +
                    (bytes[1] and 0xff shl 8) +
                    (bytes[0] and 0xff)
        }

        fun writeLInt(i: Int): ByteArray {
            return byteArrayOf(
                    (i and 0xFF).toByte(),
                    (i ushr 8 and 0xFF).toByte(),
                    (i ushr 16 and 0xFF).toByte(),
                    (i ushr 24 and 0xFF).toByte()
            )
        }

        @JvmOverloads
        fun readFloat(bytes: ByteArray, accuracy: Int = -1): Float {
            val `val`: Float = Float.intBitsToFloat(readInt(bytes))
            return if (accuracy > -1) {
                NukkitMath.round(`val`, accuracy)
            } else {
                `val`
            }
        }

        fun writeFloat(f: Float): ByteArray {
            return writeInt(Float.floatToIntBits(f))
        }

        @JvmOverloads
        fun readLFloat(bytes: ByteArray, accuracy: Int = -1): Float {
            val `val`: Float = Float.intBitsToFloat(readLInt(bytes))
            return if (accuracy > -1) {
                NukkitMath.round(`val`, accuracy)
            } else {
                `val`
            }
        }

        fun writeLFloat(f: Float): ByteArray {
            return writeLInt(Float.floatToIntBits(f))
        }

        fun readDouble(bytes: ByteArray): Double {
            return Double.longBitsToDouble(readLong(bytes))
        }

        fun writeDouble(d: Double): ByteArray {
            return writeLong(Double.doubleToLongBits(d))
        }

        fun readLDouble(bytes: ByteArray): Double {
            return Double.longBitsToDouble(readLLong(bytes))
        }

        fun writeLDouble(d: Double): ByteArray {
            return writeLLong(Double.doubleToLongBits(d))
        }

        fun readLong(bytes: ByteArray): Long {
            return (bytes[0].toLong() shl 56) +
                    ((bytes[1] and 0xFF) as Long shl 48) +
                    ((bytes[2] and 0xFF) as Long shl 40) +
                    ((bytes[3] and 0xFF) as Long shl 32) +
                    ((bytes[4] and 0xFF) as Long shl 24) +
                    (bytes[5] and 0xFF shl 16) +
                    (bytes[6] and 0xFF shl 8) +
                    (bytes[7] and 0xFF)
        }

        fun writeLong(l: Long): ByteArray {
            return byteArrayOf(
                    (l ushr 56).toByte(),
                    (l ushr 48).toByte(),
                    (l ushr 40).toByte(),
                    (l ushr 32).toByte(),
                    (l ushr 24).toByte(),
                    (l ushr 16).toByte(),
                    (l ushr 8).toByte(),
                    l.toByte()
            )
        }

        fun readLLong(bytes: ByteArray): Long {
            return (bytes[7].toLong() shl 56) +
                    ((bytes[6] and 0xFF) as Long shl 48) +
                    ((bytes[5] and 0xFF) as Long shl 40) +
                    ((bytes[4] and 0xFF) as Long shl 32) +
                    ((bytes[3] and 0xFF) as Long shl 24) +
                    (bytes[2] and 0xFF shl 16) +
                    (bytes[1] and 0xFF shl 8) +
                    (bytes[0] and 0xFF)
        }

        fun writeLLong(l: Long): ByteArray {
            return byteArrayOf(
                    l.toByte(),
                    (l ushr 8).toByte(),
                    (l ushr 16).toByte(),
                    (l ushr 24).toByte(),
                    (l ushr 32).toByte(),
                    (l ushr 40).toByte(),
                    (l ushr 48).toByte(),
                    (l ushr 56).toByte())
        }

        fun writeVarInt(v: Int): ByteArray {
            val stream = BinaryStream()
            stream.putVarInt(v)
            return stream.getBuffer()
        }

        fun writeUnsignedVarInt(v: Long): ByteArray {
            val stream = BinaryStream()
            stream.putUnsignedVarInt(v)
            return stream.getBuffer()
        }

        fun reserveBytes(bytes: ByteArray): ByteArray {
            val newBytes = ByteArray(bytes.size)
            for (i in bytes.indices) {
                newBytes[bytes.size - 1 - i] = bytes[i]
            }
            return newBytes
        }

        fun bytesToHexString(src: ByteArray?): String? {
            return bytesToHexString(src, false)
        }

        fun bytesToHexString(src: ByteArray?, blank: Boolean): String? {
            val stringBuilder = StringBuilder()
            if (src == null || src.size <= 0) {
                return null
            }
            for (b in src) {
                if (!(stringBuilder.length() === 0) && blank) {
                    stringBuilder.append(" ")
                }
                val v: Int = b and 0xFF
                val hv: String = Integer.toHexString(v)
                if (hv.length() < 2) {
                    stringBuilder.append(0)
                }
                stringBuilder.append(hv)
            }
            return stringBuilder.toString().toUpperCase()
        }

        fun hexStringToBytes(hexString: String?): ByteArray? {
            var hexString = hexString
            if (hexString == null || hexString.equals("")) {
                return null
            }
            val str = "0123456789ABCDEF"
            hexString = hexString.toUpperCase().replace(" ", "")
            val length: Int = hexString.length() / 2
            val hexChars: CharArray = hexString.toCharArray()
            val d = ByteArray(length)
            for (i in 0 until length) {
                val pos: Int = i * 2
                d[i] = (str.indexOf(hexChars[pos]) as Byte shl 4 or str.indexOf(hexChars[pos + 1]) as Byte) as Byte
            }
            return d
        }

        @JvmOverloads
        fun subBytes(bytes: ByteArray, start: Int, length: Int = bytes.size - start): ByteArray {
            val len: Int = Math.min(bytes.size, start + length)
            return Arrays.copyOfRange(bytes, start, len)
        }

        fun splitBytes(bytes: ByteArray, chunkSize: Int): Array<ByteArray> {
            val splits = Array((bytes.size + chunkSize - 1) / chunkSize) { ByteArray(chunkSize) }
            var chunks = 0
            var i = 0
            while (i < bytes.size) {
                if (bytes.size - i > chunkSize) {
                    splits[chunks] = Arrays.copyOfRange(bytes, i, i + chunkSize)
                } else {
                    splits[chunks] = Arrays.copyOfRange(bytes, i, bytes.size)
                }
                chunks++
                i += chunkSize
            }
            return splits
        }

        fun appendBytes(bytes: Array<ByteArray>): ByteArray {
            var length = 0
            for (b in bytes) {
                length += b.size
            }
            val appendedBytes = ByteArray(length)
            var index = 0
            for (b in bytes) {
                System.arraycopy(b, 0, appendedBytes, index, b.size)
                index += b.size
            }
            return appendedBytes
        }

        fun appendBytes(byte1: Byte, vararg bytes2: ByteArray): ByteArray {
            var length = 1
            for (bytes in bytes2) {
                length += bytes.size
            }
            val buffer: ByteBuffer = ByteBuffer.allocate(length)
            buffer.put(byte1)
            for (bytes in bytes2) {
                buffer.put(bytes)
            }
            return buffer.array()
        }

        fun appendBytes(bytes1: ByteArray, vararg bytes2: ByteArray): ByteArray {
            var length = bytes1.size
            for (bytes in bytes2) {
                length += bytes.size
            }
            val appendedBytes = ByteArray(length)
            System.arraycopy(bytes1, 0, appendedBytes, 0, bytes1.size)
            var index = bytes1.size
            for (b in bytes2) {
                System.arraycopy(b, 0, appendedBytes, index, b.size)
                index += b.size
            }
            return appendedBytes
        }
    }
}