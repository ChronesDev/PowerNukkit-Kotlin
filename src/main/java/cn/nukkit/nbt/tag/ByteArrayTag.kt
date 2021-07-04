package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

class ByteArrayTag : Tag {
    var data: ByteArray?

    constructor(name: String?) : super(name) {}
    constructor(name: String?, data: ByteArray?) : super(name) {
        this.data = data
    }

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream) {
        if (data == null) {
            dos.writeInt(0)
            return
        }
        dos.writeInt(data!!.size)
        dos.write(data)
    }

    @Override
    @Throws(IOException::class)
    fun load(dis: NBTInputStream) {
        val length: Int = dis.readInt()
        data = ByteArray(length)
        dis.readFully(data)
    }

    override val id: Byte
        @Override get() = TAG_Byte_Array

    @Override
    override fun toString(): String {
        return "ByteArrayTag " + this.getName().toString() + " (data: 0x" + Binary.bytesToHexString(data, true).toString() + " [" + data!!.size.toString() + " bytes])"
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (super.equals(obj)) {
            val byteArrayTag = obj as ByteArrayTag
            return data == null && byteArrayTag.data == null || data != null && Arrays.equals(data, byteArrayTag.data)
        }
        return false
    }

    @Override
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + Arrays.hashCode(data)
        return result
    }

    @Override
    override fun copy(): Tag {
        val cp = ByteArray(data!!.size)
        System.arraycopy(data, 0, cp, 0, data!!.size)
        return ByteArrayTag(getName(), cp)
    }

    @Override
    override fun parseValue(): ByteArray? {
        return data
    }
}