package cn.nukkit.nbt.tag

import cn.nukkit.nbt.stream.NBTInputStream

class ByteTag : NumberTag<Integer?> {
    override var data = 0
    @Override
    fun getData(): Integer {
        return data
    }

    @Override
    fun setData(data: Integer?) {
        this.data = if (data == null) 0 else data
    }

    constructor(name: String?) : super(name) {}
    constructor(name: String?, data: Int) : super(name) {
        this.data = data
    }

    @Override
    @Throws(IOException::class)
    override fun write(dos: NBTOutputStream) {
        dos.writeByte(data)
    }

    @Override
    @Throws(IOException::class)
    fun load(dis: NBTInputStream) {
        data = dis.readByte().toInt()
    }

    override val id: Byte
        @Override get() = TAG_Byte

    @Override
    override fun parseValue(): Integer {
        return data
    }

    @Override
    override fun toString(): String {
        var hex: String = Integer.toHexString(data)
        if (hex.length() < 2) {
            hex = "0$hex"
        }
        return "ByteTag " + this.getName().toString() + " (data: 0x" + hex + ")"
    }

    @Override
    override fun equals(obj: Object): Boolean {
        if (super.equals(obj)) {
            val byteTag = obj as ByteTag
            return data == byteTag.data
        }
        return false
    }

    @Override
    override fun copy(): Tag {
        return ByteTag(getName(), data)
    }
}